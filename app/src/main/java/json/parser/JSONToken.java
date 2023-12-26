package json.parser;

public class JSONToken<T> {
    private T value;
    private JSONTokenType type;
    private int length;

    public JSONToken(T value) {
        this.value = value;
        inferType();
    }

    public JSONToken(T value, JSONTokenType type) {
        this.value = value;
        this.type = type;
    }

    public JSONToken(T value, JSONTokenType type, int length) {
        this.value = value;
        this.type = type;
        this.length = length;
    }

    public JSONTokenType getType() {
        return this.type;
    }

    public T getValue() {
        return this.value;
    }

    public int getStringLength() {
        return this.length;
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
                break;
            case "[":
                type = JSONTokenType.LeftBracket;
                break;
            case "]":
                type = JSONTokenType.RightBracket;
                break;
            default:
                throw new UnsupportedOperationException("Unable to infer type, specify in constructor.");
        }
        length = 1;
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
