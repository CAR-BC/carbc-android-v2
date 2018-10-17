package network.Protocol;

import chainUtil.KeyGenerator;
import network.Client.RequestMessage;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;

public class AgreementCreator {

    public static RequestMessage createAgreementRequest(JSONObject agreement) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sender = KeyGenerator.getInstance().getPublicKeyAsString();
        String receiver = "yourPublicKey";
        String messageType = "Agreement";

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.addHeader("timestamp", timestamp.toString());
        requestMessage.addHeader("sender", sender);
        requestMessage.addHeader("receiver", receiver);
        requestMessage.addHeader("messageType", messageType);
        requestMessage.addTheData(agreement.toString());
        return requestMessage;
    }
}
