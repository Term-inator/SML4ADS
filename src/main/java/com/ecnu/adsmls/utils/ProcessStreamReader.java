package com.ecnu.adsmls.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Deprecated
public class ProcessStreamReader extends Thread {
    private InputStream inputStream;

    public ProcessStreamReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void run() {
        InputStreamReader inputStreamReader = null;
        BufferedReader br = null;
        try {
            inputStreamReader = new InputStreamReader(
                    inputStream);
            br = new BufferedReader(inputStreamReader);
            // 打印信息
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            // 不打印信息
//            while (br.readLine() != null);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                br.close();
                inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
