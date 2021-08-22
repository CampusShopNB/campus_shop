package com.service;

import com.alipay.api.AlipayApiException;
import com.entity.OrderVo;
import com.entity.Traderecord;
import com.mapper.TradeMapper;
import com.util.AlipayUtil;
import com.util.KeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

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
        Random random = new Random();
        String id = String.valueOf(random.nextInt(9999)+10000);
        return tradeMapper.insertRecord(new Traderecord().setId(id).setFromid(from).setToid(to).setMoney(money).setTradetime(new Date()));
    }

    public String pay(OrderVo orderVo) throws AlipayApiException {
        return AlipayUtil.connect(orderVo);
    }

}