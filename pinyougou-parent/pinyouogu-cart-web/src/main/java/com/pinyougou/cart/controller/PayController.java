package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
 *
 *    @苑帅
 *    @时间为：2019-07-15-20-21
 *
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService payService;

    @Reference
    private OrderService orderService;



    @RequestMapping("/createNative")
    public Map<String,String> createNative() {

        //获取用户名
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //调用orderservice的服务   根据userID从Redis中获取数据
        TbPayLog tbPayLog = orderService.getPayLogByUserId(userId);

        /*//3.获取金额 和 订单号

        //1.生成一个[支付]订单
        String out_trade_no = new IdWorker().nextId() + "";
        //2.获取商品订单的总金额(先写死)
        String total_fee = "1";//单位是分

        System.out.println(payService);*/
        //3.调用服务(内部实现调用统一下单API)
        return payService.createNative(tbPayLog.getOutTradeNo(),tbPayLog.getTotalFee()+"",userId);
    }


    @RequestMapping("/queryStatus")
    public Result queryStatus(String out_trade_no) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        //直接轮询调用 pay-service的接口方法 查询该out_trade_no对应的支付状态 返回数据
        Result result = new Result(false, "支付失败");


        //有一个超时时间 如果进过了5分钟还没支付就是表示超时
        int count = 0;
        while (true) {
            Map<String,String> resultMap = payService.queryStatus(out_trade_no);

            count++;
            if (count >=10) {
                result = new Result(false,"超时");


                // 关闭微信支付订单
                Map<String,String> resultmap = payService.closePay(out_trade_no);

                if("SUCCESS".equals(resultmap.get("result_code"))){
                    //关闭微信的订单成功

                    //删除订单
                    //1. 恢复redis中的商品的库存
                    //2. 删除预订单
                    //4. 恢复队列
                    orderService.deleteOrder(userId);
                }


                break;
            }

            if (resultMap == null) {
                result = new Result(false, "支付失败");
                break;
            }
            if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                result = new Result(true, "支付成功");

                 /* + 修改商品的订单的状态
                        + 支付日志的订单的状态
                        + 删除掉redis中的支付日志*/
                orderService.updateStatus(out_trade_no,resultMap.get("transaction_id"));
                break;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    @RequestMapping("/getName")
    public String getName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
