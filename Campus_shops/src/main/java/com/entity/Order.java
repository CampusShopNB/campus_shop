
package com.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 订单记录实体类。对应数据表order
 * </p>
 * 属性和字段一致
 */
@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
@Data
@Accessors(chain = true)//链式写法
public class Order {
    private static final long serialVersionUID = 1L;

    /**
     *主键，订单id
     */
    private String id;
    /**
     * 商品id商品id，商品的唯一标识
     */
    private String commid;
    /**
     * 商品名称
     */
    private String commname;
    /**
     * 商品描述
     */
    private String commdesc;
    /**
     * 售价
     */
    private BigDecimal thinkmoney;
    /**
     * 售出时间，以卖家售出操作为准
     */
    private Date soldtime;
    /**
     * 买家id
     */
    private String buyerid;
    /**
     * 买家用户名
     */
    private String buyername;
    /**
     * 卖家id
     */
    private String sellerid;
    /**
     * 卖家用户名
     */
    private String sellername;
    /**
     * 订单状态
     * 初始状态0 InitialOrderState
     * 已支付1 PaidOrderState
     * 已发货/已达成交易2 DeliveredOrderState
     * 已收货3 ReceivedOrderState
     * 已售出4 SoldOrderState
     * 已完成5 CompletedOrderState
     * 已取消6 CancelledOrderState
     * 已删除7 Deleted
     */
    private Integer orderstatus;
    /**
     * 给买家的评分。默认为0
     */
    private Integer startobuyer;
    /**
     * 给卖家的评分。默认为0
     */
    private Integer startoseller;
}