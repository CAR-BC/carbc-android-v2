package core.smartContract;

import core.connection.BlockJDBCDAO;
import org.json.JSONObject;

import java.sql.SQLException;

public class OwnershipExchange {
    String vehicleId;
    String sender;

    public OwnershipExchange(String vehicleId, String sender){
        this.vehicleId = vehicleId;
        this.sender = sender;
    }

    public boolean isAuthorizedToSeller() throws SQLException {
        try {
            BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
            JSONObject vehicleInfo2 = blockJDBCDAO.getVehicleInfoByEvent(vehicleId, "ExchangeOwnership");

            if (vehicleInfo2.length()>0){
                JSONObject data = new JSONObject(vehicleInfo2.getString("data"));
                JSONObject secondaryParty = data.getJSONObject("SecondaryParty").getJSONObject("NewOwner");
                if ((secondaryParty.getString("publicKey").equals(sender))){
                    return true;
                }
            }else {
                JSONObject vehicleInfo1 = blockJDBCDAO.getVehicleInfoByEvent(vehicleId, "RegisterVehicle");

                if (vehicleInfo1.length()>0){
                    JSONObject data = new JSONObject(vehicleInfo1.getString("data"));
                    if ((data.getString("current_owner").equals(sender))){
                        return true;
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
