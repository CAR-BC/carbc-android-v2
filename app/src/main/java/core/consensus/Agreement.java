package core.consensus;

import core.blockchain.Block;
import core.blockchain.Transaction;

//remove this class
public class Agreement {
    private String digitalSignature;
    private String signedBlock;
    private String blockHash;
    private String address;

    public Agreement(String signature, String signedBlock, String blockHash, String publicKey) {
        this.digitalSignature = signature;
        this.blockHash = blockHash;
        this.signedBlock = signedBlock;
        this.address = publicKey;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public String getDigitalSignature() {
        return digitalSignature;
    }

    public String getPublicKey() {
        return address;
    }

    public String getSignedBlock() {
        return signedBlock;
    }
}
