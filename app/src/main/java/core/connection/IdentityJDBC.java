package core.connection;

import chainUtil.KeyGenerator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IdentityJDBC {
    Connection connection = null;
    PreparedStatement ptmt = null;
    ResultSet resultSet = null;

    public JSONObject getIdentity(String query, String type) throws SQLException {
        JSONObject identity = null;

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(query);
            ptmt.setString(1, type);
            resultSet = ptmt.executeQuery();

            if (resultSet.next()){
                System.out.println(resultSet);
                identity = new JSONObject();
                String publicKey = resultSet.getString("public_key");
                String role = resultSet.getString("role");
                String name = resultSet.getString("name");
                String location = resultSet.getString("location");

                identity.put("publicKey", publicKey);
                identity.put("role", role);
                identity.put("name", name);
                identity.put("location", location);

                return identity;

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
            return identity;
        }

    }


    //get an identity of a person by address
    public JSONObject getIdentityByAddress(String address) throws SQLException {
        String query = "SELECT public_key, role, name, location FROM `Identity` WHERE `public_key` = ?";
        return getIdentity(query, address);
    }

    //get an identity of a person by address
    public JSONObject getIdentityByRole(String role) throws SQLException {
        String query = "SELECT public_key, role, name, location FROM `Identity` WHERE `role` = ?";
        return getIdentity(query, role);
    }

    public String getPeerPublicKey(String peerID) {
        return KeyGenerator.getInstance().getPublicKeyAsString();
    }

    public JSONArray getPeersByLocation(String location) throws SQLException {
        String query = "SELECT public_key, role, name, location FROM `Identity` WHERE `location` LIKE %?%";

        JSONArray nodes = new JSONArray();
        JSONObject identity = new JSONObject();

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(query);
            ptmt.setString(1, location);
            resultSet = ptmt.executeQuery();

            while (resultSet.next()){
                String publicKey = resultSet.getString("public_key");
                String role = resultSet.getString("role");
                String name = resultSet.getString("name");
                String address = resultSet.getString("location");

                identity.put("publicKey", publicKey);
                identity.put("role", role);
                identity.put("name", name);
                identity.put("location", address);

                nodes.put(identity);
                identity = new JSONObject();

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
            return nodes;
        }

    }
}
