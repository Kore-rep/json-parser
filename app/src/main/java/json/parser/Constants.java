package json.parser;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

public class Constants {
    public static Set<Character> JSON_WHITESPACE = new HashSet<>(Arrays.asList(' ', '\t', '\b', '\n', '\r'));
    public static Set<Character> JSON_SYNTAX = new HashSet<>(Arrays.asList('{', '}', ':', ',', '[', ']'));
    public static Set<Character> JSON_BOOLS_STARTS = new HashSet<>(Arrays.asList('t', 'T', 'f', 'F'));
    public static Set<Character> JSON_NUMBER_CHARS = new HashSet<>(
            Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '.', 'e', 'E'));
    public static char JSON_ESCPAE_CHARACTER = '\\';
}
