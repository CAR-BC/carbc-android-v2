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

    //changed line 17 and 26-28
    private static Blockchain blockchain = new Blockchain();
    private static long blockchainLength;
    private static final Logger log = LoggerFactory.getLogger(Blockchain.class);


    private Blockchain() {
        blockchainLength = getRecentBlockNumber();
    }

    public static Blockchain getInstance() {
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
        }else if(blockchainLength == 1) {
            MessageSender.requestBlockchainHash();
        }
    }



    public static JSONObject getBlockchainJSON(int from) throws SQLException {
        BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
        JSONObject blockchain = null;
        try {
            blockchain = blockJDBCDAO.getBlockchain(from);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
        return blockchain;
    }

    public static String getPreviousHash() {
        BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
        String previousHash = null;
        try {
            previousHash = blockJDBCDAO.getPreviousHash();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return previousHash;
    }

    public static long getRecentBlockNumber() {

        BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
        long recentBlockNumber = 0;
        try {
            recentBlockNumber = blockJDBCDAO.getRecentBlockNumber();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recentBlockNumber;
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
