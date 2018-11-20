package core.connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

import HelperInterface.AsyncResponse;

import static core.connection.BlockJDBCDAO.base_url;

public class ApplicationUserJDBCDAO implements AsyncResponse {

    JSONArray jsonArray;

    public Boolean getBlockData(String blockHash) throws SQLException, JSONException {
        APICaller apiCaller = new APICaller();
        Boolean isCorrectUser = false;
        apiCaller.delegate = this;
        jsonArray = null;

        try {

            apiCaller.execute(base_url + "blockinfo?block_number=", "GET", "v", "g");

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
            isCorrectUser = array.getBoolean(0);
        }

        return isCorrectUser;
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
