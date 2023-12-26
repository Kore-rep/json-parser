package json.parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.*;

import json.parser.interfaces.JSONItem;

import java.text.NumberFormat;
import java.text.ParseException;
import java.lang.UnsupportedOperationException;

public class Parser {
    private static LinkedList<JSONToken<?>> tokens = new LinkedList<JSONToken<?>>();
    private static int maxNumTokens;

    public static void parse(String object) {
        try {
            lexicalAnalysis(object.trim());
            JSONItem obj = syntacticAnalysis();
            System.out.println(obj.toString());
            System.exit(0);
        } catch (UnsupportedOperationException | ParseException e) {
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

            int whitespaceCount = consumeWhitespace(currString);
            if (whitespaceCount > 0) {
                i += whitespaceCount;
                continue;
            }

            JSONToken<String> syntaxToken = lexJsonSyntax(currString);
            if (syntaxToken != null) {
                tokens.add(syntaxToken);
                i += syntaxToken.getStringLength();
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
                i += numberToken.getStringLength();
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

    /*
     * Consume whitespace until a non-whitespace char is found.
     */
    private static int consumeWhitespace(String object) {
        int counter = 0;
        while (Constants.JSON_WHITESPACE.contains(object.charAt(counter))) {
            counter++;
        }
        return counter;
    }

    private static JSONToken<String> lexJsonSyntax(String object) {
        char c = object.charAt(0);
        if (Constants.JSON_SYNTAX.contains(c)) {
            return new JSONToken<String>(String.valueOf(c));
        }
        return null;
    }

    private static JSONToken<String> lexString(String object) throws ParseException {
        if (object.charAt(0) != '"')
            return null;
        StringBuilder sb = new StringBuilder();
        char prev = ' ';
        for (int i = 1; i < object.length(); i++) {
            char c = object.charAt(i);
            if (c == '"' && prev != Constants.JSON_ESCPAE_CHARACTER)
                return new JSONToken<String>(sb.toString(), JSONTokenType.String);
            sb.append(c);
            prev = c;
        }
        throw new ParseException("Expected end of string quote", 0);
    }

    private static JSONToken<Boolean> lexBool(String object) throws ParseException {
        if (object.length() < 5)
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append(object.substring(0, 4));
        if (sb.toString().equals("true"))
            return new JSONToken<Boolean>(true, JSONTokenType.Boolean);
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
                    res = NumberFormat.getInstance(Locale.US).parse(object.substring(0, i));
                    return new JSONToken<Number>(res, JSONTokenType.Number, i);
                } catch (ParseException e) {
                    break;
                }
        }
        return null;
    }

    private static boolean lexNull(String object) {
        if (object.length() < 5)
            return false;
        StringBuilder sb = new StringBuilder();
        sb.append(object.substring(0, 4));
        if (sb.toString().toLowerCase().equals("null"))
            return true;
        return false;
    }

    private static JSONItem syntacticAnalysis() throws ParseException {
        JSONToken<?> t = tokens.peek();
        if (t == null)
            throw new ParseException("Not enough tokens to process", 0);
        switch (t.getType()) {
            case LeftBrace:
                return parseObject();
            case LeftBracket:
                return parseArray();
            default:
                throw new UnsupportedOperationException("Not a JSON object or Array");
        }
    }

    private static JSONArray parseArray() throws ParseException {
        JSONArray obj = new JSONArray();
        JSONToken<?> t = tokens.poll();
        if (t.getType() != JSONTokenType.LeftBracket) {
            throw new ParseException(String.format("Expected array bracket, got %s", t),
                    maxNumTokens - tokens.size());
        }

        t = tokens.peek();
        if (t.getType() == JSONTokenType.RightBracket) {
            tokens.poll();
            return obj;
        }

        while (!tokens.isEmpty()) {
            // Expect value (can be another object or array).
            Object value = new JSONObject();
            try {
                value = syntacticAnalysis();
            } catch (UnsupportedOperationException e) {
                t = tokens.poll();
                value = t.getValue();
            }
            obj.addItem(value);
            t = tokens.poll();

            // Expect closing bracket
            if (t.getType() == JSONTokenType.RightBracket) {
                return obj;
            }

            // Otherwise Expect comma
            if (t.getType() != JSONTokenType.Seperator) {
                throw new ParseException(String.format("Expected Comma after pair, got %s", t),
                        maxNumTokens - tokens.size());
            }
        }
        throw new ParseException(
                String.format("Expected end of array bracket, got %s", tokens.poll().getValue()),
                maxNumTokens - tokens.size());
    }

    private static JSONObject parseObject() throws ParseException {
        JSONObject obj = new JSONObject();
        JSONToken<?> t = tokens.poll();
        if (t.getType() != JSONTokenType.LeftBrace) {
            throw new ParseException(String.format("Expected object brace, got %s", t),
                    maxNumTokens - tokens.size());
        }
        t = tokens.peek();
        if (t.getType() == JSONTokenType.RightBrace) {
            tokens.poll();
            return obj;
        }

        while (!tokens.isEmpty()) {
            // Expect Key
            t = tokens.poll();
            if (t.getType() != JSONTokenType.String) {
                throw new ParseException(String.format("Expected string key, got %s", t), maxNumTokens - tokens.size());
            }
            String jsonKey = (String) t.getValue();
            Object value = new JSONObject();
            t = tokens.poll();
            // Expect Colon
            if (t.getType() != JSONTokenType.Colon) {
                throw new ParseException(String.format("Expected Colon after key, got %s", t),
                        maxNumTokens - tokens.size());
            }
            // Expect value (can be another object)
            try {
                value = syntacticAnalysis();
            } catch (UnsupportedOperationException e) {
                t = tokens.poll();
                value = t.getValue();
            }
            obj.addItem(jsonKey, value);
            t = tokens.poll();

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
                String.format("Expected end of object brace, got %s", tokens.poll().getValue()),
                maxNumTokens - tokens.size());
    }

    public static void clearTokens() {
        tokens.clear();
        maxNumTokens = 0;
    }
}
