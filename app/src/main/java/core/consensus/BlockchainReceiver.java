package core.consensus;

import network.Neighbour;

public class BlockchainReceiver {

    private Neighbour peer;
    private String signedBlockchain;
    private String id;

    public BlockchainReceiver(Neighbour peer, String signedBlockchain, String blockchainHash) {
        this.peer = peer;
        this.signedBlockchain = signedBlockchain;
        this.id = blockchainHash;
    }

    public String getId() {
        return id;
    }

    public String getIp() {
        return peer.getIp();
    }

    public int getListeningPort() {
        return peer.getPort();
    }

    public String getSignedBlockchain() {
        return signedBlockchain;
    }

    public String getBlockchainHash() {
        return id;
    }

}
