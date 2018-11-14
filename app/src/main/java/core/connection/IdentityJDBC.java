package core.connection;

import HelperInterface.AsyncResponse;
import chainUtil.KeyGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static core.connection.BlockJDBCDAO.base_url;

public class IdentityJDBC implements AsyncResponse {

    JSONArray jsonArray;


    //get an identity of a person by address
    public JSONObject getIdentityByAddress(String address) throws SQLException, JSONException {
        APICaller apiCaller = new APICaller();
        JSONObject object = new JSONObject();
        jsonArray = null;
        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url+"findidentity?publicKey=" + address , "GET", "v", "g");

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (jsonArray.getBoolean(0)){
            JSONArray array = jsonArray.getJSONArray(1);
            object = array.getJSONObject(0);
        }
        return object;
    }

    //get an identity of a person by address
    public JSONObject getIdentityByRole(String role) throws SQLException, JSONException {
        APICaller apiCaller = new APICaller();
    JSONObject object = new JSONObject();
        jsonArray = null;
        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url+"findidentitybyrole?role=" + role , "GET", "v", "g");

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (jsonArray.getBoolean(0)){
            JSONArray array = jsonArray.getJSONArray(1);
            object = array.getJSONObject(0);
        }
        return object;
    }

    public String getPeerPublicKey(String peerID) {
        return KeyGenerator.getInstance().getPublicKeyAsString();
    }

    public JSONArray getPeersByLocation() throws SQLException {
        APICaller apiCaller = new APICaller();
        JSONArray array = new JSONArray();
        jsonArray = null;
        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url+"getservicestations", "GET", "v", "g");

            while (jsonArray == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        try {
            if (jsonArray.getBoolean(0)){
                array = jsonArray.getJSONArray(1);
                //array = array.getJSONArray(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }


    @Override
    public JSONArray processFinish(JSONArray output) {
        System.out.println("process finish executed");
        if (output.length()==0){
            this.jsonArray.put("nullResultFound");
        }
        else {
            this.jsonArray = output;
        }
        return jsonArray;
    }
}
