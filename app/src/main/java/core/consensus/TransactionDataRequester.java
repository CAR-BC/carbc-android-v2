package core.consensus;

public class TransactionDataRequester extends DataRequester{

    private String vehicleID;
    private String date;

    public TransactionDataRequester(String peerID, String vehicleID, String date) {
        super(peerID);
        this.vehicleID = vehicleID;
        this.date = date;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public String getDate() {
        return date;
    }
}
