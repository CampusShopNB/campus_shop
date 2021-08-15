package com.controller;

import com.entity.Commodity;
import com.request.CreateOrderRequest;
import com.service.CommodityService;
import com.service.TradeService;
import com.util.StatusCode;
import com.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

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

    /**
     * 生成订单
     * 简化业务逻辑：支付成功后才生成订单，不支持未支付锁定订单
     */
    @ResponseBody
    @PostMapping("/createOrder")
    public ResultVo createOrder(@RequestBody CreateOrderRequest request, HttpSession session){
        String userId = (String) session.getAttribute("userid");
        String id = request.getCommid();
        Commodity commodity = commodityService.LookCommodity(new Commodity().setCommid(id));
        if(commodity.getCommstatus() == 1 && pay(id,userId)){
            commodityService.ChangeCommstatus(id, 4);
            BigDecimal money = commodity.getThinkmoney();
            // new Order here
            // 生成交易记录
            tradeService.addRecord(userId, commodity.getUserid(), money);
            return new ResultVo(true,StatusCode.OK,"成功生成订单");
        }
        return new ResultVo(false, StatusCode.ERROR,"生成订单失败");
    }

    /**
     * 支付宝支付
     * @param commid
     * @return
     */
    private boolean pay(String commid,String userId){
        // 调用aliPay

        return true;
    }

}