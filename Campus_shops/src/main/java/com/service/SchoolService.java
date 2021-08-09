package com.service;

import com.entity.School;
import com.mapper.SchoolMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  学校 服务类
 * </p>
 */
@Service
@Transactional
public class SchoolService {
    @Autowired
    private SchoolMapper schoolMapper;

    /**新增学校*/
    public Integer addSchool(School school){
        return schoolMapper.addSchool(school);
    }

    /**修改学校（名称、代号等）
     * 包括：删除学校。修改状态为0不可用*/
    public Integer updateSchool(School school){
        return schoolMapper.updateSchool(school);
    }

    /**查询所有学校*/
    public List<School> queryAllSchool(){
        return schoolMapper.queryAllSchool();
    }
}
