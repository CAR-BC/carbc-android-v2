package core.connection;

import HelperInterface.AsyncResponse;
import network.Neighbour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static core.connection.BlockJDBCDAO.base_url;

public class NeighbourDAO implements AsyncResponse {

    private final Logger log = LoggerFactory.getLogger(NeighbourDAO.class);
    Connection connection ;
    PreparedStatement ptmt;
    JSONArray jsonArray;

    APICaller apiCaller = new APICaller();


    public NeighbourDAO() {
        connection = ConnectionFactory.getInstance().getConnection();
    }

    public void saveNeighbours(String nodeID, String ip, int port, String publicKey) {

        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url+"blockinfo?block_number=", "GET", "v", "g");

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

    }

    public void saveNeighboursToDB(String nodeID, String ip, int port) {
        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url+"insertpeerdetails" +
                    "?node_id=" + nodeID +
                    "&ip=" + ip +
                    "&port=" + port
                    , "GET", "v", "g");

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
    }

    public void saveNeighbours(Neighbour neighbour) {
        saveNeighboursToDB(neighbour.getPeerID(), neighbour.getIp(), neighbour.getPort());
    }

    public void updatePeer(String nodeID, String ip, int port ) {

        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url+"updatepeerdetails" +
                    "?node_id=" + nodeID +
                    "&ip=" + ip +
                    "&port=" + port
                    , "GET", "v", "g");

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
    }

    public Neighbour getPeer(String nodeID) throws SQLException {

        apiCaller.delegate = this;
        Neighbour neighbour = null;
        try {

            apiCaller.execute(base_url+"findpeer?node_id=" + nodeID, "GET", "v", "g");

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
            JSONObject object = jsonArray.getJSONObject(0);
            neighbour = new Neighbour(object.getString("node_id"),object.getString("ip"), (Integer) object.get("port"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

            return neighbour;
        }


    public ArrayList<Neighbour> getPeers() throws SQLException {

        ArrayList<Neighbour> neighbours = new ArrayList<>();
        try {

            apiCaller.execute(base_url+"findallpeers", "GET", "v", "g");

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

        for (int i = 0; i<jsonArray.length();i++) {
            JSONObject object = null;
            try {
                object = jsonArray.getJSONObject(0);
                Neighbour neighbour = new Neighbour(object.getString("node_id"),object.getString("ip"), (Integer) object.get("port"));
                neighbours.add(neighbour);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
            return neighbours;
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
