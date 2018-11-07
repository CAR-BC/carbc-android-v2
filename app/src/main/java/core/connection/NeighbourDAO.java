package core.connection;

import network.Neighbour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class NeighbourDAO {

    private final Logger log = LoggerFactory.getLogger(NeighbourDAO.class);
    Connection connection ;
    PreparedStatement ptmt;

    public NeighbourDAO() {
        connection = ConnectionFactory.getInstance().getConnection();
    }

    public void saveNeighbours(String nodeID, String ip, int port, String publicKey) {


    }

    public void saveNeighboursToDB(String nodeID, String ip, int port) {
        String queryString = "INSERT INTO `PeerDetails`(`node_id`,`ip`,`port`) VALUES(?,?,?)";
        try {
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1,nodeID);
            ptmt.setString(2, ip);
            ptmt.setInt(3, port);
            ptmt.execute();
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
            log.info("Peer added to database successfully: {}", nodeID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveNeighbours(Neighbour neighbour) {
        saveNeighboursToDB(neighbour.getPeerID(), neighbour.getIp(), neighbour.getPort());
    }

    public void updatePeer(String nodeID, String ip, int port ) {
        String queryString = "UPDATE `PeerDetails` SET `ip` = ?, `port` = ? WHERE  `node_id` = ?";
        try {

            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, ip);
            ptmt.setInt(2, port);
            ptmt.setString(3, nodeID);
            ptmt.executeUpdate();
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
            log.info("Peer details updated successfully: {}", nodeID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Neighbour getPeer(String nodeID) throws SQLException {
        String query = "SELECT node_id, ip, port FROM `PeerDetails` WHERE `node_id` = ?";
        ResultSet resultSet = null;
        Neighbour neighbour = null;
        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(query);
            ptmt.setString(1, nodeID);
            resultSet = ptmt.executeQuery();


            if (resultSet.next()){
                String node_id = resultSet.getString("node_id");
                String ip = resultSet.getString("ip");
                int port = resultSet.getInt("port");

                neighbour = new Neighbour(node_id, ip, port);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null)
                resultSet.close();
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
            return neighbour;
        }
    }

    public ArrayList<Neighbour> getPeers() throws SQLException {
        String query = "SELECT * FROM `PeerDetails`";
        ResultSet resultSet = null;
        ArrayList<Neighbour> neighbours = new ArrayList<>();
        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(query);
            resultSet = ptmt.executeQuery();

            while (resultSet.next()) {
                String node_id = resultSet.getString("node_id");
                String ip = resultSet.getString("ip");
                int port = resultSet.getInt("port");
                neighbours.add(new Neighbour(node_id, ip, port));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (resultSet != null)
                resultSet.close();
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
            return neighbours;
        }
    }
}
