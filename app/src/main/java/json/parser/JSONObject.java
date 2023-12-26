package json.parser;

import java.util.HashMap;

import json.parser.interfaces.JSONItem;

public class JSONObject implements JSONItem {
    HashMap<String, Object> objects = new HashMap<>();
    // int tokens = 0;

    @Override
    public String toString() {
        return "JSONObject [objects=" + objects + "]";
    }

    public void addItem(String key, Object value) {
        objects.put(key, value);
    }

    @Deprecated
    public void addItem(Object item) {
        throw new UnsupportedOperationException("Use addItem(String key, Object value) instead.");
    }
}
