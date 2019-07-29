package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.activation.CommandInfo;
import javax.persistence.Id;
import java.util.HashMap;
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
    private SeckillOrderService seckillOrderService;


    @RequestMapping("/createNative")
    public Map<String, String> createNative() {

        //1.获取用户的ID
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        //2.从redis中获取预订单 获取预订单的金额  和 支付订单号
        TbSeckillOrder seckillOrder = seckillOrderService.findOrderByUserId(userId);

        if (seckillOrder != null) {
            double v = seckillOrder.getMoney().doubleValue() * 100;
            long fen = (long) v;
            return payService.createNative(seckillOrder.getId() + "", fen + "", userId);
        } else {
            return new HashMap<>();
        }
    }


    /*
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
            if (count >=30) {
                result = new Result(false,"超时");

                //关闭微信订单
                Map<String,String> resultmap = payService.closePay(out_trade_no);

                if ("SUCCESS".equals(resultmap.get("result_code"))) {
                    //关闭微信的订单
                    //删除订单
                    //恢复Redis中的商品库存
                    //恢复队列
                    seckillOrderService.deleteOrder(userId);
                }else if ("ORDERPAID".equals(resultmap.get("err_code"))) {
                    seckillOrderService.updateOrderStatus(userId,resultmap.get("transaction_id"));
                }else {

                }

                break;
            }

            if (resultMap == null) {
                result = new Result(false, "支付失败");
                break;
            }
            if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                result = new Result(true, "支付成功");


                seckillOrderService.updateOrderStatus(userId,resultMap.get("transaction_id"));
                break;
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;

    }
    */


    @RequestMapping("/queryStatus")
    public Result queryStatus(String out_trade_no) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();


        Result result = new Result(false, "支付失败");

        Map<String, String> resultMap = payService.queryStatus(out_trade_no);


        if ("SUCCESS".equals(resultMap.get("trade_state"))) {
            result = new Result(true, "支付成功");
            seckillOrderService.updateOrderStatus(userId, resultMap.get("transaction_id"));
        }

        if (resultMap == null) {
            result = new Result(false, "支付失败");
        }


        return result;
    }


    /**
     * 关闭订单
     *
     * @param out_trade_no
     */
    @RequestMapping("/closePay")
    public Result closePay(String out_trade_no) {


        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Map<String, String> resultMap = payService.queryStatus(out_trade_no);
        //关闭微信订单
        Map<String, String> resultmap = payService.closePay(out_trade_no);
        if ("SUCCESS".equals(resultmap.get("result_code"))) {
            //关闭微信的订单
            //删除订单
            //恢复Redis中的商品库存
            //恢复队列
            seckillOrderService.deleteOrder(userId);
            return new Result(true, "订单已关闭!");
        } else if ("ORDERPAID".equals(resultmap.get("err_code"))) {
            seckillOrderService.updateOrderStatus(userId, resultmap.get("transaction_id"));
            return new Result(false, "支付成功!");
        }
        return new Result();
    }

}
