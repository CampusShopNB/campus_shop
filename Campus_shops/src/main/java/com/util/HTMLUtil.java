package com.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

public class HTMLUtil {
    public Boolean createHtml(String html){
        StringBuilder stringHtml = new StringBuilder();
        PrintStream printStream = null;
        try{
            printStream = new PrintStream(new FileOutputStream(".\\src\\main\\resources\\templates\\alipay\\test.html"));
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        stringHtml.append(html);
        try{
            printStream.println(stringHtml.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            printStream.close();
        }
        return true;
    }
}
