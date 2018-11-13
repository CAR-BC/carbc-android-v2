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
        boolean isAuthorized = false;
        try{
            String registrationNumber = data.getString("registrationNumber");
            VehicleJDBCDAO vehicleJDBCDAO = new VehicleJDBCDAO();
            boolean isPresent = vehicleJDBCDAO.searchVehicleByRegistrationNumber(registrationNumber);
            isAuthorized = !isPresent;

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return isAuthorized;
        }
    }
}
