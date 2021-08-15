package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author ajiu
 * @since 2021-08-09
 */
@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
@Data
@Accessors(chain = true)//链式写法
public class Traderecord {

    // 交易ID
    private Integer id;
    // 付款方
    private String from;
    // 收款方
    private String to;
    // 交易金额
    private BigDecimal money;
    // 交易时间
    private Date tradetime;

}