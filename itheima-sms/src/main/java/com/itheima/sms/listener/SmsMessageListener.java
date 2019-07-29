package com.itheima.sms.listener;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-09-21-23
 *
 */

import com.alibaba.fastjson.JSON;
import com.aliyuncs.exceptions.ClientException;
import com.itheima.sms.util.SmsUtil;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.Map;

public class SmsMessageListener implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            if (list != null) {
                for (MessageExt msg : list) {
                    byte[] body = msg.getBody();//获取去消息体
                    String mapstr = new String(body);//map对象的字符串
                    Map<String,String> map = JSON.parseObject(mapstr, Map.class);

                    System.out.println("==================接收到了数据==="+map);
                    //发短信
                    SmsUtil.sendSms(map);//add
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return ConsumeConcurrentlyStatus.RECONSUME_LATER;

    }
}
