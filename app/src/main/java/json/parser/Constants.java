package json.parser;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

public class Constants {
    public static Set<Character> JSON_WHITESPACE = new HashSet<>(Arrays.asList(' ', '\t', '\b', '\n', '\r'));
    public static Set<Character> JSON_SYNTAX = new HashSet<>(Arrays.asList('{', '}', ':', ',', '[', ']'));
}
