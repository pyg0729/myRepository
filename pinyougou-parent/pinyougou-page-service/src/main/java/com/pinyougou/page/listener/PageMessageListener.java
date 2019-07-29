package com.pinyougou.page.listener;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-08-21-43
 *
 */

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbItem;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PageMessageListener implements MessageListenerConcurrently {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        try {
            if (msgs != null) {
                //1.循环遍历
                for (MessageExt msg : msgs) {
                    //2.获取消息体 bytes
                    byte[] body = msg.getBody();
                    //3.转成字符串  Messageinfo
                    String info = new String(body);
                    //4.转成对象
                    MessageInfo messageInfo = JSON.parseObject(info, MessageInfo.class);

                    //5.判断 类型 //add  update  delete  分别进行生成静态页 和删除静态页
                    switch (messageInfo.getMethod()) {
                        case MessageInfo.METHOD_ADD:{
                            break;
                        }
                        case MessageInfo.METHOD_UPDATE:{
                            Object context = messageInfo.getContext();
                            String s = context.toString();
                            List<TbItem> itemList = JSON.parseArray(s, TbItem.class);
                            Set<Long> set = new HashSet<>();
                            for (TbItem tbItem : itemList) {
                                Long goodsId = tbItem.getGoodsId();
                                set.add(goodsId);
                            }
                            for (Long aLong : set) {
                                itemPageService.genItemHtml(aLong);//SPU 的ID
                            }
                            break;
                        }
                        case MessageInfo.METHOD_DELETE:{
                            break;
                        }
                    }

                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
