package com.mapper;

import com.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hlt
 * @since 2019-12-21
 */
public interface UserInfoMapper {
    /**查询用户信息*/
    UserInfo LookUserinfo(String userid);
    /**分页查询不同角色用户信息*/
    List<UserInfo> queryAllUserInfo(@Param("page") Integer page, @Param("count") Integer count, @Param("roleid") Integer roleid, @Param("userstatus") Integer userstatus);
    /**查看不同角色用户总数*/
    Integer queryAllUserCount(Integer roleid);
    /**添加用户信息*/
    Integer userReg(UserInfo userInfo);
    /**修改用户信息*/
    Integer UpdateUserInfo(UserInfo userInfo);
    /**查询用户的昵称和头像**/
    UserInfo queryPartInfo(String userid);

    /**根据学校名称，精准查询，查询到一个List，存放UserInfo，这些对象的学校名称一样，且applyschoolstatus=1*/
    Integer updateApplyStatusBySchool(String school);
    /**作为上一个方法的补充。先查询*/
    Integer queryApplyCountBySchool(String school);
}
