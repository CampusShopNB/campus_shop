package com.util;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.config.AlipayConfig;
import com.entity.OrderVo;

/* 支付宝 */
public class AlipayUtil {
    public static String connect(OrderVo orderVo) throws AlipayApiException {
        //1、获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(
                AlipayConfig.getKey("gatewayUrl"),//支付宝网关
                AlipayConfig.getKey("app_id"),//appid
                AlipayConfig.getKey("merchant_private_key"),//商户私钥
                "json",
                AlipayConfig.getKey("charset"),//字符编码格式
                AlipayConfig.getKey("alipay_public_key"),//支付宝公钥
                AlipayConfig.getKey("sign_type")//签名方式
        );
        //2、设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        //页面跳转同步通知页面路径
        alipayRequest.setReturnUrl(AlipayConfig.getKey("return_url"));
        // 服务器异步通知页面路径
        alipayRequest.setNotifyUrl(AlipayConfig.getKey("notify_url"));
        //封装参数
        alipayRequest.setBizContent(JSON.toJSONString(orderVo));

        //3、请求支付宝进行付款，并获取支付结果
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        //返回付款信息
        return result;
    }
}

