package core.blockchain;

import chainUtil.ChainUtil;
import core.connection.BlockJDBCDAO;
import core.consensus.Consensus;
import network.communicationHandler.MessageSender;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Member;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Blockchain {

    private static Blockchain blockchain;
    private static long blockchainLength;
    private static final Logger log = LoggerFactory.getLogger(Blockchain.class);


    private Blockchain() {
        blockchainLength = getRecentBlockNumber();
    }

    public static Blockchain getInstance() {
        if (blockchain == null) {
            blockchain = new Blockchain();
        }
        return blockchain;
    }

    public static Block createGenesis() {
        Transaction transaction = new Transaction();
        BlockBody blockBody = new BlockBody();
        blockBody.setTransaction(transaction);
        BlockHeader blockHeader = new BlockHeader();
        blockHeader.setBlockNumber(1);
        blockHeader.setBlockTime("2018.02.04.23.13.00"); //officially project started time
        blockHeader.setPreviousHash("GENESIS_BLOCK");
        blockHeader.setValidity(true);
        blockHeader.setHash(ChainUtil.getBlockHash(blockBody));
        Block genesisBlock = new Block(blockHeader, blockBody);
        log.info("Genesis Block Created");
        return genesisBlock;
    }

    public static void runBlockChain() {
        blockchainLength = getRecentBlockNumber();
        if(blockchainLength == 0) {
            addBlocktoBlockchain(createGenesis());
            log.info("Requesting Blockchain");
            MessageSender.requestBlockchainHash();
        }
    }

    public static JSONObject getBlockchain(int from) {
        BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
        ResultSet rs = null;
        JSONObject convertedResultSet = null;
        try {
            rs = blockJDBCDAO.getBlockchain(from);
            convertedResultSet = convertResultSetIntoJSON(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertedResultSet;

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
        result.put("blockchainSize", count);
        result.put("blockchain", jsonArray.toString());
        return result;
    }


    //TODO implement actual logic
    public static String getPreviousHash() {
//        BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
//        String previousHash = null;
//        previousHash = Blockchain.getPreviousHash();

        return "previousHash";
    }

    //TODO implement actual logic
    public static long getRecentBlockNumber() {

//        BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
//        long recentBlockNumber = 0;
//        try {
//            recentBlockNumber = blockJDBCDAO.getRecentBlockNumber();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return 104;
    }

    public long getBlockchainLength() {
        return blockchainLength;
    }

    public void setBlockchainLength(long blockchainLength) {
        this.blockchainLength = blockchainLength;
    }

    public void incrementBlockchain() {
        blockchainLength++;
    }

    public void decrementBlockchain() {
        blockchainLength--;
    }

    //test method
    public static void addBlocktoBlockchain(Block block) {
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
        blockInfo.setValidity(true);
        BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
        try {
            blockJDBCDAO.addBlockToBlockchain(blockInfo, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
