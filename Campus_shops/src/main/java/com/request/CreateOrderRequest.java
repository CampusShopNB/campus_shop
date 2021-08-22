package com.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 *  生成订单入参
 * </p>
 *
 * @author ajiu
 * @since 2021-08-09
 */
@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
@Accessors(chain = true)//链式写法
@Data
public class CreateOrderRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    @NotBlank
    private String commid;

    /**
     * 收货人
     */
    @NotBlank(message = "收货人信息不能为空")
    private String receiverName;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String receiverTel;

    /**
     * 协商交易地址（提醒客户先与商家私聊协商）
     */
    @NotBlank(message = "协商交易地址不能为空")
    private String address;
}