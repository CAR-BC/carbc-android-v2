package core.smartContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestJSON {

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();

        JSONObject item1 = new JSONObject();
        JSONArray list1 = new JSONArray();
        JSONObject item11 = new JSONObject();
        try {
            item11.put("name", "Sajinie");
            item11.put("address", "1234");
            list1.put(item11);

            item1.put("ServiceStation", list1);
            jsonObject.put("SecondaryParty", item1);

            System.out.println(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
