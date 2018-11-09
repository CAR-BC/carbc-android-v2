package chainUtil;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

    }

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

            // Store Public Key.
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
            FileOutputStream fos = new FileOutputStream("sdcard/publicKey.key");
            log.info("PublicKey Stored");
            fos.write(x509EncodedKeySpec.getEncoded());
            fos.close();

            // Store Private Key.
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                    privateKey.getEncoded());
            //change path to relative path
            fos = new FileOutputStream("sdcard/privateKey.key");
            fos.write(pkcs8EncodedKeySpec.getEncoded());
            fos.close();
            log.info("PrivateKey Stored");
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
        File filePublicKey = new File("sdcard/publicKey.key");
        FileInputStream fis = null;
        KeyFactory keyFactory = null;
        X509EncodedKeySpec publicKeySpec = null;
        PublicKey publicKey = null;
        try {
            fis = new FileInputStream("sdcard/publicKey.key");
            byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
            fis.read(encodedPublicKey);
            fis.close();

            //load public key
            keyFactory = KeyFactory.getInstance("DSA");
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
        File filePrivateKey = new File("sdcard/privateKey.key");
        FileInputStream fis = null;
        KeyFactory keyFactory = null;
        PKCS8EncodedKeySpec privateKeySpec = null;
        PrivateKey privateKey = null;

        try {
            fis = new FileInputStream("sdcard/privateKey.key");
            byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
            fis.read(encodedPrivateKey);
            fis.close();

            //load private key
            keyFactory = KeyFactory.getInstance("DSA");
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
        File pk = new File("sdcard/public.key");
        if (pk == null) {
            log.info("inside getPublicKey if statement");
            generateKeyPair();
        }
        return loadPublicKey();
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
        File sk = new File("sdcard/private.key");
        if (sk == null) {
            generateKeyPair();
        }
        return loadPrivateKey();
    }

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

    public void genKeyPairandSave() {
        log.info("inside genkey pair");
        KeyPair kp = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
            kpg.initialize(1024);
            kp = kpg.generateKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();

            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
            FileOutputStream fos = new FileOutputStream("sdcard/publicKey.key");
            log.info("PublicKey Stored");
            fos.write(x509EncodedKeySpec.getEncoded());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getPUbkeyformfile() {
        File filePublicKey = new File("sdcard/publicKey.key");
        FileInputStream fis = null;
        KeyFactory keyFactory = null;
        X509EncodedKeySpec publicKeySpec = null;
        PublicKey publicKey = null;
        try {
            fis = new FileInputStream("sdcard/public.key");
            byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
            fis.read(encodedPublicKey);
            fis.close();

            //load public key
            keyFactory = KeyFactory.getInstance("DSA");
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

        System.out.println("PublicKey" + publicKey);
    }

    public static void writeToFile(String text, String fileName)
    {
        File logFile = new File("sdcard/log"+fileName+".txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
