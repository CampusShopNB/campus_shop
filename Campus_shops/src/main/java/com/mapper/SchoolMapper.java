package com.mapper;

import com.entity.School;

import java.util.List;

/**
 * <p>
 *  学校Mapper接口
 * </p>
 */
public interface SchoolMapper {
    /**新增学校*/
    Integer addSchool(School school);
    /**修改学校（名称、代号等）
     * 包括：删除学校。修改状态为0不可用*/
    Integer updateSchool(School school);
    /**查询所有学校*/
    List<School> queryAllSchool();
}
