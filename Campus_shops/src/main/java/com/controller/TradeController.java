package com.controller;

import com.alipay.api.AlipayApiException;
import com.entity.Commodity;
import com.entity.Order;
import com.entity.OrderVo;
import com.request.CreateOrderRequest;
import com.service.CommodityService;
import com.service.OrderService;
import com.service.TradeService;
import com.util.StatusCode;
import com.vo.ResultVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *  交易控制器
 * </p>
 *
 * @author ajiu
 * @since 2021-08-09
 */
@Controller
public class TradeController {
    @Autowired
    private TradeService tradeService;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private OrderService orderService;

    /**
     * 生成订单
     * 简化业务逻辑：支付成功后才生成订单，不支持未支付锁定订单
     */
    @ResponseBody
    @PostMapping("/createOrder")
    public ResultVo createOrder(@RequestBody CreateOrderRequest request, HttpSession session){
        String userId = (String) session.getAttribute("userid");
        String commid = request.getCommid();
        Commodity commodity = commodityService.LookCommodity(new Commodity().setCommid(commid));
        if(commodity.getCommstatus() == 1 ){
            // 新建订单
            Order order = new Order();
            BeanUtils.copyProperties(commodity, order);
            order.setBuyerid(userId);
            order.setBuyername((String) session.getAttribute("username"));
            try {
                String result = tradeService.pay(new OrderVo().setOut_trade_no(order.getId())
                        .setSubject(commodity.getCommname())
                        .setTotal_amount(new StringBuffer().append(1))
                        .setTotal_amount(new StringBuffer().append(commodity.getThinkmoney())));
                System.out.println("payResult : " + result);
            }catch (AlipayApiException e) {
                return new ResultVo(false, StatusCode.ERROR,"支付失败");
            }
            commodityService.ChangeCommstatus(commid, 4);
            order.setOrderstatus(1);
            orderService.addOrder(order);
            // 生成付款交易记录
            tradeService.addRecord(userId, commodity.getUserid(), commodity.getThinkmoney());
            return new ResultVo(true,StatusCode.OK,"成功生成订单");
        }
        return new ResultVo(false, StatusCode.ERROR,"生成订单失败");
    }

    /**
     * 下单后半小时内，未发货商品可以取消订单
     */
    @ResponseBody
    @PostMapping("/cancelOrder/{orderId}")
    public ResultVo cancelOrder(String orderId, HttpSession session){
        String userId = (String) session.getAttribute("userid");
        Order order =  orderService.getOrderbyId(orderId);
        if(!order.getBuyerid().equals(userId)){
            return new ResultVo(false, StatusCode.ERROR,"这不是您的订单，无法取消");
        }
        if(order.getOrderstatus() != 1 ||
                (long) new Date().getTime() - (long) order.getSoldtime().getTime() > 30 * 60 * 1000){
            return new ResultVo(false, StatusCode.ERROR,"无法取消订单");
        }
        if(refund(order.getThinkmoney(), userId, order.getSellerid())){
            order.setOrderstatus(6);
            commodityService.ChangeCommstatus(order.getCommid(), 1);
            // 生成退款交易记录
            tradeService.addRecord(order.getSellerid(), userId, order.getThinkmoney());
            return new ResultVo(true,StatusCode.OK,"成功生成订单");
        }
        return new ResultVo(false, StatusCode.ERROR,"取消订单失败");
    }

    @RequestMapping("return")
    public String PayReturn(){
        return "支付成功";
    }
    @RequestMapping("notify")
    public String PayNotify(){
        return "支付失败";
    }

    /**
     * 退款
     */
    private boolean refund(BigDecimal money, String buyerId, String sellerId){
        // 调用aliPay
        return true;
    }

    @GetMapping("/trade/orderinfo")
    public String orderinfo(){
        return "/common/orderinfo";
    }

}