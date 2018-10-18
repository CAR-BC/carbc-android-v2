package core.serviceStation.dao;

import core.connection.ConnectionFactory;
import core.serviceStation.ServiceRecord;
import core.serviceStation.ServiceType;
import core.serviceStation.SparePart;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class ServiceJDBCDAO {
    private final Logger log = LoggerFactory.getLogger(ServiceJDBCDAO.class);
    Connection connection = null;

    //add a service to service type
    public boolean addServiceType(ServiceType serviceType) throws SQLException {
        String queryString = "INSERT INTO `ServiceType`(`service_type`) " +
                "VALUES (?)";
        PreparedStatement ptmt = null;
        boolean succeed = false;

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, serviceType.getService_type());
            succeed = ptmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
            return succeed;
        }
    }

    //add a service to the service record and update service table accordingly
    public boolean addServiceRecord(ServiceRecord serviceRecord) throws SQLException {
        PreparedStatement ptmt = null;
        PreparedStatement pstm = null;

        try {
            String queryString = "INSERT INTO `ServiceRecord`( " +
                    "`vehicle_id`, `cost`, `serviced_date`) VALUES (?,?,?)";

            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString,
                    Statement.RETURN_GENERATED_KEYS);

            ptmt.setString(1, serviceRecord.getVehicle_id());
            ptmt.setInt(2, serviceRecord.getCost());
            ptmt.setTimestamp(3, serviceRecord.getServiced_date());
            boolean succeed = ptmt.execute();

            log.debug("Vehicle id :{}. Status - inserted service record :{}",
                    serviceRecord.getVehicle_id(), succeed);

            ResultSet rs = ptmt.getGeneratedKeys();

            String query = "INSERT INTO `Service`(`record_id`, `service_id`, `spare_part_serial_number`) " +
                    "VALUES (?,?,?)";

            if (rs.next()){
                int record_id = rs.getInt(1);

                pstm = connection.prepareStatement(query);
                pstm.setInt(1, record_id);
                pstm.setInt(2, serviceRecord.getService().getService_id());
                pstm.setString(3, serviceRecord.getService().getSparePart());
                succeed = pstm.execute();

                log.debug("Record id :{}. Status - inserted service :{}",
                        record_id, succeed);
            }
            log.info("Service record is inserted to the database successfully");

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (pstm != null)
                pstm.close();
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
            return true;
        }

    }


    //add spare part purchase details
    public boolean addPurchasedSparePart(SparePart sparePart) throws SQLException {
        String queryString = "INSERT INTO `SparePart`(`serial_number`, `spare_part`," +
                " `seller`) VALUES (?,?,?)";
        PreparedStatement ptmt = null;
        boolean succeed = false;

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, sparePart.getSerial_number());
            ptmt.setString(2, sparePart.getSparepart());
            ptmt.setString(3, sparePart.getSeller());
            succeed = ptmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
            return succeed;
        }
    }


    //return service details by vehicle number
    public JSONObject getServiceRecords(String vehicle_id) throws SQLException {
        String queryString = "SELECT s.record_id, `vehicle_id`, `cost`, `serviced_date`, `service_type`," +
                " `spare_part`, `seller` FROM `ServiceRecord` sr INNER JOIN `Service` s " +
                "ON sr.record_id = s.record_id LEFT JOIN `ServiceType` st " +
                "ON s.service_id = st.service_id LEFT JOIN `SparePart` sp " +
                "ON s.spare_part_serial_number = sp.serial_number " +
                "WHERE `vehicle_id` = ?";

        PreparedStatement ptmt = null;
        ResultSet resultSet = null;
        JSONObject serviceRecord = new JSONObject();

        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, vehicle_id);
            resultSet = ptmt.executeQuery();

            int count = 0;
            JSONArray arr = new JSONArray();

            if (resultSet.next()){
                if (count == 0){
                    int recordId = resultSet.getInt(1);
                    serviceRecord.put("vehicle_id", resultSet.getString("vehicle_id"));
                    serviceRecord.put("serviced_date", resultSet.getTimestamp("serviced_date"));
                    count = 1;
                }
                JSONObject services = new JSONObject();
                services.put("service_type", resultSet.getString("service_type"));
                services.put("spare_part", resultSet.getString("spare_part"));
                services.put("seller", resultSet.getString("seller"));
                arr.put(services);
            }
            serviceRecord.put("services", arr);

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
            return serviceRecord;
        }
    }

    public String getCustomerPublicKey(String nodeID) throws SQLException {
        String queryString = "SELECT `public_key` FROM `Customer_details` WHERE `node_id` = ?";

        PreparedStatement ptmt = null;
        ResultSet result = null;
        String publicKey = null;
        try {
            connection = ConnectionFactory.getInstance().getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, nodeID);
            result = ptmt.executeQuery();
            result.next();
            publicKey = result.getString("public_key");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result != null)
                result.close();
            if (ptmt != null)
                ptmt.close();
            if (connection != null)
                connection.close();
            return publicKey;
        }

    }
}
