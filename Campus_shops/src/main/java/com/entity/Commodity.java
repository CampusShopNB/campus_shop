package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商品实体类，对应数据表commodity
 * </p>
 * 数据表commodity有16个字段，这里有18个属性，比数据表多了common2和otherimg
 */
@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
@Data//setter、getter
@Accessors(chain = true)//链式写法
public class Commodity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    private String commid;
    /**
     * 商品名
     */
    private String commname;
    /**
     * 商品描述
     */
    private String commdesc;
    /**
     * 视频
     */
    private String videourl;
    /**
     * 原价
     */
    private BigDecimal orimoney;
    /**
     * 售价
     */
    private BigDecimal thinkmoney;
    /**
     * 商品所在学校
     */
    private String school;
    /**
     * 发布时间
     */
    private Date createtime;
    /**
     * 修改时间
     */
    private Date updatetime;
    /**
     * 结束时间
     */
    private Date endtime;
    /**
     * 0违规
     * 1正常
     * 2删除
     * 3待审核
     * 4已完成交易(注释没写，CommodityController.java中的方法toeditgoods提醒的)
     */
    private Integer commstatus;
    /**
     * 常用选项：自提，可小刀，不议价等选项
     */
    private String common;
    /**
     * 常用类别字段
     *
     * 感觉其实common2和属性common合起来就对应数据表commodity中的字段common
     * 只不过数据表中字段common是可小刀、仅自提，这里对、进行分割，让common=可小刀，让common2=仅自提
     * */
    private String common2;
    /**
     * 商品其他图集合
     * */
    private List<String> otherimg;

    /**
     * 浏览量
     */
    private Integer rednumber;
    /**
     * 商品类别
     */
    private String category;
    /**
     * 简介图
     * 我猜这个是主图，然后CommodityImages对应的是商品的其他图片
     */
    private String image;
    /**
     * 用户id
     */
    private String userid;

}
