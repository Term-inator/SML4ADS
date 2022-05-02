package com.ecnu.adsmls.verifier;

import com.ecnu.adsmls.verifier.convert.src.main.java.Convert;

import java.util.Arrays;

public class Verifier {
    public static void verify(String[] args) {
        final String ADSML_PATH = args[0];
        final String JSON_PATH = ADSML_PATH + args[1];
        final String XML_PATH = args[2];

        try {
            System.out.println(Arrays.toString(args));
            Convert.ADSML2Uppaal(ADSML_PATH, JSON_PATH, XML_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
