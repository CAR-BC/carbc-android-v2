package core.consensus;

public class AddtionalDataRequester extends DataRequester{

    String blockHash;

    public AddtionalDataRequester(String peerID, String blockHash) {
        super(peerID);
        this.blockHash = blockHash;
    }


    public String getBlockHash() {
        return blockHash;
    }
}
