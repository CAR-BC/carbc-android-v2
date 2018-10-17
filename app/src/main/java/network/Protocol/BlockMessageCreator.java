package network.Protocol;

import network.Client.RequestMessage;
import org.json.JSONObject;

import java.sql.Timestamp;

public class BlockMessageCreator {

    public static RequestMessage createBlockMessage(JSONObject block){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sender = "myPublicKey";
        String receiver = "yourPublicKey";
        String messageType = "BlockBroadcast";

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.addHeader("timestamp", timestamp.toString());
        requestMessage.addHeader("sender", sender);
        requestMessage.addHeader("receiver", receiver);
        requestMessage.addHeader("messageType", messageType);
        requestMessage.addTheData(block.toString());
        return requestMessage;
    }
}
