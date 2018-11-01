package controller;

import org.json.JSONObject;

import chainUtil.ChainUtil;
import chainUtil.KeyGenerator;
import core.blockchain.Block;
import core.blockchain.BlockBody;
import core.blockchain.BlockHeader;
import core.blockchain.Transaction;
import core.consensus.Consensus;

public class BlockSender extends Thread {

    private JSONObject data;
    private String vehicleID;
    private String event;

    public BlockSender(String event, String vehicleID, JSONObject data) {
        this.vehicleID = vehicleID;
        this.event = event;
        this.data = data;
    }

    public BlockSender(String event, JSONObject data) {
        this.data = data;
        this.event = event;
    }

    public void run() {
        switch (event) {
            case "RegisterVehicle":
                sendRegisterTransaction();

            default:
                sendTransaction();

        }
    }

    public void sendRegisterTransaction() {
        String sender = KeyGenerator.getInstance().getPublicKeyAsString();
        Transaction transaction = new Transaction("V", sender, "RegisterVehicle", data.toString());
        transaction.setAddress();
        BlockBody blockBody = new BlockBody();
        blockBody.setTransaction(transaction);
        String blockHash = ChainUtil.getInstance().getBlockHash(blockBody);
        BlockHeader blockHeader = new BlockHeader(blockHash);
        Block block = new Block(blockHeader, blockBody);
        Consensus.getInstance().broadcastBlock(block, data.toString());
    }

    public void sendTransaction() {
        String sender = KeyGenerator.getInstance().getPublicKeyAsString();
        Transaction transaction = new Transaction("V", sender, event, data.toString(), vehicleID);
        BlockBody blockBody = new BlockBody();
        blockBody.setTransaction(transaction);
        String blockHash = ChainUtil.getInstance().getBlockHash(blockBody);
        BlockHeader blockHeader = new BlockHeader(blockHash);
        Block block = new Block(blockHeader, blockBody);
        Consensus.getInstance().broadcastBlock(block, data.toString());
    }
}
