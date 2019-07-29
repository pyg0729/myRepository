package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-08-21-18
 *
 *
 * 监听器的作用:
 * 1.获取消息
 * 2.获取消息的内容 转换数据
 * 3.更新索引库
 */

public class GoodsMessageListener implements MessageListenerConcurrently {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {

            if (msgs != null) {
                //循环遍历消息
                for (MessageExt msg : msgs) {
                    //获取消息体
                    byte[] body = msg.getBody();
                    //转换成字符串 messageInfo
                    String jsonstr = new String(body);
                    //转换成对象
                    MessageInfo messageInfo = JSON.parseObject(jsonstr, MessageInfo.class);
                    //判断  对象中的执行的 类型  (add    delete   update)
                    switch (messageInfo.getMethod()) {
                        case 1:{//ADD

                            break;
                        }
                        case 2:{//update
                            Object context = messageInfo.getContext();//商品的数据
                            //转成字符串
                            String contextstr = context.toString();
                            //转成json对象
                            List<TbItem> itemList = JSON.parseArray(contextstr, TbItem.class);
                            itemSearchService.updataIndex(itemList);
                            break;
                        }
                        case 3:{
                            Object context = messageInfo.getContext();//Long[] ----ids 数据了
                            String contextstr = context.toString();
                            Long[] ids = JSON.parseObject(contextstr, Long[].class);
                            itemSearchService.deleteByIds(ids);
                            break;
                        }
                        default:{
                            break;
                        }
                    }

                }

            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }



    }
}
