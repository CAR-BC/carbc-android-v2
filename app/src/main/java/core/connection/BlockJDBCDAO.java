package core.connection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
    public boolean addBlockToBlockchain(BlockInfo blockInfo, Identity identity) throws SQLException {
        System.out.println("inside BlockJDBCDAO/addBlockToBlockchain()");

        Connection connection = null;
        PreparedStatement ptmt = null;
        PreparedStatement psmt = null;
        ResultSet resultSet = null;

        String transactionId = blockInfo.getTransactionId();
        String transactionType = transactionId.substring(0, 1);
        String query = "";

        if (transactionType.equals("I")){
            query = "INSERT INTO `Identity`(`block_hash`, `role`, `name`) " +
                    "VALUES (?,?,?,?)";
        }

        try {
            String queryString = "INSERT INTO `Blockchain`(`previous_hash`, " +
                    "`block_hash`, `block_timestamp`, `block_number`, `validity`," +
                    " `transaction_id`, `sender`, `event`, `data`, `address`) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?)";

            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);

            ptmt.setString(1, blockInfo.getPreviousHash());
            ptmt.setString(2, blockInfo.getHash());
            ptmt.setTimestamp(3, blockInfo.getBlockTime());
            ptmt.setLong(4, blockInfo.getBlockNumber());
            ptmt.setBoolean(5, blockInfo.isValidity());
            ptmt.setString(6, blockInfo.getTransactionId());
            ptmt.setString(7, blockInfo.getSender());
            ptmt.setString(8, blockInfo.getEvent());
            ptmt.setString(9, blockInfo.getData());
            ptmt.setString(10, blockInfo.getAddress());
            ptmt.executeUpdate();

            if (transactionType.equals("I")){
                psmt = connection.prepareStatement(query);
                psmt.setString(1, identity.getBlock_hash());
                psmt.setString(2, identity.getPublic_key());
                psmt.setString(3, identity.getRole());
                psmt.setString(4, identity.getName());
                psmt.executeUpdate();
            }

            System.out.println("Block is Added Successfully");

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (ptmt != null)
                ptmt.close();
            if (psmt != null)
                psmt.close();
            if (connection != null)
                connection.close();
            return true;
        }

    }

    public void saveBlockchain(JSONArray blockchain) throws SQLException {
        System.out.println("inside BlockJDBCDAO/addBlockToBlockchain()");

        Connection connection = null;
        PreparedStatement ptmt = null;
        PreparedStatement psmt = null;
        ResultSet resultSet = null;

        final int batchSize = blockchain.length();
        int count = 0;

        try {
            String queryString = "INSERT INTO `Blockchain`(`previous_hash`, " +
                    "`block_hash`, `block_timestamp`, `block_number`, `validity`," +
                    " `transaction_id`, `sender`, `event`, `data`, `address`) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?)";

            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);

            for (int i = 0; i < blockchain.length(); i++){
                try{
                    JSONObject block = blockchain.getJSONObject(i);
                    ptmt.setString(1, block.getString("previous_hash"));
                    ptmt.setString(2, block.getString("block_hash"));
                    System.out.println(block.get("block_timestamp"));

                    ptmt.setTimestamp(3, ChainUtil.convertStringToTimestamp2(block.getString("block_timestamp")));

//                ptmt.setLong(4, Long.valueOf(block.getString("block_number")));

//                ptmt.setTimestamp(3, (Timestamp) block.get("block_timestamp"));
                    ptmt.setLong(4, block.getLong("block_number"));
                    ptmt.setBoolean(5, true);
                    ptmt.setString(6, block.getString("transaction_id"));
                    ptmt.setString(7, block.getString("sender"));
                    ptmt.setString(8, block.getString("event"));

                    String data = block.getString("data");
                    String jsonFormattedString = data.replaceAll("\\\\", "");

                    ptmt.setString(9, jsonFormattedString);
                    ptmt.setString(10, block.getString("address"));

                    ptmt.addBatch();

                    if(++count % batchSize == 0) {
                        ptmt.executeBatch();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
            ptmt.executeBatch();

            System.out.println("Block is Added Successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ptmt != null)
                ptmt.close();
            if (psmt != null)
                psmt.close();
            if (connection != null)
                connection.close();
        }
    }

    public JSONObject getBlockchain(long blockNumber) throws SQLException {
        Connection connection = null;
        PreparedStatement ptmt = null;
        ResultSet resultSet = null;
        JSONObject convertedResultSet = null;

        String queryString = "SELECT `previous_hash`, `block_hash`, `block_timestamp`, " +
                "`block_number`, `transaction_id`, `sender`, `event`, `data`, `address` " +
                "FROM `Blockchain` WHERE `block_number` > ? AND `validity` = 1";
        String blockchain = "";

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setLong(1, blockNumber);
            resultSet = ptmt.executeQuery();
            convertedResultSet = convertResultSetIntoJSON(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ptmt != null)
                ptmt.close();
            if (resultSet != null)
                resultSet.close();
            if (connection != null)
                connection.close();
            return convertedResultSet;
        }
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
        result.put("blockchainLength", count);
        result.put("blockchain", jsonArray);
        return result;
    }


    //get an identity related transactions
    public void updateIdentityTableAtBlockchainReceipt() throws SQLException {
        Connection connection = null;
        PreparedStatement ptmt = null;
        ResultSet resultSet = null;

        String query = "SELECT `data` FROM `Blockchain` WHERE `address` LIKE 'I%'";
        String queryForIdentity = "INSERT INTO `Identity`(`block_hash`, `role`, `name`) VALUES (?,?,?)";
        JSONObject identity = null;

        String block_hash = null;
        String role = null;
        String name = null;

        try {
            connection = ConnectionFactory.getInstance().getConnection();

            Statement st = connection.createStatement();
            resultSet = st.executeQuery(query);

            ptmt = connection.prepareStatement(queryForIdentity);

            if (resultSet.next()){
                block_hash = resultSet.getString("block_hash");
                role = resultSet.getString("role");
                name = resultSet.getString("name");

                ptmt.setString(1, block_hash);
                ptmt.setString(1, role);
                ptmt.setString(1, name);
                ptmt.executeQuery();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null)
                resultSet.close();
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
        }
    }

    public int getBLockchainSize() throws SQLException {
        Connection connection = null;
        PreparedStatement ptmt = null;
        ResultSet resultSet = null;
        int blockchainSize = 0;

        String queryString = "SELECT COUNT(id) AS size FROM `Blockchain` WHERE validity = '1'";

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);
            resultSet = ptmt.executeQuery();

            if (resultSet.next()){
                blockchainSize = resultSet.getInt("size");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null)
                resultSet.close();
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
            return blockchainSize;
        }
    }

    public JSONObject getVehicleInfoByEvent(String vehicleId, String event) throws SQLException {
        Connection connection = null;
        PreparedStatement ptmt = null;
        ResultSet resultSet = null;
        JSONObject vehicleInfo = new JSONObject();

        String queryString = "SELECT `previous_hash`, `block_hash`, `block_timestamp`, " +
                "`block_number`, `transaction_id`, `sender`, `event`, `data`, `address` " +
                "FROM `Blockchain` WHERE `transaction_id` LIKE ? AND `address` = ? AND " +
                "`event` = ? AND `validity` = 1 ORDER BY `block_number` DESC LIMIT 1";

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, "V%");
            ptmt.setString(2, vehicleId);
            ptmt.setString(3, event);
            resultSet = ptmt.executeQuery();

            if (resultSet.next()){
                vehicleInfo.put("sender", resultSet.getString("sender"));
                vehicleInfo.put("event", resultSet.getString("event"));
                vehicleInfo.put("data", resultSet.getString("data"));

                System.out.println(vehicleInfo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null)
                resultSet.close();
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
            return vehicleInfo;
        }
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

    public long getRecentBlockNumber() throws SQLException {
        String queryString = "SELECT `block_number` FROM Blockchain ORDER BY id DESC LIMIT 1";
        Connection connection = null;
        PreparedStatement ptmt = null;
        ResultSet result = null;
        long blockNumber = 0;

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);
            result = ptmt.executeQuery();
            if(result.next()) {
                blockNumber = result.getLong("block_number");
            }else {
                blockNumber = 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result != null)
                result.close();
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
            return blockNumber;
        }
    }

    public JSONObject getPreviousBlockData() throws SQLException {
        String queryString = "SELECT `block_hash`,`block_number`, `block_timestamp` FROM Blockchain WHERE `validity` = 1 ORDER BY id DESC LIMIT 1";
        Connection connection = null;
        PreparedStatement ptmt = null;
        ResultSet result = null;
        JSONObject previousBlock = new JSONObject();

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);
            result = ptmt.executeQuery();
            if(result.next()) {
                previousBlock.put("blockHash", result.getString("block_hash"));
                previousBlock.put("blockNumber", result.getString("block_number"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result != null)
                result.close();
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
            return previousBlock;
        }
    }

    public String getPreviousHash() throws SQLException {
        String queryString = "SELECT `block_hash` FROM Blockchain WHERE `validity` = 1 ORDER BY id DESC LIMIT 1";
        Connection connection = null;
        PreparedStatement ptmt = null;
        ResultSet result = null;
        String previousHash = null;

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);
            result = ptmt.executeQuery();
            if(result.next()) {
                previousHash = result.getString("block_hash");
            }else {
                previousHash = null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result != null)
                result.close();
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
            return previousHash;
        }
    }

    @Override
    public JSONArray processFinish(JSONArray output) {
        return null;
    }
}
