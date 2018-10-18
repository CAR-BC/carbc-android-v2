package core.connection;

import chainUtil.ChainUtil;
import core.blockchain.Block;
import core.blockchain.BlockInfo;
import org.json.JSONObject;

import java.sql.*;

public class BlockJDBCDAO {

    public boolean addBlockToBlockchain(BlockInfo blockInfo, Identity identity) throws SQLException {
        Connection connection = null;
        PreparedStatement ptmt = null;
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

            PreparedStatement psmt = connection.prepareStatement(query);
            psmt.setString(1, identity.getBlock_hash());
            psmt.setString(2, identity.getPublic_key());
            psmt.setString(3, identity.getRole());
            psmt.setString(4, identity.getName());
            psmt.executeUpdate();

            System.out.println("Block is Added Successfully");

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
            return true;
        }

    }


    public ResultSet getBlockchain(long blockNumber) throws SQLException {
        Connection connection = null;
        PreparedStatement ptmt = null;
        ResultSet resultSet = null;

        String queryString = "SELECT `previous_hash`, `block_hash`, `block_timestamp`, " +
                "`block_number`, `transaction_id`, `sender`, `event`, `data`, `address` " +
                "FROM `Blockchain` WHERE `block_number` >= ? AND `validity` = 1";
        String blockchain = "";

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setLong(1, blockNumber);
            resultSet = ptmt.executeQuery();

//            if (resultSet.next()){
//                blockchain = ChainUtil.getInstance().getBlockchainAsJsonString(resultSet);
//            }
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



//    public long getBlockchainLength() {
//
//    }
}
