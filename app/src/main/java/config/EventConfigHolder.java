package config;

import Exceptions.FileUtilityException;
import constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;
import utils.FileUtils;

public class EventConfigHolder {
    private static final EventConfigHolder INSTANCE = new EventConfigHolder();
    private JSONObject eventConfig;

    private EventConfigHolder(){}

    public static EventConfigHolder getInstance() {
        return INSTANCE;
    }

    public JSONObject getEventJson() {
        return eventConfig;
    }

    public void setConfigUsingResource() throws FileUtilityException {
        String resourcePath = System.getProperty(Constants.CARBC_HOME)
                + "/src/main/resources/event.json";
        try {
            this.eventConfig = new JSONObject(FileUtils.readFileContentAsText(resourcePath));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
