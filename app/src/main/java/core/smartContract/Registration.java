package core.smartContract;

import core.connection.VehicleJDBCDAO;
import org.json.JSONObject;
import java.sql.SQLException;

public class Registration {
    private JSONObject data;

    public Registration(JSONObject data){
        this.data = data;
    }

    public boolean isAuthorized() throws SQLException {
        boolean isPresent = false;
        try{
            String registrationNumber = data.getString("registrationNumber");
            VehicleJDBCDAO vehicleJDBCDAO = new VehicleJDBCDAO();
            isPresent = vehicleJDBCDAO.searchVehicleByRegistrationNumber(registrationNumber);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return isPresent;
        }
    }
}
