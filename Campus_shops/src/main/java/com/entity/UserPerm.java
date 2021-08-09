package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类，对应数据表user_perm
 * </p>
 */
@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
@Data
@Accessors(chain = true)//链式写法
public class UserPerm implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 1普通用户 2管理员 3超级管理员
     */
	private Integer roleid;
    /**
     * 对应权限
     */
	private String perms;
    /**
     * 权限解释
     * 用户个人信息、设置管理员
     */
	private String mean;


}
