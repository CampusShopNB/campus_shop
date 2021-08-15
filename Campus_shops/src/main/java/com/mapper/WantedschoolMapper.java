package com.mapper;

import com.entity.Wantedschool;

import java.util.List;

/**
 * <p>
 *  请求新增学校Mapper接口
 * </p>
 */
public interface WantedschoolMapper {
    /**新增记录*/
    Integer addSchool(Wantedschool wantedschool);

    /**修改学校处理状态，字段schoolproessstatus
     * 管理员点击审核按钮时使用*/
    Integer updateSchoolStatus(Wantedschool wantedschool);

    /**分页查看所有待处理记录（在后台给管理员使用）
     * select * from wantedschool where processstatus = 0*/
    List<Wantedschool> queryAllUnprocessSchool(Integer page, Integer count);

    /**查询所有待处理记录的总数    select count(*)*/
    Integer queryAllUnprocessSchoolCount();

    /**新增记录前，看下表中有没有学校，有就不要再添加了。*/
    Integer querySchoolname(String schoolname);

    /**通过id查询所有字段，主要是为了userid和学校名称*/
    Wantedschool queryAllFielById(String id);
}