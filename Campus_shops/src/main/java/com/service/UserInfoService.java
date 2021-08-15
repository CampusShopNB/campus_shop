package com.service;

import com.entity.UserInfo;
import com.mapper.UserInfoMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hlt
 * @since 2019-12-21
 */
@Service
@Transactional
public class UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    /**查询用户信息*/
    public UserInfo LookUserinfo(String userid) {
        return userInfoMapper.LookUserinfo(userid);
    }
    /**分页查询不同角色用户信息*/
    public List<UserInfo> queryAllUserInfo(Integer page,Integer count,Integer roleid,Integer userstatus){
        return userInfoMapper.queryAllUserInfo(page,count,roleid,userstatus);
    }
    /**查看不同角色用户总数*/
    public Integer queryAllUserCount(Integer roleid){
        return userInfoMapper.queryAllUserCount(roleid);
    }
    /**添加用户信息*/
    public Integer userReg(UserInfo userInfo){
        return userInfoMapper.userReg(userInfo);
    }
    /**修改用户信息*/
    public Integer UpdateUserInfo(UserInfo userInfo){
        return userInfoMapper.UpdateUserInfo(userInfo);
    }
    /**查询用户的昵称和头像**/
    public UserInfo queryPartInfo(String userid){
        return userInfoMapper.queryPartInfo(userid);
    }

    /**根据学校名称，修改字段
     * 精准查询（不要like，比如华商VS华南理工有重复前缀）
     * 查询到一个List，存放UserInfo，这些对象的学校名称一样，且applyschoolstatus=1*/
    public Integer updateApplyStatusBySchoolName(String school){
        return userInfoMapper.updateApplyStatusBySchool(school);
    }
    /**作为上一个方法的补充。先查询*/
    public Integer queryApplyCountBySchool(String school){
        return userInfoMapper.queryApplyCountBySchool(school);
    }

    /**查询用户的评分*/
    public List<UserInfo> queryAllUsersStar(){
        return userInfoMapper.queryAllUsersStar();
    }

}
