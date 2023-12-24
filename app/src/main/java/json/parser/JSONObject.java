package json.parser;

import java.util.HashMap;

public class JSONObject {
    HashMap<String, Object> objects = new HashMap<>();

    @Override
    public String toString() {
        return "JSONObject [objects=" + objects + "]";
    }

    public void addItem(String key, Object value) {
        objects.put(key, value);
    }
}
