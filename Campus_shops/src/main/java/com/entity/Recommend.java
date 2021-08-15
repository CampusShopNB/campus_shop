package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 推荐商品实体类。对应数据表recommend
 * </p>
 * 属性和字段一致
 */
@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
@Data
@Accessors(chain = true)//链式写法
public class Recommend {
    private static final long serialVersionUID = 1L;

    /**
     *主键
     */
    private String id;
    /**
     * 商品id，商品的唯一标识。commodity表的主键
     */
    private String commid;
    /**
     * 显示在主页的商品名称。不一定是商品名称（后者有时过于冗长）
     */
    private String recomname;
    /**
     * 显示在主页的商品图片
     * 需要重新上传，不一定是商品主图（为了营销会做新图），但必须png且背景透明
     */
    private String recomimg;
    /**
     * 显示在主页的推荐语录
     * 不一定是商品描述
     */
    private String recomdesc;
    /**
     * 推荐商品的状态
     *
     * 本来：0待审核、1正在首页展示、2审核通过待首页展示、3审核不通过、4商品已被下架
     * 0表示待审核，1表示审核通过并显示在首页（即正在首页推荐展示），2表示审核通过但是待推荐（最多推荐2个商品），即暂时还不显示在首页推荐
     * 3表示审核不通过，4表示商品已经下架，不再显示在首页（与commodity的状态4一致）
     *
     * 现在变更为：
     * 0表示待审核，
     * 1表示审核通过（会从中按时间顺序选两个展示在首页）
     * 2表示审核不通过，
     * 3表示商品已经下架（与commodity的状态4一致）
     */
    private Integer recomstatus;
    /**
     * 更新时间（用户请求推荐、审核通过等）
     */
    private Date updatetime;
}