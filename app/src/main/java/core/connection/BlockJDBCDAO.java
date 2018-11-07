package core.connection;

import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.camera2.params.BlackLevelPattern;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import HelperInterface.AsyncResponse;
import chainUtil.ChainUtil;
import core.blockchain.BlockInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BlockJDBCDAO implements AsyncResponse {
    APICaller apiCaller = new APICaller();
    final static String base_url = "http://192.168.8.102:8080/";

    JSONArray jsonArray;

    public boolean addBlockToBlockchain(BlockInfo blockInfo, Identity identity) throws SQLException {
        System.out.println("inside BlockJDBCDAO/addBlockToBlockchain()");

        String transactionId = blockInfo.getTransactionId();
        String transactionType = transactionId.substring(0, 1);

        apiCaller.delegate = this;

        if (transactionType.equals("I")) {
            apiCaller.execute(base_url+"insertInToIdentityTable.php", "GET", "Identity", identity);
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
            if(blockInfo.isValidity()) {
                validity = 1;
            }
            apiCaller.execute(base_url+"insertblock"+
                    "?previous_hash=" +blockInfo.getPreviousHash() +
                    "&block_hash=" + blockInfo.getHash() +
                    "&block_timestamp="+ blockInfo.getBlockTime() + "&block_number=" + validity +
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
        } finally {

            return true;
        }

    }

    public void saveBlockchain(JSONArray blockchain) throws SQLException {
        System.out.println("inside BlockJDBCDAO/addBlockToBlockchain()");
        final int batchSize = blockchain.length();
        int count = 0;
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
                    if(blockInfo.isValidity()) {
                        validity = 1;
                    }
                    apiCaller.execute(base_url+"insertblock"+
                            "?previous_hash=" +blockInfo.getPreviousHash() +
                            "&block_hash=" + blockInfo.getHash() +
                            "&block_timestamp="+ blockInfo.getBlockTime() + "&block_number=" + validity +
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
        apiCaller.delegate = this;

        try {

            apiCaller.execute(base_url+"blockinfo?block_number=" + blockNumber, "GET", "v", "g");

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

        apiCaller.delegate = this;

        try {

            apiCaller.execute(base_url+"blockinfo?block_number=", "GET", "v", "g");

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
                    apiCaller.execute(base_url+"insertInToIdentityTable.php", "POST", "Identity", identity);
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

        int blockchainSize = 0;
        apiCaller.delegate = this;

        try {

            apiCaller.execute(base_url+"blockinfo?block_number=", "GET", "v", "g");

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
        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url+"blockinfo?block_number=", "GET", "v", "g");

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
        apiCaller.delegate = this;
        long blockNumber = 0;

        try {

            apiCaller.execute(base_url+"blockinfo?block_number=", "GET", "v", "g");

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
            return (long) jsonArray.get(0);
        }


    public JSONObject getPreviousBlockData() throws SQLException, JSONException {

        JSONObject previousBlock = new JSONObject();
        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url+"blockinfo?block_number=", "GET", "v", "g");

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

    public String getPreviousHash() throws SQLException, JSONException {

        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url+"blockinfo?block_number=", "GET", "v", "g");

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
            return jsonArray.getString(0);
        }



    @Override
    public JSONArray processFinish(JSONArray output) {
        System.out.println("process finish executed");
        this.jsonArray = output;
        return output;
    }
}
