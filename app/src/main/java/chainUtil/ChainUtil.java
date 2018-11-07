package chainUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import core.blockchain.Block;
import core.blockchain.BlockBody;
import core.connection.BlockJDBCDAO;

public class ChainUtil {
    private static ChainUtil chainUtil;

    //change to private after changes
    public ChainUtil() {}

    public static ChainUtil getInstance() {
        if (chainUtil == null) {
            chainUtil = new ChainUtil();
        }
        return chainUtil;
    }

    public static String digitalSignature(String data) {
        Signature dsa = null;
        String signature = null;
        try {
            dsa = Signature.getInstance("SHA1withDSA");
            dsa.initSign(KeyGenerator.getInstance().getPrivateKey());
            byte[] byteArray = data.getBytes();
            dsa.update(byteArray);
            signature = bytesToHex(dsa.sign());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return signature;
    }

    public static boolean signatureVerification(String publicKey, String signature, String data) {
        return verify(KeyGenerator.getInstance().getPublicKey(publicKey),hexStringToByteArray(signature),data);
    }

    public static byte[] sign(PrivateKey privateKey, String data) throws SignatureException {
        //sign the data
        Signature dsa = null;
        try {
            dsa = Signature.getInstance("SHA1withDSA");
            dsa.initSign(privateKey);
            byte[] byteArray = data.getBytes();
            dsa.update(byteArray);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return dsa.sign();
    }

    public static boolean verify(PublicKey publicKey, byte[] signature, String data) {
        Signature sig = null;
        boolean verification = false;
        try {
            sig = Signature.getInstance("SHA1withDSA");
            sig.initVerify(publicKey);
            sig.update(data.getBytes(),0,data.getBytes().length);
            verification = sig.verify(signature);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return verification;
    }

//    public publicKeyEncryption() {
//
//    }

    public static byte[] getHashByteArray(String data) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return digest.digest(data.getBytes(StandardCharsets.UTF_8));
    }

    public static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String getHash(String data) {
        return bytesToHex(getHashByteArray(data));
    }

    public String getBlockHash(Block block) {
        Gson gson = (new GsonBuilder()).create();
        JSONObject jsonBlock = null;
        try {
            jsonBlock = new JSONObject(gson.toJson(block));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getHash((jsonBlock.toString()));
    }

    public static String getBlockHash(BlockBody blockBody) {
        Gson gson = (new GsonBuilder()).create();
        JSONObject jsonBlock = null;
        try {
            jsonBlock = new JSONObject(gson.toJson(blockBody));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getHash((jsonBlock.toString()));
    }

    public JSONObject getBlockchain(int from) throws Exception {
        BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
        //ResultSet rs = blockJDBCDAO.getBlockchain(from);
        //return convertResultSetIntoJSON(rs);
 return blockJDBCDAO.getBlockchain(from);
    }

    public static JSONObject getJsonBlock(Block block) {
        Gson gson = (new GsonBuilder()).create();
        JSONObject jsonBlock = null;
        try {
            jsonBlock = new JSONObject(gson.toJson(block));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonBlock;
    }

    public JSONObject convertResultSetIntoJSON(ResultSet resultSet) throws Exception {
        JSONObject result = new JSONObject();
        int count = 0;

        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            count++;
            int total_rows = resultSet.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();

            for (int i = 0; i < total_rows; i++) {
                String columnName = resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase();
                Object columnValue = resultSet.getObject(i + 1);
                // if value in DB is null, then we set it to default value
                if (columnValue == null){
                    columnValue = "null";
                }
                /*
                Next if block is a hack. In case when in db we have values like price and price1 there's a bug in jdbc -
                both this names are getting stored as price in ResulSet. Therefore when we store second column value,
                we overwrite original value of price. To avoid that, i simply add 1 to be consistent with DB.
                 */
                if (obj.has(columnName)){
                    columnName += "1";
                }
                obj.put(columnName, columnValue);
            }
            jsonArray.put(obj);
        }
        result.put("blockchainSize", count);
        result.put("blockchain", jsonArray.toString());
        return result;
    }

    public boolean verifyUser(String peerID, String publicKey) {
        if(peerID.equals(publicKey.substring(0,40))) {
            return true;
        }
        return false;
    }

    public static String getPreviousHash() {
        return "b9a9c304e9313a6c9d1c5a3a2f3fcd41ae3aa56964f9a3186379d6477500583e";
    }

    public long getRecentBlockNumber() {
        return 104;
    }

    public static Timestamp convertStringToTimestamp(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.hh.mm.ss");
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new java.sql.Timestamp(parsedDate.getTime());
    }

    public static String getNodeIdUsingPk(String publicKey) {
        return publicKey.substring(publicKey.length()-40);
    }
}
