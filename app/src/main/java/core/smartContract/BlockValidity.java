package core.smartContract;

import config.EventConfigHolder;
import core.connection.BlockJDBCDAO;
import core.connection.IdentityJDBC;
import core.blockchain.Block;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Iterator;

public class BlockValidity {
    private Block block;

    public BlockValidity(Block block){
        this.block = block;
    }

    public boolean isSecondaryPartyValid() throws SQLException {

        String event = block.getBlockBody().getTransaction().getEvent();
        JSONObject data = null;
        try {
            data = new JSONObject(block.getBlockBody().getTransaction().getData());
            JSONObject params = data.getJSONObject("data");
            JSONObject eventConfig = EventConfigHolder.getInstance().getEventJson();

            IdentityJDBC identityJDBC = new IdentityJDBC();

//        JSONObject params = eventConfig.getJSONObject(event).getJSONObject("params");

            JSONObject secondaryParties = params.getJSONObject("SecondaryParty");

        } catch (JSONException e) {
            e.printStackTrace();
        }


//        Iterator<String> keys = params.keys();
//        while ( keys.hasNext() ){
//            String key = (String)keys.next(); // First key in your json object
//
//            if (params.get(key) instanceof JSONObject) {
//                JSONObject jsonObject = secondaryParties.getJSONObject(key);
//                String secondaryPartyAddress = jsonObject.getString("address");
//                String secondaryPartyRole = jsonObject.getString("role");
//
//                JSONObject identity = identityJDBC.getIdentityByAddress(secondaryPartyAddress);
//
//                //if want, can check the name also
//
//                if (!identity.getString("role").equals(secondaryPartyRole)){
//                    return false;
//                }
//            }
//
//        }

        ////old version
//        for (int i = 0; i < secondaryParties.length(); i++){
//            JSONObject jsonObject = secondaryParties.getJSONObject(i);
//            String secondaryPartyAddress = jsonObject.getString("address");
//            String secondaryPartyRole = jsonObject.getString("role");
//
//            JSONObject identity = blockJDBCDAO.getIdentityByAddress(secondaryPartyAddress);
//
//            //if want, can check the name also
//
//            if (!identity.getString("role").equals(secondaryPartyRole)){
//                return false;
//            }
//
//        }
        return true;
    }

    public boolean checkSecondaryParty(String secondaryPartyRole, String secondatyPartyAddress){


        return true;
    }
}
