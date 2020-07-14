/* 
 * Authored by Jules W-G on 7/13
 * 
 * This static class has one public method, jLex, which
 * will take a buffered reader and return a single String
 * of the text in the java document.
 * 
 * based on implementation found at
 * https://www.cc.gatech.edu/gvu/people/Faculty/hudson/java_cup/classes.v0.9e/java_cup/lexer.java
 */

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SimpleLexer {
    /* never used */
    private SimpleLexer() {}

    /* keep track of next two characters from the document */
    protected static int nextChar;
    protected static int nextChar2;
    protected static int EOF_CHAR = -1;

    /* the 51 java keywords and "true", "false", "null" */
    protected static Hashtable<String, Integer> keywords;

    /* input and output */
    protected static BufferedReader in;
    protected static StringBuilder result; 

    /* Returns a complete String produced from java source code */
    public static String jLex(BufferedReader input) throws IOException {
        // set up the lexer and feed it the input reader
        setup(input);
        nextChar = in.read();
        nextChar2 = in.read();

        // loop through the whole file
        while (nextChar != EOF_CHAR) {
            // case 1: whitespace
            if (Character.isWhitespace(nextChar)) {
                advance();
                continue;
            } else if (nextChar == '/') { // case 2: potential comments
                if (nextChar2 == '/') { // subcase: "//" comments
                    while (nextChar != '\n')
                        advance();
                } else if (nextChar2 == '*') {  // subcase: "/* comments"
                    while (nextChar != '*' || nextChar2 != '/')
                        advance();
                    advance();
                    advance();
                } else { // subcase: not a comment
                    result.append((char)nextChar);
                    advance();
                }
            } else if (Character.isJavaIdentifierStart(nextChar)) { // case 3: start of a token
                result.append(nextToken());
            } else if (nextChar == '\"' || nextChar == '\'') { // case 4: string literal
                result.append(nextStringLiteral());
            } else if (nextChar == EOF_CHAR) { // case 5: EOF_CHAR 
                break;
            } else { // case 6: some other character
                result.append((char)nextChar);
                advance();
            }
        }
        return result.toString();
    }

    /* initialize static instance variables. */
    private static void setup(BufferedReader input) {
        result = new StringBuilder();
        in = input;
        keywords = new Hashtable<>(120);

        String[] allKeywords = {"abstract", "assert", "boolean", "break", "byte", "case",
                            "catch", "char", "class", "const", "continue", "default", "do",
                            "double", "else", "enum", "extends", "final", "finally", "float",
                            "for", "goto", "if", "implements", "import", "instanceof", "int",
                            "interface", "long", "native", "new", "package", "private",
                            "protected", "public", "return", "short", "static", "strictfp",
                            "super", "switch", "synchronized", "this", "throw", "throws",
                            "transient", "try", "void", "volatile", "while",
                            "true", "false", "null"};
        for (String elem : allKeywords )
            keywords.put(elem, 0);
    }

    /* read the next character from the file */
    private static void advance() throws IOException {
        nextChar = nextChar2;
        if (nextChar == EOF_CHAR) {
            nextChar2 = EOF_CHAR; //maybe dont need
        } else {
            nextChar2 = in.read();
        }
    }

    /* returns "V" for all identifiers, and the string if a keyword */
    private static String nextToken() throws IOException {
        StringBuilder sBuilder = new StringBuilder();
        while (Character.isJavaIdentifierPart(nextChar)) {
            sBuilder.append((char)nextChar);
            advance();
        }
        String token = sBuilder.toString();
        
        // if this token is a keyword return it, else return "V"
        if (keywords.get(token) != null) {
            return token;
        } else {
            return "V";
        }
    }

    /* return a string literal */
    private static String nextStringLiteral() throws IOException {
        StringBuilder sBuilder = new StringBuilder();
        int startingChar = nextChar;
        sBuilder.append((char)nextChar); // append the first apostrophe
        advance();
        while (nextChar != startingChar) {
            if (nextChar == '\\') {
                sBuilder.append(nextEscapeSequence());
            } else {
                sBuilder.append((char)nextChar);
                advance();
            }
        }
        sBuilder.append((char)nextChar);
        advance();
        return sBuilder.toString();
    }

    /* return the two-character escape sequence */
    private static String nextEscapeSequence() throws IOException {
        assert nextChar == '\\' : "Not start of escape sequence";
        String seq = "" + (char)nextChar + (char)nextChar2;
        advance();
        advance();
        return seq;
    }

    // debugging
    public static void main(String[] args) throws IOException {
        Path file = Paths.get("testJava.java");
        String reducedFile = SimpleLexer.jLex(Files.newBufferedReader(file));
        System.out.println(reducedFile + "\n");
        reducedFile = SimpleLexer.jLex(Files.newBufferedReader(file));
        System.out.println(reducedFile);
    }
}
