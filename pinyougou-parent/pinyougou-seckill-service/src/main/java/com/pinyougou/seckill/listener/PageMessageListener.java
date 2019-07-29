package com.pinyougou.seckill.listener;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-18-20-59
 *
 */

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageMessageListener implements MessageListenerConcurrently {


    @Autowired
    private FreeMarkerConfigurer configurer;


    @Autowired
    private SeckillGoodsService goodsService;



    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        if (msgs != null) {
            for (MessageExt msg : msgs) {
                //获取消息体
                byte[] body = msg.getBody();
                //转换成string
                String s = new String(body);
                //将字符串转换为json对象
                MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);

                switch (messageInfo.getMethod()) {

                    //ADD
                    case 1:{
                        //获取对象
                        String context = messageInfo.getContext().toString();
                        //转换数组
                        Long[] longs = JSON.parseObject(context, Long[].class);
                        //查询数据库数据
                        for (Long aLong : longs) {
                            //生成静态页面
                            genHTML("item.ftl",aLong);
                        }
                        break;}
                        //update
                    case 2:{

                        break;}
                        //delete
                    case 3:{

                        break;}
                    default:{
                        break;
                    }
                }

            }
        }


        return null;
    }

    @Value("${PageDir}")
    private String pageDir;


    /**
     * 生成静态页面
     * @param templateName 模板名称
     * @param id
     */
    private void genHTML(String templateName, Long id) {

        FileWriter fileWriter = null;
        try {
            //1.创建一个配置类Configruation
            //2.设置utf-8编码
            //3.设置模板文件所在的目录
            Configuration configuration = configurer.getConfiguration();
            //4.创建模板文件(模板语法 ----秒杀详情页面的模板)

            //5.加载模板对象
            Template template = configuration.getTemplate(templateName);

            //6.从数据库中获取数据集(秒杀商品的数据)
            TbSeckillGoods tbSeckillGoods = goodsService.selectByPrimaryKey(id);

            Map<String,Object> model = new HashMap<>();
            model.put("seckillGoods",tbSeckillGoods);

            //7.创建一个writer
            fileWriter = new FileWriter(new File(pageDir + id + ".html"));
            //8.处理生成页面
            template.process(model,fileWriter);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
