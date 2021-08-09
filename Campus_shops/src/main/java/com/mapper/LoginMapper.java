package com.mapper;

import com.entity.Login;

/**
 * <p>
 *  Mapper 接口
 * </p>
 */
public interface LoginMapper {
    /**注册*/
    Integer loginAdd(Login login);
    /**登录及判断用户是否存在*/
    Login userLogin(Login login);
    /**修改登录信息*/
    Integer updateLogin(Login login);
}