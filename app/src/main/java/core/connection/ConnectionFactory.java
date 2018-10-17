package core.connection;

import Exceptions.FileUtilityException;
import config.DatabaseConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private String driverClassName = "com.mysql.jdbc.Driver";
    private String connectionUrl = "jdbc:mysql://localhost:3306/CarBC?verifyServerCertificate=false&useSSL=true";
    private String dbUser;
    private String dbPwd;

    private static ConnectionFactory connectionFactory = null;

    private ConnectionFactory() {
        try {
            DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
            databaseConfig.setConfigUsingResource();
            JSONObject dbDetail = databaseConfig.getDBJson();
            dbUser = dbDetail.getString("dbUser");
            dbPwd = dbDetail.getString("dbPwd");
            System.out.println(dbUser);
            System.out.println(dbPwd);

            Class.forName(getDriverClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileUtilityException e) {
            e.printStackTrace();
        } catch (JSONException e) {


        }

    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(getConnectionUrl(), getDbUser(), getDbPwd());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static ConnectionFactory getInstance() {
        if (connectionFactory == null) {
            connectionFactory = new ConnectionFactory();
        }
        return connectionFactory;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPwd() {
        return dbPwd;
    }
}
