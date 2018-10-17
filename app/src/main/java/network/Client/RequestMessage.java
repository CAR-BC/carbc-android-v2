package network.Client;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RequestMessage implements Serializable{
    Map<String, String> headers;
    String data;

    public RequestMessage() {
        headers = new HashMap<>();
    }

    public void addHeader(String key, String value){
        this.headers.put(key, value);
    }

    public void addTheData(String data) {
        this.data = data;
    }

    public Map<String, String> readHeaders(){
        return headers;
    }

    public String readData() {
        return data;
    }

}
