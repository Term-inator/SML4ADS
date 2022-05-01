package com.ecnu.adsmls.verifier;

import java.util.Arrays;

public class Verifier {
    public static void verify(String[] args) {
        final String ADSML_PATH = args[0];
        final String JSON_PATH = ADSML_PATH + args[1];
        final String XML_PATH = args[2];

        try {
            System.out.println(Arrays.toString(args));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
