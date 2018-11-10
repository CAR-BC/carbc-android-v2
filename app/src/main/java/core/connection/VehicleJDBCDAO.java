package core.connection;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import HelperInterface.AsyncResponse;

import static core.connection.BlockJDBCDAO.base_url;

public class VehicleJDBCDAO implements AsyncResponse {
    JSONArray jsonArray;


    public ArrayList<String> getRegistrationNumbers(String current_owner){
        APICaller apiCaller = new APICaller();
        jsonArray = null;

        apiCaller.delegate = this;
        ArrayList<String> arrayList = new ArrayList<>();


        try {

            apiCaller.execute(base_url + "blockinfo?current_owner="+ current_owner, "GET", "v", "g");

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
                JSONArray array = jsonArray.getJSONArray(1);
                for (int i = 0; i<array.length();i++){
                    arrayList.add(array.getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    @Override
    public JSONArray processFinish(JSONArray output) {
        System.out.println("process finish executed");
        jsonArray = new JSONArray();
        this.jsonArray = output;
        System.out.println(jsonArray);

        return jsonArray;
    }
}
