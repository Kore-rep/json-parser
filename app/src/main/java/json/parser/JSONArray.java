package json.parser;

import java.util.ArrayList;
import java.util.List;

import json.parser.interfaces.JSONItem;

public class JSONArray implements JSONItem {
    private List<Object> list = new ArrayList<>();

    public void addItem(Object item) {
        list.add(item);
    }

    @Deprecated
    public void addItem(String key, Object value) {
        throw new UnsupportedOperationException("Use addItem(Object item) instead.");
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
