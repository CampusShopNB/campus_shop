package com.util;

import java.util.Random;

public class KeyUtil {
    /**
     * 生成唯一的主键
     * 格式:时间+随机数
     *
     * 为了防止重复 加个synchronized
     * */
    public static synchronized String genUniqueKey(){
        Random random=new Random();
        //六位随机数
        Integer number=random.nextInt(900000)+100000;
        return System.currentTimeMillis()+String.valueOf(number);
    }

    /**
     * 生成唯一的学生编号，
     * 格式:学校代号+当前时间(生成编号的时间，一般是上传学生证选学校的时间)+user_id后四位
     *
     * 为了防止重复 加个synchronized
     * */
//    public static synchronized String genUniqueStuIdBySchool(){
//        Random random=new Random();
//        //六位随机数
//        Integer number=random.nextInt(900000)+100000;
//        return System.currentTimeMillis()+String.valueOf(number);
//    }


    public static void main(String[] args){
        String s = KeyUtil.genUniqueKey();
        System.out.println(s);
    }
}
