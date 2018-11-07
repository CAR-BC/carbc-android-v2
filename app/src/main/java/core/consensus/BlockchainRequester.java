package core.consensus;

import chainUtil.ChainUtil;
import core.blockchain.Block;
import core.blockchain.Blockchain;
import core.blockchain.TimeKeeperForBC;
import core.connection.BlockJDBCDAO;
import network.Neighbour;
import network.communicationHandler.Handler;
import network.communicationHandler.MessageSender;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class BlockchainRequester {

    private static BlockchainRequester blockchainRequester;
    private ArrayList<BlockchainShare> blockchainShareDetails;
    private ArrayList<BlockchainReceiver> blockchainReceiveDetails;
    private int blockchainRequest;
    private String requestedBlockchainHash;
    private final Logger log = LoggerFactory.getLogger(Handler.class);

    private BlockchainRequester() {
        blockchainShareDetails = new ArrayList<>();
        blockchainReceiveDetails = new ArrayList<>();
        blockchainRequest = 0;
    }

    public static BlockchainRequester getInstance() {
        if (blockchainRequester == null) {
            blockchainRequester = new BlockchainRequester();
        }
        return blockchainRequester;
    }

    //blockchain request methods
    public synchronized void handleBlockchainHashRequest(Neighbour blockchainRequeseter) throws SQLException {
        long blockChainLength = Blockchain.getRecentBlockNumber();
        if (blockChainLength > 1) {
            sendSignedBlockChain(blockchainRequeseter);
        } else {
            log.info("Only Genesis Block Exist");
        }
    }

    public synchronized void sendSignedBlockChain(Neighbour blockchainRequester) throws SQLException {
        try{
            BlockchainShare blockchainShare = new BlockchainShare(blockchainRequester);
            String blockchainHash = ChainUtil.getHash(Blockchain.getBlockchainJSON(1).getJSONArray("blockchain").toString());
            System.out.println(Blockchain.getBlockchainJSON(1).getJSONArray("blockchain").toString());
            blockchainShareDetails.add(blockchainShare);
            String signedBlockchainHash = ChainUtil.digitalSignature(blockchainHash);
            MessageSender.sendSignedBlockChain(blockchainRequester, signedBlockchainHash, blockchainHash);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    //no need of synchronizing
    public void sendBlockchain(String ip, int listeningPort) throws Exception {
        JSONObject blockchainInfo = Blockchain.getBlockchainJSON(1);
        MessageSender.sendBlockchainToPeer(
                ip,
                listeningPort,
                blockchainInfo.getJSONArray("blockchain"),
                blockchainInfo.getInt("blockchainLength"));
    }

    //no need of synchronizing
    public BlockchainShare getBlockchainShareFromIP(String ip, int listeningPort) {
        String id = ip + String.valueOf(listeningPort);
        for (BlockchainShare blockchainShare : blockchainShareDetails) {
            if (id.equals(blockchainShare.getId())) {
                return blockchainShare;
            }
        }
        return null;
    }

    public synchronized void handleReceivedSignedBlockchain(Neighbour peer, String signedBlockchain, String blockchainHash) {
        if (ChainUtil.signatureVerification(peer.getPublicKey(), signedBlockchain, blockchainHash)) {
            BlockchainReceiver blockchainReceiver = new BlockchainReceiver(peer, signedBlockchain, blockchainHash);
            blockchainReceiveDetails.add(blockchainReceiver);
//            blockchainRequest -= 1;
//            if (blockchainRequest == 0) {
//                requestBlockchain();
//            }
        }
    }

    public synchronized void addReceivedBlockchain(String publicKey, JSONArray blockchain) {
        String receivedBlockchainHash = ChainUtil.getHash(blockchain.toString());
        BlockchainReceiver blockchainReceiver = getBlockchainReceiverfromPK(receivedBlockchainHash);
        if (blockchainReceiver != null) {
            String blockchainHash = blockchainReceiver.getBlockchainHash();
            if (receivedBlockchainHash.equals(blockchainHash)) {
                try{
                    BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
                    blockJDBCDAO.saveBlockchain(blockchain);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getBlockchainRequest() {
        return blockchainRequest;
    }

    public synchronized void setBlockchainRequest(int blockchainRequest) {
        this.blockchainRequest = blockchainRequest;
        TimeKeeperForBC timeKeeperForBC = new TimeKeeperForBC();
        timeKeeperForBC.start();
    }

    public synchronized String findCorrectBlockchain() {
        HashMap<String, Integer> counter = new HashMap<String, Integer>();
        for (BlockchainReceiver blockchainReceiver : blockchainReceiveDetails) {
            if (counter.containsKey(blockchainReceiver.getId())) {
                counter.put(blockchainReceiver.getId(), counter.get(blockchainReceiver.getId()) + 1);
            } else {
                counter.put(blockchainReceiver.getId(), 1);
            }
        }
        int max = 0;
        String publicKey = "";
        for (String key : counter.keySet()) {
            if (counter.get(key) > max) {
                max = counter.get(key);
                publicKey = key;
            }
        }
        return publicKey;
    }


    //no need of synchronizing
    public void requestBlockchain() {
        BlockchainReceiver blockchainReceiver = getBlockchainReceiverfromPK(findCorrectBlockchain());
        requestedBlockchainHash = blockchainReceiver.getBlockchainHash();
        MessageSender.requestBlockchainFromPeer(blockchainReceiver.getIp(), blockchainReceiver.getListeningPort());
    }

    //no need of synchronizing
    public BlockchainReceiver getBlockchainReceiverfromPK(String publicKey) {
        for (BlockchainReceiver blockchainReceiver : blockchainReceiveDetails) {
            if (publicKey.equals(blockchainReceiver.getId())) {
                return blockchainReceiver;
            }
        }
        return null;
    }
}
