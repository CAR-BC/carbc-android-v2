package core.consensus;

public class TransactionDataRequester extends DataRequester{

    private String vehicleID;
    private String date;
    private String transactionType;

    public TransactionDataRequester(String transactionType, String peerID, String vehicleID, String date) {
        super(peerID);
        this.transactionType = transactionType;
        this.vehicleID = vehicleID;
        this.date = date;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public String getDate() {
        return date;
    }

    public String getTransactionType() {
        return transactionType;
    }

}
