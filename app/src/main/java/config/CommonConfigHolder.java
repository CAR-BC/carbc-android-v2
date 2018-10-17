package config;


import Exceptions.FileUtilityException;
//import com.sun.javafx.runtime.SystemProperties;
import constants.Constants;
import org.json.JSONObject;
import utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 */
public final class CommonConfigHolder {
    private static final CommonConfigHolder INSTANCE = new CommonConfigHolder();
    private JSONObject configJson;

    private CommonConfigHolder() {
    }

    public static CommonConfigHolder getInstance() {
        return INSTANCE;
    }

    public JSONObject getConfigJson() {
        return configJson;
    }

    public void setConfigUsingResource(String peerName) {
        String resourcePath = System.getProperty(Constants.CARBC_HOME)
                + "/src/main/resources/" + peerName + ".json";
//        try {
//            this.configJson = new JSONObject(FileUtils.readFileContentAsText(resourcePath));
//        } catch (FileUtilityException e) {
//            e.printStackTrace();
//        }
    }


    public void savePeersIPsandPorts(String data) throws IOException {
        String resourcePath = System.getProperty(Constants.CARBC_HOME)
                + "/src/main/resources/";
        try (FileWriter file = new FileWriter(resourcePath + "peersDetails.json")) {
            file.write(data);

            System.out.println("written into file");

        }


    }

}

