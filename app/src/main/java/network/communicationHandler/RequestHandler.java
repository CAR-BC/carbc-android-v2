package network.communicationHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RequestHandler {
    private final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private static RequestHandler requestHandler;
    private int listeningPort;

    private RequestHandler() {}

    public static RequestHandler getInstance() {
        if(requestHandler == null) {
            requestHandler = new RequestHandler();
        }
        return requestHandler;
    }

    public void handleRequest(Map headers, String data){
        log.info("=============requestHandler==============");
        String messageType = (String)headers.get("messageType");
        String peerID = (String)headers.get("sender");

        Handler handler = new Handler(messageType, data, peerID);
        handler.start();

    }

}
