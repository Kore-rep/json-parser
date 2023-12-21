package json.parser;

public class JSONToken {
    private String value;
    private JSONTokenType type;

    public JSONToken(String value) {
        this.value = value;
        inferType();
    }

    public JSONToken(String value, JSONTokenType type) {
        this.value = value;
        this.type = type;
    }

    public JSONTokenType getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    private void inferType() {
        switch (value) {
            case "{":
                type = JSONTokenType.LeftBrace;
            case "}":
                type = JSONTokenType.RightBrace;
            case ",":
                type = JSONTokenType.Seperator;
        }
    }
}

enum JSONTokenType {
    LeftBrace,
    RightBrace,
    Seperator,
    String
}
