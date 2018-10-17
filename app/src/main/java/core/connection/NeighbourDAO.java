package core.connection;

import network.Neighbour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
