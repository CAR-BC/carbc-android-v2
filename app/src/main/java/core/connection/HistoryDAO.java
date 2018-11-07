package core.connection;

import chainUtil.ChainUtil;
import core.blockchain.Block;
import core.blockchain.BlockInfo;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HistoryDAO {
    private final Logger log = LoggerFactory.getLogger(HistoryDAO.class);

    public boolean saveBlockWithAdditionalData(Block block, String data) throws SQLException {

        if (block != null) {
            BlockInfo blockInfo = new BlockInfo();
            blockInfo.setPreviousHash(block.getBlockHeader().getPreviousHash());
            blockInfo.setHash(block.getBlockHeader().getHash());
            blockInfo.setBlockTime(ChainUtil.convertStringToTimestamp(block.getBlockHeader().getBlockTime()));
            blockInfo.setBlockNumber(block.getBlockHeader().getBlockNumber());
            blockInfo.setTransactionId(block.getBlockBody().getTransaction().getTransactionId());
            blockInfo.setSender(block.getBlockBody().getTransaction().getSender());
            blockInfo.setEvent(block.getBlockBody().getTransaction().getEvent());
            blockInfo.setData(block.getBlockBody().getTransaction().getData().toString());
            blockInfo.setAddress(block.getBlockBody().getTransaction().getAddress());

            Connection connection = null;
            PreparedStatement ptmt = null;

            String query = "";

            try {
                String queryString = "INSERT INTO `History`(`previous_hash`, " +
                        "`block_hash`, `block_timestamp`, `block_number`, `validity`," +
                        " `transaction_id`, `sender`, `event`, `data`, `address`, `additional_data`) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

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
                ptmt.setString(11, data.toString());
                ptmt.executeUpdate();

                log.info("Block Added to History");

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
        return false;
    }

    public JSONObject getBlockData(String blockHash) throws SQLException {
        String queryString = "SELECT `event`, `address` FROM `Blockchain` " +
                "WHERE `block_hash` = ? AND `validity` = `T`";

        PreparedStatement ptmt = null;
        Connection connection = null;
        ResultSet result = null;
        JSONObject jsonObject = new JSONObject();

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, blockHash);
            result = ptmt.executeQuery();

            if(result.next()){
                String event = result.getString("event");
                String address = result.getString("address");

                jsonObject.put("event", event);
                jsonObject.put("address", address);
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
            return jsonObject;
        }
    }

    public String getAdditionalData(String blockHash) throws SQLException {
        String queryString = "SELECT `additional_data` FROM `History` WHERE `block_hash` = ?";

        PreparedStatement ptmt = null;
        Connection connection = null;
        ResultSet result = null;
        String additonalData = null;
        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, blockHash);
            result = ptmt.executeQuery();
            result.next();
            additonalData = result.getString("additional_data");
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
            return additonalData;
        }
    }
}
