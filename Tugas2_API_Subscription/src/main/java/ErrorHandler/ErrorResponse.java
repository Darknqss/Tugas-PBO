package ErrorHandler;

import org.json.JSONObject;

public class ErrorResponse {

    public static String create(String message) {
        JSONObject json = new JSONObject();
        json.put("error", message);
        return json.toString();
    }
}

