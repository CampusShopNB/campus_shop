package com.util;

/**
 * @Description: 状态码工具
 */
public class StatusCode {

    //发送成功
    public static final int SMS = 200;
    //成功
    public static final int OK = 200;
    //失败
    public static final int ERROR = 201;
    //用户名密码错误
    public static final int LOGINERROR = 202;
    //权限不足
    public static final int ACCESSERROR = 203;
    //远程调用失败
    public static final int REMOTEERROR = 204;
    //重复操作
    public static final int REPERROR = 205;

    /**
     * 自己新增：数据重复（数据库中已经有了）
     */
    public static final int ALREADYEXIST = 206;

    //找不到
    public static final int FINDERROR = 404;
    //服务器错误
    public static final int SERVERERROR = 500;


}
