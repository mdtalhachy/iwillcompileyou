package com.talhachy.iwillcompileyou;

import java.util.ArrayList;

public class Compiler {

    public static void main(String[] args) {

        String inpString = "(3 + 3);";

        ArrayList<Token> tokens = Tokenizer.tokenize(inpString);

        for(Token i: tokens) {
            System.out.println(i);
        }
    }

}
