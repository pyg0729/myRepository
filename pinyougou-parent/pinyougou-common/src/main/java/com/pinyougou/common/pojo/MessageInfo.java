package com.pinyougou.common.pojo;

import java.io.Serializable;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.common.pojo *
 * @since 1.0
 */
public class MessageInfo implements Serializable {

    public static final int METHOD_ADD=1;//用于新增 操作
    public static final int METHOD_UPDATE=2;//用于更新 操作
    public static final int METHOD_DELETE=3;//用于删除 操作



    //要发送的内容
    private Object context;//数字  json  数组.....

    //主题
    private String topic;


    //标签
    private String tags;

    //业务的唯一标识
    private String keys;

    //要执行的方法
    private int method;

    public MessageInfo() {
    }

    public MessageInfo(String topic, String tags, String keys, Object context, int method) {
        this.context = context;
        this.topic = topic;
        this.tags = tags;
        this.keys = keys;
        this.method = method;
    }

    public MessageInfo(String topic, String tags, Object context, int method) {
        this.context = context;
        this.topic = topic;
        this.tags = tags;
        this.method = method;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }
}
