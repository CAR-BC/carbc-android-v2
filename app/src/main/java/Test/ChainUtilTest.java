package Test;

import chainUtil.ChainUtil;
import chainUtil.KeyGenerator;

public class ChainUtilTest {
    public static void main() {
        try {
            String data = "data";
            System.out.println("*********signature*********");
            String signatureString = ChainUtil.digitalSignature(data);
            System.out.println(signatureString);
            System.out.println("*********verification*********");
            System.out.println(ChainUtil.signatureVerification(KeyGenerator.getInstance().getPublicKeyAsString(), signatureString, data));
            System.out.println("publickeyAsStringTest");
            System.out.println(KeyGenerator.getInstance().getPublicKeyAsString());
            System.out.println(KeyGenerator.getInstance().getPublicKeyAsString());
            System.out.println(KeyGenerator.getInstance().getPublicKeyAsString());
            System.out.println(KeyGenerator.getInstance().getPublicKeyAsString().length());

            System.out.println("public key test");
            System.out.println(KeyGenerator.getInstance().getPublicKeyAsString());

        } catch (Exception e) {
            e.getMessage();
        }
    }
}
