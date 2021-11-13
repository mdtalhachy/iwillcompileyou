package com.talhachy.iwillcompileyou;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.talhachy.iwillcompileyou.Token.Type.*;

import com.talhachy.iwillcompileyou.Token.Type;
import com.talhachy.iwillcompileyou.errors.TokenizerError;


public class Tokenizer {


    public static ArrayList<Token> tokenize(String input) {

        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();
        System.out.println();
        return tokenizer.result;
    }

    private static final Pattern rIntConst = Pattern.compile("[0-9]+");
    private static final Pattern rBoolConst = Pattern.compile("true|false");
    private static final Pattern rIdentifier = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");

    private static final HashMap<String, Token.Type> keywords = new HashMap<String, Token.Type>();

    static {
        keywords.put("if", IF);
        keywords.put("then", THEN);
        keywords.put("else", ELSE);
        keywords.put("while", WHILE);
        keywords.put("do", DO);
    }

    private ArrayList<Token> result;
    private String input;
    private int line;
    private int col;

    public Tokenizer(String input) {

        this.result = new ArrayList<Token>();
        this.input = input;
        this.line = 1;
        this.col = 1;

    }

    private void tokenize() {

        skipWhiteSpace();

        while(!input.isEmpty()) {
            boolean ok =
                    tryTok("(", LPAREN) || tryTok(")", RPAREN) ||
                            tryTok("{", LBRACE) || tryTok("}", RBRACE) ||
                            tryTok(";", SEMICOLON) ||
                            tryTok(",", COMMA) ||
                            tryTok("+", PLUS) || tryTok("-", MINUS) ||
                            tryTok("*", TIMES) || tryTok("/", DIV) || tryTok("%", MOD) ||
                            tryTok("!", NOT) ||
                            tryTok(":=", ASSIGN) ||
                            tryTok(":", COLON) ||
                            tryTok("==", EQ) || tryTok("<>", NEQ) ||
                            tryTok("<=", LTE) || tryTok(">=", GTE) ||
                            tryTok("<", LT) || tryTok(">", GT) ||
                            tryRegex(rIntConst, INTCONST) ||
                            tryRegex(rBoolConst, BOOLCONST) ||
                            tryKeywordOrIdentifier();

            if(!ok) {
                throw new TokenizerError("Cannot tokenize at line " + line + " col " + col);
            }

            skipWhiteSpace();

        }

    }

    private void skipWhiteSpace() {

        int i = 0;

        while(i < input.length() && Character.isWhitespace(input.charAt(i))) {
            i++;
        }

        consumeInput(i);

    }


    private boolean tryTok(String expected, Token.Type tokentype) {
        if (input.startsWith(expected)) {
            result.add(new Token(tokentype, expected, line, col));
            consumeInput(expected.length());
            return true;
        } else {
            return false;
        }
    }



    private boolean tryRegex(Pattern pattern, Token.Type type){

        Matcher m = pattern.matcher(input);

        if (m.lookingAt()) {

            result.add(new Token(type, m.group(), line, col));
            consumeInput(m.end());
            return true;

        } else {

            return false;

        }
    }



    private boolean tryKeywordOrIdentifier() {

        if (tryRegex(rIdentifier, IDENTIFIER)) {

            Token token = result.get(result.size() - 1);
            Token.Type kwType = keywords.get(token.text);

            if (kwType != null) {
                token = new Token(kwType, token.text, token.line, token.col);
                result.set(result.size() - 1, token);
            }

            return true;
        } else {
            return false;
        }
    }



    private void consumeInput(int amount) {

        for (int i = 0; i < amount; ++i) {
            char c = input.charAt(i);
            if(c == '\n') {
                line++;
                col = 1;
            } else if (c == '\r') {
                //Ignore
            } else {
                col++;
            }
        }

        input = input.substring(amount);
    }
}

