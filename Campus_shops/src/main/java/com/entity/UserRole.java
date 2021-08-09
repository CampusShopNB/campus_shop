package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类，对应数据表user_role
 * </p>
 */
@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
@Data
@Accessors(chain = true)//链式写法
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

	private String userid;
    /**
     * 1普通用户 2管理员 3超级管理员
     */
	private Integer roleid;
    /**
     * 身份
     * 网站用户，管理员，超级管理员
     */
	private String identity;


}
