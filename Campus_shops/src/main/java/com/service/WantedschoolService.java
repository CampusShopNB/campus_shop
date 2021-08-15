package com.service;

import com.entity.Wantedschool;
import com.mapper.WantedschoolMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  请求新增学校 服务类
 * </p>
 */
@Service
@Transactional
public class WantedschoolService {
    @Autowired
    private WantedschoolMapper wantedschoolMapper;

    /**新增记录
     * Wantedschool中包含了userid
     * */
    public Integer addSchool(Wantedschool wantedschool){
        return wantedschoolMapper.addSchool(wantedschool);
    }

    /**修改学校处理状态，字段schoolproessstatus
     * 改为1表示已经处理，相当于删除记录 */
    public Integer updateSchoolStatus(Wantedschool wantedschool){
        return wantedschoolMapper.updateSchoolStatus(wantedschool);
    }

    /**分页查看所有待处理记录（在后台给管理员使用）
     * select * from wantedschool where processstatus = 0*/
    public List<Wantedschool> queryAllUnprocessSchool(Integer page, Integer count){
        return wantedschoolMapper.queryAllUnprocessSchool(page,count);
    }

    /**查询所有待处理记录的总数    select count(*)*/
    public Integer queryAllUnprocessSchoolCount(){
        return wantedschoolMapper.queryAllUnprocessSchoolCount();
    }

    /**新增记录前，看下表中有没有学校，有就不要再添加了。*/
    public Integer querySchoolname(String schoolname){
        return wantedschoolMapper.querySchoolname(schoolname);
    }

    /**通过id查询所有字段，主要是为了userid和学校名称*/
    public Wantedschool queryAllFielById(String id){
        return wantedschoolMapper.queryAllFielById(id);
    }
}