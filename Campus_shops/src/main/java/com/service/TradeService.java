package com.service;

import com.alipay.api.AlipayApiException;
import com.entity.OrderVo;
import com.entity.Traderecord;
import com.mapper.TradeMapper;
import com.util.AlipayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

/**
 * <p>
 *  交易 服务类
 * </p>
 *
 * @author ajiu
 * @since 2021-08-09
 */
@Service
@Transactional
public class TradeService{

    @Autowired
    TradeMapper tradeMapper;

    public Integer addRecord(String from, String to, BigDecimal money){
        return tradeMapper.insert(new Traderecord().setFrom(from).setTo(to).setMoney(money));
    }

    public String pay(OrderVo orderVo) throws AlipayApiException {
        return AlipayUtil.connect(orderVo);
    }

}