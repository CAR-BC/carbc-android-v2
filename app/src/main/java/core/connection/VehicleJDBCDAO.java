package core.connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import HelperInterface.AsyncResponse;

import static core.connection.BlockJDBCDAO.base_url;

public class VehicleJDBCDAO implements AsyncResponse {
    JSONArray jsonArray;
    public static HashMap<String, String> vehicleNumbersWithRegistrationNumbers = new HashMap<>();
    public static HashMap<String, String> registrationNumbersWithVehicleNumbers = new HashMap<>();


    public ArrayList<String> getRegistrationNumbers(String current_owner) {
        APICaller apiCaller = new APICaller();
        jsonArray = null;
        apiCaller.delegate = this;
        ArrayList<String> arrayList = new ArrayList<>();
        vehicleNumbersWithRegistrationNumbers = new HashMap<>();
        try {
            apiCaller.execute(base_url + "findmyvehiclenumbers?current_owner=" + current_owner, "GET", "v", "g");
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
            if (jsonArray.getBoolean(0)) {
                JSONArray array = jsonArray.getJSONArray(1);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    arrayList.add(object.getString("registration_number"));
                    System.out.println("??????????????????????????????");
                    System.out.println("registration_number"+object.getString("registration_number"));
                    System.out.println("vehicle_id"+object.getString("vehicle_id"));
                    vehicleNumbersWithRegistrationNumbers.put(object.getString("registration_number")
                            ,object.getString("vehicle_id"));
                    registrationNumbersWithVehicleNumbers.put(object.getString("vehicle_id")
                            ,object.getString("registration_number"));

                    System.out.println("********************************************************");
                    System.out.println(registrationNumbersWithVehicleNumbers.keySet());
                    System.out.println(registrationNumbersWithVehicleNumbers.values());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public void addDataToVehicle() {

    }

    public boolean searchVehicleByRegistrationNumber(String registrationNumber) {


        return false;
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
