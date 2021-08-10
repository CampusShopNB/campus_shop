package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 学校列表实体类。对应数据表school
 * </p>
 * 属性和字段一致
 */
@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
@Data
@Accessors(chain = true)//链式写法
public class School {
    /**
     * 主键
     */
    private String id;
    /**
     * 学校代号。教育部统一规定院校代号
     */
    private String schoolcode;
    /**
     * 学校名称
     */
    private String schoolname;
    /**
     * 表示学校状态，0为默认为1正常
     */
    private Integer status;

}