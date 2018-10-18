package network.Protocol;

import network.Client.RequestMessage;
import network.Node;

import java.sql.Timestamp;

public class AckMessageCreator {
    public static RequestMessage createAckMessage(String ackFor, String peer){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sender = Node.getInstance().getNodeId();
        String receiver = peer;
        String messageType = "Ack";

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.addHeader("timestamp", timestamp.toString());
        requestMessage.addHeader("sender", sender);
        requestMessage.addHeader("receiver", receiver);
        requestMessage.addHeader("messageType", messageType);
        requestMessage.addHeader("ackFor", ackFor);
        return requestMessage;
    }
}
