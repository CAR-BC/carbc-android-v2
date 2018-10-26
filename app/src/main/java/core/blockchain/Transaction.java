package core.blockchain;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Transaction {
    private String transactionId;
    private String sender;
    private String event;
    private String data;
    private String address;
    private String time;

    public Transaction(String transactionType, String sender, String event, String jsonData, String address){
        this.transactionId = generateTransactionID(transactionType);
        this.sender = sender;
        this.event = event;
        this.data = jsonData;
        this.address = address;
        this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public Transaction(String transactionType, String sender, String event, String jsonData){
        this.transactionId = generateTransactionID(transactionType);
        this.sender = sender;
        this.event = event;
        this.data = jsonData;
        this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public Transaction() {
        this.transactionId = "0-0000";
        this.sender = "CAR.BC";
        this.event = "genesisBlock";
        this.data = "Initial Data";
        this.address = "0000";
        this.time = new SimpleDateFormat("YYYY.MM.dd.HH.mm.ss").format(new Date(0));
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getSender() {
        return sender;
    }

    public String getEvent() {
        return event;
    }

    public String getData() {
        return data;
    }

    public String getAddress() { return address; }

    public String generateTransactionID(String transactionType) {
        Random random = new Random();
        int number = 10000 + Math.abs(random.nextInt(90000));
        return transactionType+"-"+ number;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime() {
        this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public void setAddress() {
        Random random = new Random(System.currentTimeMillis());
        int number = 10000 + Math.abs(random.nextInt(90000));
        this.address = "V -"+ number;
    }
}
