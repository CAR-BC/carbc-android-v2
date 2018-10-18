package config;

import Exceptions.FileUtilityException;
import constants.Constants;
import org.json.JSONObject;
import utils.FileUtils;


public class DatabaseConfig {
    private static final DatabaseConfig INSTANCE = new DatabaseConfig();
    private JSONObject dbConfig;

    public static DatabaseConfig getInstance() {
        return INSTANCE;
    }

    private DatabaseConfig(){}

    public JSONObject getDBJson() {
        return dbConfig;
    }

    public void setConfigUsingResource() throws FileUtilityException {
//        String resourcePath = System.getProperty(Constants.CARBC_HOME)
//                + "/src/main/resources/db.json";
//        this.dbConfig = new JSONObject(FileUtils.readFileContentAsText(resourcePath));
    }

}
