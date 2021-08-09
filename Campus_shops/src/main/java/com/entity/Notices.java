package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 通知类，实体类，对应数据表notices
 * </p>
 * 属性个数与数据表字段个数一致
 */
@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
@Data
@Accessors(chain = true)//链式写法
public class Notices implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知id
     */
	private String id;
    /**
     * 用户id
     */
	private String userid;
    /**
     * 通知内容
     *
     * 内容可能是文本+html标签，后者可以引入超链接等
     * 示例：您的商品 <a href=/product-detail/1583938501381902202 style="color:#08bf91" target="_blank" >南极人2020春季新款男士韩版休闲牛仔外套</a> 已通过审核，快去看看吧。
     */
	private String whys;
    /**
     * 是否阅读 0未阅读 1已阅读
     */
	private Integer isread;
    /**
     * 通知类型
     *
     * 比如：商品审核、系统通知、评论、评论回复
     */
	private String tpname;
    /**
     * 通知时间
     */
	private Date nttime;
    /**
     * 是否为新通知 1是 2不是
     */
    private Integer latest;

}
