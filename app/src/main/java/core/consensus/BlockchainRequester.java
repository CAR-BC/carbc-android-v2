package core.consensus;

import chainUtil.ChainUtil;
import core.blockchain.Block;
import core.blockchain.Blockchain;
import core.connection.BlockJDBCDAO;
import network.Neighbour;
import network.communicationHandler.MessageSender;
import org.json.JSONObject;

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
    public synchronized void handleBlockchainHashRequest(Neighbour blockchainRequeseter) {
        long blockChainLength = Blockchain.getRecentBlockNumber();
        if (blockChainLength > 1) {
            sendSignedBlockChain(blockchainRequeseter);
        }
    }

    public synchronized void sendSignedBlockChain(Neighbour blockchainRequeseter) {
        BlockchainShare blockchainShare = new BlockchainShare(blockchainRequeseter);
        String blockchainHash = ChainUtil.getHash(Blockchain.getBlockchain(0).toString());
        blockchainShareDetails.add(blockchainShare);
        String signedBlockchainHash = ChainUtil.getInstance().digitalSignature(blockchainHash);
        MessageSender.sendSignedBlockChain(blockchainRequeseter, signedBlockchainHash, blockchainHash);

    }

    //no need of synchronizing
    public void sendBlockchain(String ip, int listeningPort) throws Exception {
        JSONObject blockchainInfo = Blockchain.getBlockchain(0);
        MessageSender.sendBlockchainToPeer(
                ip,
                listeningPort,
                blockchainInfo.getString("blockchain"),
                blockchainInfo.getInt("count"));
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
        if (ChainUtil.getInstance().signatureVerification(peer.getPublicKey(), signedBlockchain, blockchainHash)) {
            BlockchainReceiver blockchainReceiver = new BlockchainReceiver(peer, signedBlockchain, blockchainHash);
            blockchainReceiveDetails.add(blockchainReceiver);
            blockchainRequest -= 1;
            if (blockchainRequest == 0) {
                requestBlockchain();
            }
        }
    }

    public synchronized void addReceivedBlockchain(String publicKey, JSONObject blockchain, int blockchainLength) throws SQLException, ParseException {
//        LinkedList<Block> blockchainArray = new LinkedList<>();
//        for(int i = 0; i< blockchainLength; i++) {
//            blockchainArray.add(RequestHandler.getInstance().JSONStringToBlock(blockchain.getString(String.valueOf(i))));
//        }
//
//        String blockchainHash = ChainUtil.getInstance().getBlockChainHash(blockchainArray);
//        if(requestedBlockchainHash.equals(blockchainHash)) {
//            addBlockchain(blockchainArray);
//        }
    }

    public void addBlockchain(LinkedList<Block> blockchain) throws SQLException, ParseException {
//        //add to blockchain
//        for(int i = 1; i< blockchain.size(); i++ ) {
//            Blockchain.getInstance().addBlock(blockchain.get(i));
//        }
    }

    public int getBlockchainRequest() {
        return blockchainRequest;
    }

    public synchronized void setBlockchainRequest(int blockchainRequest) {
        this.blockchainRequest = blockchainRequest;
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
        System.out.println(counter);
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
