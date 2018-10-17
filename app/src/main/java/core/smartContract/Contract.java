package core.smartContract;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

public class Contract {

    private final UUID contractAddress;
    private final PublicKey contractOwner;
    //    private final String contractHash;
    private final PublicKey contractPublicKey;


    public Contract(byte[] owner) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //generating a unique random id to the contract
        UUID uuid = UUID.randomUUID();
        this.contractAddress = uuid;

        //generating a key pair to the contract
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.contractPublicKey = keyPair.getPublic();

        //generate publicKey
        this.contractOwner =
                KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(owner));


    }

    public UUID getContractAddress() {
        return this.contractAddress;

    }


    protected PublicKey getContractOwner() {
        return this.contractOwner;
    }


    public void generateContractHash() {

    }


    protected void getContractHash() {

    }

    public PublicKey getContractPublicKey(){
        return this.contractPublicKey;
    }
}

