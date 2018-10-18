package chainUtil;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyGenerator {

    private static KeyGenerator keyGenerator;
    private final Logger log = LoggerFactory.getLogger(KeyGenerator.class);

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public KeyGenerator(){
        KeyPair kp = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
            kpg.initialize(1024);
            kp = kpg.generateKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    };

    public static KeyGenerator getInstance(){
        if(keyGenerator == null) {
            keyGenerator = new KeyGenerator();
        }
        return keyGenerator;
    }

    public boolean generateKeyPair() {
        KeyPairGenerator keyGen = null;
        KeyPair kp = null;
        try {

            keyGen = KeyPairGenerator.getInstance("DSA");
            keyGen.initialize(1024);
            kp = keyGen.generateKeyPair();
            PublicKey publicKey = kp.getPublic();
            log.info("PublicKey Generated");
            PrivateKey privateKey = kp.getPrivate();
            log.info("PrivateKey Generated");



//            keyGen = KeyPairGenerator.getInstance("DSA");
//            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
//            keyGen.initialize(512, random);
//            KeyPair pair = keyGen.generateKeyPair();
//            PublicKey publicKey = pair.getPublic();
//            log.info("PublicKey Generated");
//            PrivateKey privateKey = pair.getPrivate();
//            log.info("PrivateKey Generated");

            // Store Public Key.
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
//            FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "/src/main/resources" + "/public.key");
            FileOutputStream fos = new FileOutputStream("android.resource:/key/public.key");
            log.info("PublicKey Stored");
            fos.write(x509EncodedKeySpec.getEncoded());
            fos.close();

            // Store Private Key.
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                    privateKey.getEncoded());
            //change path to relative path
//            fos = new FileOutputStream(System.getProperty("user.dir") + "/src/main/resources" + "/private.key");
            fos = new FileOutputStream("@keys/private.key");
            fos.write(pkcs8EncodedKeySpec.getEncoded());
            fos.close();
//            log.info("PrivateKey Stored");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private PublicKey loadPublicKey() {
        // Read Public Key.
        System.out.println(getResourcesFilePath("public.key"));
        File filePublicKey = new File(getResourcesFilePath("public.key"));
        FileInputStream fis = null;
        KeyFactory keyFactory = null;
        X509EncodedKeySpec publicKeySpec = null;
        PublicKey publicKey = null;
        try {
            fis = new FileInputStream(getResourcesFilePath("public.key"));
            byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
            fis.read(encodedPublicKey);
            fis.close();

            //load public key
            keyFactory = KeyFactory.getInstance("RSA");
            publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return publicKey;
    }

    private PrivateKey loadPrivateKey() {
        // Read Private Key.
        File filePrivateKey = new File(getResourcesFilePath("private.key"));
        FileInputStream fis = null;
        KeyFactory keyFactory = null;
        PKCS8EncodedKeySpec privateKeySpec = null;
        PrivateKey privateKey = null;

        try {
            fis = new FileInputStream(getResourcesFilePath("private.key"));
            byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
            fis.read(encodedPrivateKey);
            fis.close();

            //load private key
            keyFactory = KeyFactory.getInstance("RSA");
            privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
            privateKey = keyFactory.generatePrivate(privateKeySpec);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return privateKey;
    }

    public PublicKey getPublicKey() {
//        if (getResourcesFilePath("public.key") == null) {
//            log.info("inside getPublicKey if statement");
//            generateKeyPair();
//        }
        return publicKey;
    }

    public PublicKey getPublicKey(String hexvalue) {
        byte[] encodedPublicKey = ChainUtil.hexStringToByteArray(hexvalue);
        KeyFactory keyFactory = null;
        X509EncodedKeySpec publicKeySpec = null;
        PublicKey publicKey = null;
        try {
            keyFactory = KeyFactory.getInstance("DSA");
            publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return publicKey;
    }

    public PrivateKey getPrivateKey() {
//        if (getResourcesFilePath("private.key") == null) {
//            generateKeyPair();
//        }
        return privateKey;
    }

//    public void saveFile() {
//        File path = new File(getFilesDir(),"myfolder");
//    }

    public String getResourcesFilePath(String fileName) {
        URL url = getClass().getClassLoader().getResource(fileName);
        if (url == null) {
            return null;
        } else {
            return url.getPath();
        }
    }

    public String getEncodedPublicKeyString(PublicKey publicKey) {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        return ChainUtil.bytesToHex(x509EncodedKeySpec.getEncoded());
    }

    public String getPublicKeyAsString() {
        return getEncodedPublicKeyString(getPublicKey());
    }
}
