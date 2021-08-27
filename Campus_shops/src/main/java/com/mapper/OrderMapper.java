package com.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  订单OrderMapper接口
 * </p>
 * 注意没有delete操作。如果取消收藏，直接update，修改相关信息和收藏状态即可，以此减少数据库的操作
 */
public interface OrderMapper extends BaseMapper<Order>{
    /**添加订单*/
    Integer addOrder(Order order);

    /**删除订单（看似delete，实则update订单状态为已删除）
     * 合并到修改订单状态updateOrder方法中*/
//    Integer deleteOrder(Order order);

    /**修改订单状态*/
    Integer updateOrder(Order order);
    /**根据订单id,sellerid,修改订单的startoseller给卖家的评分*/
    Integer updateStartoseller(Order order);
    /**根据订单id,sellerid,修改订单的startobuyer给买家的评分*/
    Integer updateStartobuyer(Order order);


    /**分页查看所有购入订单内容*/
    List<Order> queryAllBuyInOrder(@Param("page") Integer page, @Param("count") Integer count, @Param("buyerid") String buyerid);
    /**分页查看所有售出订单内容*/
    List<Order> queryAllSellOutOrder(@Param("page") Integer page, @Param("count") Integer count, @Param("sellerid") String sellerid);

    /**查询购入订单的总数*/
    Integer queryBuyInOrderCount(String buyerid);
    /**查询售出订单的总数*/
    Integer querySellOutOrderCount(String sellerid);

    /**查询订单状态（暂时不清楚作用，先留着）*/
//    Order queryOrderStatus(Order order);

    /**查询购入订单状态（估计是下拉列表进行分类）*/
    Order queryBuyInOrderStatus(Order order);
    /**查询售出订单状态（估计是下拉列表进行分类）*/
    Order querySellOutOrderStatus(Order order);

    /**通过id查询commid（为了售出，修改商品状态）*/
    String queryCommidById(String id);

    /**根据sellerid，查询卖家用户近30日内，所有star!=0的记录（只查询startoseller字段，并放在List中
     * 然后在控制器层：①计算所有star之和。 ②记录总数。 ③根据公式：所有star之和 ÷ 订单记录总数 = 最终评分*/
    List<Integer> queryStarBySellerId(String sellerId);

    /**根据buyerid，查询买家用户。。。。（类似上一个方法）*/
    List<Integer> queryStarByBuyerId(String buyerId);
    /**后台销售列表查看所有订单*/
    List<Order> adminQueryAllOrder(@Param("page") Integer page, @Param("count") Integer count);
    /**后台销售列表,所有订单总数*/
    Integer adminQueryOrderCount();
    /**后台分析图表，按照年月查询记录总数*/
    List<Order> showDiagramByData(@Param("tyear") String tyear, @Param("tmonth") String tmonth);
}