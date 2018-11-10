package core.connection;

import android.app.Application;
import android.os.AsyncTask;

import com.example.madhushika.carbc_android_v3.MainActivity;
import com.example.madhushika.carbc_android_v3.NavigationHandler;

import HelperInterface.AsyncResponse;
import chainUtil.ChainUtil;
import chainUtil.KeyGenerator;
import core.blockchain.BlockInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.sql.*;


public class BlockJDBCDAO implements AsyncResponse {
    final static String base_url = "http://192.168.8.103:8080/";

    JSONArray jsonArray;

    public boolean addBlockToBlockchain(BlockInfo blockInfo, Identity identity) throws SQLException {
        System.out.println("inside BlockJDBCDAO/addBlockToBlockchain()");
        String transactionId = blockInfo.getTransactionId();
        String transactionType = transactionId.substring(0, 1);

        APICaller apiCaller = new APICaller();
        APICaller apiCaller1 = new APICaller();
        APICaller apiCaller2 = new APICaller();
        APICaller apiCaller3 = new APICaller();

        apiCaller.delegate = this;
        apiCaller1.delegate = this;
        apiCaller2.delegate = this;
        apiCaller3.delegate = this;

        if (transactionType.equals("I")) {
            apiCaller.execute(base_url + "insertidentity?block_hash=" + identity.getBlock_hash() +
                    "&public_key" + identity.getPublic_key() +
                    "&role=" + identity.getRole() +
                    "&name=" + identity.getName()+
                    "location" + identity.getLocation(), "GET", "Identity", identity);
            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            int validity = 0;
            if (blockInfo.isValidity()) {
                validity = 1;
            }
            apiCaller1.execute(base_url + "insertblock" +
                    "?previous_hash=" + URLEncoder.encode(blockInfo.getPreviousHash(), "UTF-8") +
                    "&block_hash=" + URLEncoder.encode(blockInfo.getHash(), "UTF-8") +
                    "&block_timestamp=" + URLEncoder.encode(blockInfo.getBlockTimeAsString(), "UTF-8").replace("+", "%20") +
                    "&block_number=" + URLEncoder.encode(String.valueOf(blockInfo.getBlockNumber()), "UTF-8") +
                    "&validity=" + URLEncoder.encode(String.valueOf(validity), "UTF-8") +
                    "&transaction_id=" + URLEncoder.encode(blockInfo.getTransactionId(), "UTF-8") +
                    "&sender=" + URLEncoder.encode(blockInfo.getSender(), "UTF-8") +
                    "&event=" + URLEncoder.encode(blockInfo.getEvent(), "UTF-8") +
                    "&data=" + URLEncoder.encode(blockInfo.getData(), "UTF-8") +
                    "&address=" + URLEncoder.encode(blockInfo.getAddress(), "UTF-8" )+
                    "&rating=" + URLEncoder.encode(String.valueOf(blockInfo.getRating()), "UTF-8"
            ), "GET", "BlockInfo", blockInfo);

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (blockInfo.getEvent().equals("ExchangeOwnership")){
                String data = blockInfo.getData();
                JSONObject object = new JSONObject(data);
                apiCaller2.execute(base_url + "updatevehicle?current_owner=" + URLEncoder.encode(object.getJSONObject("SecondaryParty").getJSONObject("NewOwner").getString("publicKey"), "UTF-8")+
                        "&vehicle_id=" + URLEncoder.encode(blockInfo.getAddress(), "UTF-8" ) , "GET", "Identity", identity);
                while (jsonArray == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(blockInfo.getEvent().equals("RegisterVehicle")){
                String data = blockInfo.getData();
                JSONObject object = new JSONObject(data);
                apiCaller3.execute(base_url + "insertintovehicle?registration_number=" + URLEncoder.encode(object.getString("registrationNumber"), "UTF-8" )+
                        "&vehicle_id=" + URLEncoder.encode(blockInfo.getAddress(), "UTF-8" ) +
                                "&current_owner=" + URLEncoder.encode(object.getString("currentOwner"), "UTF-8" ), "GET", "Identity", "v");
                while (jsonArray == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                VehicleJDBCDAO vehicleJDBCDAO = new VehicleJDBCDAO();
                MainActivity.vehicle_numbers = vehicleJDBCDAO.getRegistrationNumbers(KeyGenerator.getInstance().getPublicKeyAsString());

                NavigationHandler.navigateTo("addtransactionFragment");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            return true;
        }

    }

    public void saveBlockchain(JSONArray blockchain) throws SQLException {
        System.out.println("inside BlockJDBCDAO/addBlockToBlockchain()");
        final int batchSize = blockchain.length();
        int count = 0;
        jsonArray = null;

        APICaller apiCaller = new APICaller();
        apiCaller.delegate = this;

        for (int i = 0; i < batchSize; i++) {
            try {
                JSONObject block = blockchain.getJSONObject(i);
                BlockInfo blockInfo = new BlockInfo();
                blockInfo.setTransactionId(block.getString("transaction_id"));
                blockInfo.setPreviousHash(block.getString("previous_hash"));
                blockInfo.setHash(block.getString("block_hash"));
                blockInfo.setBlockTime(ChainUtil.convertStringToTimestamp2(block.getString("block_timestamp")));
                blockInfo.setBlockNumber(block.getLong("block_number"));
                blockInfo.setValidity(true);
                blockInfo.setSender(block.getString("sender"));
                blockInfo.setEvent(block.getString("event"));
                blockInfo.setData(block.getString("data"));

                try {
                    int validity = 0;
                    if (blockInfo.isValidity()) {
                        validity = 1;
                    }
                    apiCaller.execute(base_url + "insertblock" +
                            "?previous_hash=" + blockInfo.getPreviousHash() +
                            "&block_hash=" + blockInfo.getHash() +
                            "&block_timestamp=" + blockInfo.getBlockTime() + "&block_number=" + validity +
                            "&transaction_id=" + blockInfo.getTransactionId() +
                            "&sender=" + blockInfo.getSender() +
                            "&event=" + blockInfo.getEvent() +
                            "&data=" + blockInfo.getData() +
                            "&address=" + blockInfo.getAddress(), "GET", "BlockInfo", blockInfo);
                    while (jsonArray == null) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Block is Added Successfully");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject getBlockchain(long blockNumber) throws SQLException {
        APICaller apiCaller = new APICaller();
        jsonArray = null;
        apiCaller.delegate = this;

        try {

            apiCaller.execute(base_url + "blockinfo?block_number=" + blockNumber, "GET", "v", "g");

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


        JSONObject object = new JSONObject();
        //should change this
        return object;
    }


    public static JSONObject convertResultSetIntoJSON(ResultSet resultSet) throws Exception {
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
                if (columnValue == null) {
                    columnValue = "null";
                }
                /*
                Next if block is a hack. In case when in db we have values like price and price1 there's a bug in jdbc -
                both this names are getting stored as price in ResulSet. Therefore when we store second column value,
                we overwrite original value of price. To avoid that, i simply add 1 to be consistent with DB.
                 */
                if (obj.has(columnName)) {
                    columnName += "1";
                }

                obj.put(columnName, columnValue);
            }
            jsonArray.put(obj);
        }
        result.put("blockchainLength", count);
        result.put("blockchain", jsonArray);
        return result;
    }


    //get an identity related transactions
    public void updateIdentityTableAtBlockchainReceipt() throws SQLException {
        APICaller apiCaller = new APICaller();
        APICaller apiCaller1 = new APICaller();

        jsonArray = null;

        apiCaller.delegate = this;
        apiCaller1.delegate = this;

        try {

            apiCaller.execute(base_url + "blockinfo?block_number=", "GET", "v", "g");

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int count = jsonArray.length();

        for (int i = 0; i < count; i++) {
            try {
                JSONObject block = jsonArray.getJSONObject(i);
                Identity identity = new Identity(block.getString("block_hash"), block.getString("public_key"),
                        block.getString("name"), block.getString("role"), block.getString("location"));
                try {
                    apiCaller1.execute(base_url + "insertidentity?block_hash=" + identity.getBlock_hash() +
                            "&public_key" + identity.getPublic_key() +
                            "&role=" + identity.getRole() +
                            "&name=" + identity.getName()+
                            "location" + identity.getLocation(), "GET", "Identity", identity);
                    while (jsonArray == null) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // *************************************************************
    public int getBLockchainSize() throws SQLException {
        APICaller apiCaller = new APICaller();
        jsonArray = null;

        int blockchainSize = 0;
        apiCaller.delegate = this;

        try {

            apiCaller.execute(base_url + "blockinfo?block_number=", "GET", "v", "g");

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //blockchainSize = (int)jsonArray.getJSONObject(0);


        String queryString = "SELECT COUNT(id) AS size FROM `Blockchain` WHERE validity = '1'";

        return blockchainSize;
    }

    public JSONObject getVehicleInfoByEvent(String vehicleId, String event) throws SQLException, JSONException {
        APICaller apiCaller = new APICaller();
        jsonArray = null;

        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url + "getVehicleDetails?event=" + event +
                    "&address=" + vehicleId, "GET", "v", "g");

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray.getJSONObject(0);
    }


    public ResultSet getCompleteVehicleInfo(String vehicleId) throws SQLException {
//        Connection connection = null;
//        PreparedStatement ptmt = null;
        ResultSet resultSet = null;
//
//        String queryString = "SELECT `previous_hash`, `block_hash`, `block_timestamp`, " +
//                "`block_number`, `transaction_id`, `sender`, `event`, `data`, `address` " +
//                "FROM `Blockchain` WHERE `transaction_id` LIKE ? AND `event` = ? AND " +
//                "`address` >=  AND `validity` = `T`";
//
//        try {
//            connection = ConnectionFactory.getInstance().getConnection();
//            ptmt = connection.prepareStatement(queryString);
//            ptmt.setString(1, "V");
//            ptmt.setString(2, vehicleId);
//            ptmt.setString(3, "T");
//            resultSet = ptmt.executeQuery();
//
//            if (resultSet.next()){
//
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (resultSet != null)
//                resultSet.close();
//            if (ptmt != null)
//                ptmt.close();
//            if (connection != null)
//                connection.close();
        return resultSet;
//        }
    }

    public long getRecentBlockNumber() throws SQLException, JSONException {
        // String queryString = "SELECT `block_number` FROM Blockchain ORDER BY id DESC LIMIT 1";
        APICaller apiCaller = new APICaller();
        jsonArray = null;
        apiCaller.delegate = this;
        long blockNumber = 0;

        try {

            apiCaller.execute(base_url + "findRecentblocknumber", "GET", "v", "g");

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonArray.getBoolean(0)) {
            JSONArray array = jsonArray.getJSONArray(1);
            blockNumber = array.getInt(0);

        }
        return blockNumber;
    }


    public JSONObject getPreviousBlockData() throws SQLException, JSONException {
        APICaller apiCaller = new APICaller();
        jsonArray = null;

        JSONObject previousBlock = new JSONObject();
        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url + "blockinfo?block_number=", "GET", "v", "g");

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonArray.getBoolean(0)) {
            JSONArray array = jsonArray.getJSONArray(1);
            previousBlock = array.getJSONObject(0);
        }

        return previousBlock;
    }

    public String getPreviousHash() throws SQLException, JSONException {
        APICaller apiCaller = new APICaller();
        jsonArray = null;
        String previousHash = null;
        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url + "findprevioushash", "GET", "v", "g");

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonArray.getBoolean(0)) {
            JSONArray array = jsonArray.getJSONArray(1);
            previousHash = array.getString(0);
        }
        return previousHash;
    }


    public JSONObject getRegistrationInfoByRegistrationNumber(String registrationNumber) {

        APICaller apiCaller1 = new APICaller();
        jsonArray = null;
        JSONObject finalObject = new JSONObject();
        apiCaller1.delegate = this;
        try {

            apiCaller1.execute(base_url + "searchvehicleregistrationdata" + "?registration_number=" + registrationNumber, "GET", "v", "g");

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (jsonArray.getBoolean(0)) {
                try {
                    JSONArray array = jsonArray.getJSONArray(1);
                    JSONObject object = array.getJSONObject(0);

                    finalObject.put("status", true);
                    JSONObject data = new JSONObject(object.getString("data"));
                    JSONObject vehicleInfo = new JSONObject();

                    vehicleInfo.put("current_owner", object.getString("current_owner"));
                    vehicleInfo.put("engine_number", data.getString("engine_number"));
                    vehicleInfo.put("vehicle_class", data.getString("vehicle_class"));
                    vehicleInfo.put("condition_and_note", data.getString("condition_and_note"));
                    vehicleInfo.put("make", data.getString("make"));
                    vehicleInfo.put("model", data.getString("model"));
                    vehicleInfo.put("year_of_manufacture", data.getString("year_of_manufacture"));
                    vehicleInfo.put("registration_number", data.getString("registration_number"));
                    vehicleInfo.put("rating", object.getDouble("rating"));
                    vehicleInfo.put("address", object.getString("address"));
                    finalObject.put("data", vehicleInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                finalObject.put("status", false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(finalObject);

        return finalObject;
    }

    public JSONArray getVehicleInfoByRegistrationNumber(String registrationNumber) {

        APICaller apiCaller2 = new APICaller();
        apiCaller2.delegate = this;
        JSONArray array = new JSONArray();
        jsonArray = null;
        try {
            apiCaller2.execute(base_url + "searchvehicledata" + "?registration_number=" + registrationNumber, "GET", "v", "g");
//            apiCaller.execute(base_url + "searchvehicledata" + "?registration_number=" +registrationNumber, "GET", "v", "g");

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (jsonArray.getBoolean(0)) {
                //JSONArray newArry = new JSONArray();
                array = jsonArray.getJSONArray(1);
                //array = newArry.getJSONArray(0);
            }

            System.out.println(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }


    @Override
    public JSONArray processFinish(JSONArray output) {
        System.out.println("process finish executed");
        jsonArray = new JSONArray();

        this.jsonArray = output;
        System.out.println(jsonArray);

        return jsonArray;
    }
}
