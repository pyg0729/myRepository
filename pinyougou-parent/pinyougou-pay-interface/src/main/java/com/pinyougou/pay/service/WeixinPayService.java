package com.pinyougou.pay.service;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-15-20-31
 *
 */

import java.util.Map;

public interface WeixinPayService {

    /**
     *
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    Map<String, String> createNative(String out_trade_no, String total_fee,String userId);


    Map<String, String> queryStatus(String out_trade_no);

    Map<String, String> closePay(String out_trade_no);
}
