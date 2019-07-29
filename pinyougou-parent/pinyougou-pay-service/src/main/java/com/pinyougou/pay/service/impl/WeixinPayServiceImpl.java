package com.pinyougou.pay.service.impl;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-15-20-34
 *
 */

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WeixinPayService;

import java.util.HashMap;
import java.util.Map;
@Service
public class WeixinPayServiceImpl implements WeixinPayService {



    @Override
    public Map<String, String> createNative(String out_trade_no, String total_fee,String userId) {

        try {
            //1.组合参数集 存储到map中 map转换成XML
            HashMap<String, String> paramMap = new HashMap<>();

            paramMap.put("appid","wx8397f8696b538317");//公众号
            paramMap.put("mch_id","1473426802");//商户ID
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
            paramMap.put("body","品优购");
            paramMap.put("out_trade_no",out_trade_no);//商户订单号
            paramMap.put("total_fee",total_fee);//订单金额
            paramMap.put("spbill_create_ip","127.0.0.1");
            paramMap.put("notify_url","http://a31ef7db.ngrok.io/WeChatPay/WeChatPayNotify");
            paramMap.put("trade_type","NATIVE");//交易类型   NATIVE 二维码


            //自动添加签名 而且转成字符串
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");

            //2.使用httpclient 调用 接口 发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();


            //3.获取结果 XML  转成MAP(code_url)
            String resultXml = httpClient.getContent();

            Map<String, String> map = WXPayUtil.xmlToMap(resultXml);

            Map<String, String> resultMap = new HashMap<>();

            resultMap.put("code_url",map.get("code_url"));
            resultMap.put("out_trade_no",out_trade_no);
            resultMap.put("total_fee",total_fee);
            resultMap.put("userId",userId);


            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Map<String, String> queryStatus(String out_trade_no) {

        try {
            //1.组合参数集 存储到map中 map转换成XML
            HashMap<String, String> paramMap = new HashMap<>();

            paramMap.put("appid","wx8397f8696b538317");//公众号
            paramMap.put("mch_id","1473426802");//商户ID
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
            paramMap.put("out_trade_no",out_trade_no);//商户订单号

            //自动添加签名 而且转成字符串
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");

            //2.使用httpclient 调用 接口 发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();


            //3.获取结果 XML  转成MAP(code_url)
            String resultXml = httpClient.getContent();

            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);


            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, String> closePay(String out_trade_no) {

        try {
            //组合参数集合  存储到map中,  map转换成xml
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid","wx8397f8696b538317");
            paramMap.put("mch_id","1473426802");
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
            paramMap.put("out_trade_no",out_trade_no);

            //自动添加签名 而且转成字符串
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");


            //2.使用httpclient 调用 接口 发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            //3.获取结果 XML  转成MAP(code_url)
            String resultXml = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }
}
