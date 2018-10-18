package Test;

import org.json.JSONObject;

import chainUtil.ChainUtil;
import chainUtil.KeyGenerator;
import controller.Controller;
import network.Client.RequestMessage;
import network.Node;
import network.Protocol.MessageCreator;

public class SignatureTest {
    public static void main(String[] args) {
        ChainUtilTest.main();
        Controller controller = new Controller();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("signature", ChainUtil.digitalSignature("data"));
            jsonObject.put("signedData", ChainUtil.digitalSignature("data"));
            jsonObject.put("pk", KeyGenerator.getInstance().getPublicKeyAsString());

            RequestMessage msg = MessageCreator.createMessage(jsonObject, "TestSignature");
            Node.getInstance().sendMessageToPeer("192.168.8.134", 49222, msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
