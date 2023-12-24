package json.parser;

public class JSONToken {
    private Object value;
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

    public Object getValue() {
        return this.value;
    }

    private void inferType() {
        switch ((String) value) {
            case "{":
                type = JSONTokenType.LeftBrace;
                break;
            case "}":
                type = JSONTokenType.RightBrace;
                break;
            case ",":
                type = JSONTokenType.Seperator;
                break;
            case ":":
                type = JSONTokenType.Colon;
        }
    }

    @Override
    public String toString() {
        return this.value + " of type " + this.type;
    }
}

enum JSONTokenType {
    LeftBrace,
    RightBrace,
    LeftBracket,
    RightBracket,
    Seperator,
    String,
    Colon,
    Value
}
