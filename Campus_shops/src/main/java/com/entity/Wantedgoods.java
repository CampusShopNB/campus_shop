
package com.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 求购商品实体类。对应数据表wantedgoods
 * </p>
 * 属性和字段一致
 */
@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
@Data
@Accessors(chain = true)//链式写法
public class Wantedgoods {
    /**
     * 主键id。
     */
    private String id;
    /**
     * 提交求购信息的userid
     * 去user_info查询用户名、头像、手机号、邮箱
     */
    private String userid;
    /**
     * 求购标题
     */
    private String wanttitle;
    /**
     * 求购内容
     */
    private String wantcontent;
    /**
     * 求购物品的类别
     * （用于接收通知，如果有人发布相同类别的商品，
     * 可以在插入commodity表后，或者管理员审核通过后通知）
     */
    private String wantcategory;
    /**
     * 期望价格上限
     */
    private BigDecimal expectprice;
    /**
     * 期望交易地点
     */
    private String expectplace;
    /**
     * 发布时间
     */
    private Date createtime;
    /**
     * 更新时间
     */
    private Date updatetime;
    /**
     * 求购状态
     * 默认0待审核，
     * 1审核通过，
     * 2审核不通过，
     * 3删除
     */
    private Integer wantstatus;


    /**补充属性*/
    /**
     * 用户名
     */
    private String username;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户头像
     */
	private String uimage;
    /**
     * 前台类别颜色
     * 蓝色，3C数码，#428BCA;
     * 粉色，美妆，#F3BABA
     * 绿色，生活用品，#5CB85C
     * 蓝色，服饰，#5BC0DE
     * 橙色，出行，#F0AD4E
     * 书籍，#D9534F
     * 紫色，其他，#8E4FF4;
     * default:蓝色#428BCA
     */
//    private String categorycolor;
}