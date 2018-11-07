package core.connection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import HelperInterface.AsyncResponse;
import core.blockchain.BlockInfo;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BlockJDBCDAO implements AsyncResponse {
    private ProgressDialog pDialog;
    Context context;
    BlockInfo blockInfo;
    Identity identity;
    long blockNumber;
    JSONArray jsonObject;
    JSONObject object;

    ResultSet resultSet = null;

    APICaller apiCaller = new APICaller();


    public boolean addBlockToBlockchain(BlockInfo blockInfo, Identity identity) throws SQLException {

        String transactionId = blockInfo.getTransactionId();
        String transactionType = transactionId.substring(0, 1);


        if (transactionType.equals("I")){
            new APICaller().execute("http://192.168.8.100/carbc/insertInToIdentityTable.php","POST","Identity",identity);
            while (jsonObject == null){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("block added to identity table");
        }

        try {
            new APICaller().execute("http://192.168.8.100/carbc/insertInToBlockchainTable.php","POST","BlockInfo",blockInfo);
            while (jsonObject == null){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Block is Added Successfully");


        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {

            return true;
        }

    }


    public JSONObject getBlockchain(long blockNumber) throws SQLException, JSONException {
        this.blockNumber = blockNumber;

        apiCaller.delegate = this;

        apiCaller.execute("http://192.168.8.102:8080/blockinfo?block_number="+blockNumber, "GET", "v", "g");

        while (jsonObject == null){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        System.out.println(jsonObject);
        try {
            //new GetBlockchain().execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            return jsonObject.getJSONObject(0);
            //return obj;
        }
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

    public ResultSet getCompleteVehicleInfo(String vehicleId) throws SQLException {
        Connection connection = null;
        PreparedStatement ptmt = null;
        ResultSet resultSet = null;

        String queryString = "SELECT `previous_hash`, `block_hash`, `block_timestamp`, " +
                "`block_number`, `transaction_id`, `sender`, `event`, `data`, `address` " +
                "FROM `Blockchain` WHERE `transaction_id` LIKE ? AND `event` = ? AND " +
                "`address` >=  AND `validity` = `T`";

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, "V");
            ptmt.setString(2, vehicleId);
            ptmt.setString(2, "T");
            resultSet = ptmt.executeQuery();

            if (resultSet.next()){

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
            return resultSet;
        }
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


    @Override
    public JSONArray processFinish(JSONArray output) {
        System.out.println("process finish executed");
        this.jsonObject = output;
        return output;
    }
}
