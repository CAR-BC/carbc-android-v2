package core.consensus;

import network.Neighbour;
import org.json.JSONObject;

import java.sql.Timestamp;

public class DataRequester {

    private String peerID;
    private Neighbour dataOwner;
    private JSONObject receivedData;

    //for additional data request
    public DataRequester(String peerID) {
        this.peerID = peerID;
    }

    public String getPeerID() {
        return peerID;
    }

    public Neighbour getDataOwner() {
        return dataOwner;
    }

    public void setDataOwner(Neighbour dataOwner) {
        this.dataOwner = dataOwner;
    }

    public JSONObject getReceivedData() {
        return receivedData;
    }

    public void setReceivedData(JSONObject receivedData) {
        this.receivedData = receivedData;
    }
}
