package json.parser;

import java.util.HashMap;

public class JSONObject {
    HashMap<String, Object> objects = new HashMap<>();
    // int tokens = 0;

    @Override
    public String toString() {
        return "JSONObject [objects=" + objects + "]";
    }

    public void addItem(String key, Object value) {
        objects.put(key, value);
    }

    // public void addToken() {
    // this.addTokens(1);
    // }

    // public void addTokens(int num) {
    // this.tokens += num;
    // }

    // public int getTokens() {
    // return tokens;
    // }
}
