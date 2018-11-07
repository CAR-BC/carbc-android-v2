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

    APICaller apiCaller = new APICaller();

    //get an identity of a person by address
    public JSONObject getIdentityByAddress(String address) throws SQLException, JSONException {
        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url+"findidentitybyaddress?address=" + address , "GET", "v", "g");

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
        return jsonArray.getJSONObject(0);
    }

    //get an identity of a person by address
    public JSONObject getIdentityByRole(String role) throws SQLException, JSONException {
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
        return jsonArray.getJSONObject(0);
    }

    public String getPeerPublicKey(String peerID) {
        return KeyGenerator.getInstance().getPublicKeyAsString();
    }

    public JSONArray getPeersByLocation(String location) throws SQLException {
        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url+"blockinfo?block_number=" , "GET", "v", "g");

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
        return jsonArray;
        }



    @Override
    public JSONArray processFinish(JSONArray output) {
        System.out.println("process finish executed");
        this.jsonArray = output;
        return output;
    }
}
