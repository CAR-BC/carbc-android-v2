package core.consensus;

import core.blockchain.Block;
import network.Neighbour;

import java.util.LinkedList;

public class BlockchainShare {

    private Neighbour blockchainRequester;
    private int listeningPort;
    private LinkedList<Block> blockChainInstance;


    public BlockchainShare(Neighbour blockchainRequester) {
//        this.ip = ip;
//        this.listeningPort = listeningPort;
//        id = ip + String.valueOf(listeningPort);
//        blockChainInstance = Blockchain.getInstance().getBlockchainArray();
    }

    public String getId() {
        return blockchainRequester.getPeerID();
    }

    public Neighbour getBlockchainRequester() {
        return blockchainRequester;
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public LinkedList<Block> getBlockChainInstance() {
        return blockChainInstance;
    }
}
