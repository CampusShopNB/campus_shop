
package com.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 提交学校申请实体类。对应数据表wantedschool
 * </p>
 * 属性和字段一致
 */
@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
@Data
@Accessors(chain = true)//链式写法
public class Wantedschool {
    /**
     * 主键id。
     */
    private String id;
    /**
     * 提交申请的userid
     */
    private String userid;
    /**
     * 提交申请的username，只是为了方便查看
     */
//    private String username;
    /**
     * 学校名字
     */
    private String schoolname;
    /**
     * 学校代号
     */
    private String schoolcode;
    /**
     * 处理情况（管理员是否处理了）
     * 0表示还没处理。1表示已经处理了（不再显示在管理员的审核列表上）
     */
    private Integer processstatus;
}