package json.parser;

public class JSONToken<T> {
    private T value;
    private JSONTokenType type;

    public JSONToken(T value) {
        this.value = value;
        inferType();
    }

    public JSONToken(T value, JSONTokenType type) {
        this.value = value;
        this.type = type;
    }

    public JSONTokenType getType() {
        return this.type;
    }

    public T getValue() {
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
        return this.value.toString();
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
    Boolean,
    Number,
    Null
}
