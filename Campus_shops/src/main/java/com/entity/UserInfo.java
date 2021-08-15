package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *  实体类，对应数据表user_id
 * </p>
 *
 * 实体类相比数据表，多了以下属性
 * roleid,vercode,id,avatar,content,type,toid,sendtime
 *
 * @author
 * @since
 */
@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
@Data
@Accessors(chain = true)//链式写法
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
	private String userid;
    /**
     * 角色id 1普通用户 2管理员 3超级管理员
     */
    private Integer roleid;
    /**
     * 用户名
     */
	private String username;
    /**
     * 用户密码
     */
	private String password;
    /**
     * 手机号
     */
	private String mobilephone;
    /**
     * 用户邮箱
     */
	private String email;
    /**
     * 用户头像
     */
	private String uimage;
    /**
     * 用户性别
     */
	private String sex;
    /**
     * 学校
     */
	private String school;
    /**
     * 院系
     */
	private String faculty;
    /**
     * 入学时间
     */
	private String startime;
    /**
     * 1正常 0封号
     */
	private Integer userstatus;
    /**
     * 注册时间
     */
	private Date createtime;
    /**
     * 验证码
     */
	private String vercode;
    /**
     * 在线状态
     */
    private String status;//在线状态 online：在线、hide：隐身

    /**
     * 学生证图片
     */
    private String stuidcard;
    /**
     * 是否提交了新增学校的申请。默认0没有，1有但未通过（包括第一位提交的和其他无法提交的），2表示已经通过
     */
    private Integer applyschoolstatus ;

    /**
     * 我的星级
     */
    private Double star;

    //补充的属性
    private String id; //我的ID
    private String sign; //我的签名
    private String avatar;//我的头像
    private String content;   //聊天内容
    private String type; //消息类型
    private String toid; //聊天窗口的选中的用户或者群组的id
    private Date sendtime;  //消息发送时间
}
