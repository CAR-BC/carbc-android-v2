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
    Connection connection;
    PreparedStatement ptmt;
    JSONArray jsonArray;


    public NeighbourDAO() {
        connection = ConnectionFactory.getInstance().getConnection();
    }

    public void saveNeighbours(String nodeID, String ip, int port, String publicKey) {
        APICaller apiCaller = new APICaller();
        jsonArray = null;

        apiCaller.delegate = this;
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

    }

    public void saveNeighboursToDB(String nodeID, String ip, int port) {
        APICaller apiCaller = new APICaller();
        jsonArray = null;

        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url + "insertpeerdetails" +
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

    public void updatePeer(String nodeID, String ip, int port) {
        APICaller apiCaller = new APICaller();
        jsonArray = null;

        apiCaller.delegate = this;
        try {

            apiCaller.execute(base_url + "updatepeerdetails" +
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
        APICaller apiCaller = new APICaller();
        jsonArray = null;

        apiCaller.delegate = this;
        Neighbour neighbour = null;
        try {

            apiCaller.execute(base_url + "findpeer?node_id=" + nodeID, "GET", "v", "g");

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
                JSONObject object = array.getJSONObject(0);
                neighbour = new Neighbour(object.getString("node_id"), object.getString("ip"), (Integer) object.get("port"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return neighbour;
    }


    public ArrayList<Neighbour> getPeers() throws SQLException {
        APICaller apiCaller = new APICaller();
        JSONArray array = new JSONArray();
        jsonArray = null;

        ArrayList<Neighbour> neighbours = new ArrayList<>();
        try {

            apiCaller.execute(base_url + "findallpeers", "GET", "v", "g");

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
            JSONArray newarray = jsonArray.getJSONArray(1);
            array = newarray.getJSONArray(0);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = null;
                try {
                    object = array.getJSONObject(0);
                    Neighbour neighbour = new Neighbour(object.getString("node_id"), object.getString("ip"), (Integer) object.get("port"));
                    neighbours.add(neighbour);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return neighbours;
    }


    @Override
    public JSONArray processFinish(JSONArray output) {
        System.out.println("process finish executed");
        if (output.length() == 0) {
            this.jsonArray.put("nullResultFound");
        } else {
            this.jsonArray = output;
        }
        return jsonArray;
    }
}
