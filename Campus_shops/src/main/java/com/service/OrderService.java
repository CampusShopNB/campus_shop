package com.service;

import com.entity.Order;
import com.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * <p>
 *  订单 服务类
 * </p>
 */
@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    /**添加订单insert*/
    public Integer addOrder(Order order){
        return orderMapper.addOrder(order);
    }

    /**删除订单，看似delete，实则update订单状态为已删除
     * 合并到修改订单状态updateOrder方法中*/
//    public Integer deleteOrder(Order order){
//        return orderMapper.deleteOrder(order);
//    }

    /**修改订单状态*/
    public Integer updateOrder(Order order){
        return orderMapper.updateOrder(order);
    }

    /*修改评分(暂时用不上，发现可以用updateOrder)*/
    /**根据订单id,sellerid,修改订单的startoseller给卖家的评分*/
    public Integer updateStartoseller(Order order){
        return orderMapper.updateStartoseller(order);
    }
    /**根据订单id,sellerid,修改订单的startobuyer给买家的评分*/
    public Integer updateStartobuyer(Order order){
        return orderMapper.updateStartobuyer(order);
    }



    /**分页查看所有购入订单内容*/
    public List<Order> queryAllBuyInOrder(Integer page, Integer count, String buyerid){
        return orderMapper.queryAllBuyInOrder(page,count,buyerid);
    }

    /**分页查看所有售出订单内容*/
    public List<Order> queryAllSellOutOrder(Integer page, Integer count, String sellerid){
        return orderMapper.queryAllSellOutOrder(page,count,sellerid);
    }

    /**查询购入订单的总数    select count(*)*/
    public Integer queryBuyInOrderCount(String buyerid){
        return orderMapper.queryBuyInOrderCount(buyerid);
    }
    /**查询售出订单的总数*/
    public Integer querySellOutOrderCount(String sellerid){
        return orderMapper.querySellOutOrderCount(sellerid);
    }

//    /**查询订单状态（暂时不清楚作用，先留着）*/
//    public Order queryOrderStatus(Order order){
//        return orderMapper.queryOrderStatus(order);
//    }

    /**查询购入订单状态（估计是下拉列表进行分类）*/
    public Order queryBuyInOrderStatus(Order order){
        return orderMapper.queryBuyInOrderStatus(order);
    }

    /**查询售出订单状态（估计是下拉列表进行分类）*/
    public Order querySellOutOrderStatus(Order order){
        return orderMapper.querySellOutOrderStatus(order);
    }
    /**通过id查询commid（为了售出，修改商品状态）*/
    public String queryCommidById(String id) {
        return orderMapper.queryCommidById(id);
    }

    /**根据sellerid，查询卖家用户近30日内，所有star!=0的记录（只查询startoseller字段，并放在List中
     * 然后在控制器层：①计算所有star之和。 ②记录总数。 ③根据公式：所有star之和 ÷ 订单记录总数 = 最终评分*/
    public List<Integer> queryStarBySellerId(String sellerId){
        return orderMapper.queryStarBySellerId(sellerId);
    }

    /**根据buyerid，查询买家用户。。。。（类似上一个方法）*/
    public List<Integer> queryStarByBuyerId(String buyerId){
        return orderMapper.queryStarByBuyerId(buyerId);
    }

    /**根据id获取订单*/
    public Order getOrderbyId(String id){   return orderMapper.selectById(id); }

    /**后台销售列表查看所有订单*/
    public List<Order> adminQueryAllOrder(Integer page, Integer count){
        return orderMapper.adminQueryAllOrder(page,count);
    }
    /**后台销售列表,所有订单总数*/
    public Integer adminQueryOrderCount(){
        return orderMapper.adminQueryOrderCount();
    }
    /**后台分析图表，按照年月查询记录总数*/
    public List<Order> showDiagramByData(String tyear, String tmonth){
        return orderMapper.showDiagramByData(tyear,tmonth);
    }
}