package json.parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.lang.UnsupportedOperationException;

public class Parser {
    private static LinkedList<JSONToken<?>> tokens = new LinkedList<JSONToken<?>>();
    private static int maxNumTokens;

    public static void parse(String object) {
        try {
            lexicalAnalysis(object.trim());
            JSONObject obj = syntacticAnalysis();
            System.out.println(obj.toString());
            System.exit(0);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    };

    public static void parse(Path pathToFile) throws IOException {
        parse(Files.readString(pathToFile));
    }

    private static void lexicalAnalysis(String object) throws ParseException {
        int i = 0;
        while (i < object.length()) {
            String currString = object.substring(i, object.length());
            char c = object.charAt(i);
            if (Constants.JSON_SYNTAX.contains(c)) {
                tokens.add(new JSONToken<String>(String.valueOf(c)));
                i++;
                continue;
            }
            if (Constants.JSON_WHITESPACE.contains(c)) {
                // Ignore whitespace
                i++;
                continue;
            }

            JSONToken<String> stringToken = lexString(currString);
            if (stringToken != null) {
                tokens.add(stringToken);
                i += stringToken.getValue().length() + 2;
                continue;
            }

            JSONToken<Boolean> boolToken = lexBool(currString);
            if (boolToken != null) {
                tokens.add(boolToken);
                i += boolToken.getValue().toString().length();
                continue;
            }

            JSONToken<Number> numberToken = lexNumber(currString);
            if (numberToken != null) {
                tokens.add(numberToken);
                i += numberToken.getValue().toString().length();
                continue;
            }

            if (lexNull(currString)) {
                tokens.add(new JSONToken<Object>(null, JSONTokenType.Null));
                i += 4;
                continue;
            }
            throw new ParseException(String.format("Unable to tokenize char '%s' at position %d", c, i), i);
        }
        maxNumTokens = tokens.size();
    }

    // Expect first character to be first " of string to be lexed
    private static JSONToken<String> lexString(String object) throws ParseException {
        if (object.charAt(0) != '"')
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < object.length(); i++) {
            char c = object.charAt(i);
            if (c == '"')
                return new JSONToken<String>(sb.toString(), JSONTokenType.String);
            sb.append(c);
        }
        throw new ParseException("Expected end of string quote", 0);
    }

    private static JSONToken<Boolean> lexBool(String object) throws ParseException {
        StringBuilder sb = new StringBuilder();
        sb.append(object.substring(0, 4));
        if (sb.toString().equals("true"))
            return new JSONToken<Boolean>(true, JSONTokenType.Boolean);
        // False
        sb.append(object.charAt(4));
        if (sb.toString().equals("false"))
            return new JSONToken<Boolean>(false, JSONTokenType.Boolean);
        return null;
    }

    private static JSONToken<Number> lexNumber(String object) {
        Number res = null;
        for (int i = 0; i < object.length(); i++) {
            char c = object.charAt(i);
            if (!Constants.JSON_NUMBER_CHARS.contains(c))
                try {
                    res = NumberFormat.getInstance().parse(object.substring(0, i));
                    return new JSONToken<Number>(res, JSONTokenType.Number);
                } catch (ParseException e) {
                    break;
                }
        }
        return null;
    }

    private static boolean lexNull(String object) {
        StringBuilder sb = new StringBuilder();
        sb.append(object.substring(0, 4));
        if (sb.toString().toLowerCase().equals("null"))
            return true;
        return false;
    }

    private static JSONObject syntacticAnalysis() throws ParseException {
        JSONToken<?> t = tokens.peek();
        if (t == null)
            throw new ParseException("Not enough tokens to process", 0);
        switch (t.getType()) {
            case LeftBrace:
                tokens.poll();
                return parseObject();
            case LeftBracket:
                return parseArray();
            default:
                throw new UnsupportedOperationException("Base value");

        }
    }

    private static JSONObject parseArray() {
        throw new UnsupportedOperationException("Not implemented");
    }

    private static JSONObject parseObject() throws ParseException {
        JSONObject obj = new JSONObject();
        JSONToken<?> t = tokens.peek();
        if (t.getType() == JSONTokenType.RightBrace) {
            return obj;
        }

        while (!tokens.isEmpty()) {
            // Expect Key
            t = tokens.poll();
            if (t.getType() != JSONTokenType.String) {
                throw new ParseException(String.format("Expected string key, got %s", t), maxNumTokens - tokens.size());
            }
            // obj.addToken();
            String jsonKey = (String) t.getValue();
            Object value = new JSONObject();
            t = tokens.poll();
            // Expect Colon
            if (t.getType() != JSONTokenType.Colon) {
                throw new ParseException(String.format("Expected Colon after key, got %s", t),
                        maxNumTokens - tokens.size());
            }
            // obj.addToken();
            t = tokens.poll();
            // Expect value (can be another object)
            try {
                value = syntacticAnalysis();
                // tokensToAdd = ((JSONObject) value).getTokens();
                // obj.addTokens(tokensToAdd);
            } catch (UnsupportedOperationException e) {
                value = t.getValue();
                // obj.addToken();
            }
            obj.addItem(jsonKey, value); // Need to be able to add number of tokens consumed by nested objects.
            t = tokens.poll();

            // obj.addToken();
            // Expect closing bracket
            if (t.getType() == JSONTokenType.RightBrace) {
                return obj;
            }

            // Otherwise Expect comma
            if (t.getType() != JSONTokenType.Seperator) {
                throw new ParseException(String.format("Expected Comma after pair, got %s", t),
                        maxNumTokens - tokens.size());
            }
        }
        throw new ParseException(
                String.format("Expected end of object bracket, got %s", tokens.poll().getValue()),
                maxNumTokens - tokens.size());
    }
}
