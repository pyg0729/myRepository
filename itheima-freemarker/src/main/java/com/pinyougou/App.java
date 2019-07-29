package com.pinyougou;


import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        //创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());

        //设置 模板所在的目录
        configuration.setDirectoryForTemplateLoading(new File("E:\\Java60\\MyPinyougou\\itheima-freemarker\\src\\main\\resources\\template"));

        //设置字符集
        configuration.setDefaultEncoding("utf-8");

        //加载模板

        Template template = configuration.getTemplate("demo.ftl");

        //创建数据模型
        Map map = new HashMap<>();
        map.put("name","张三");
        map.put("message","欢迎来到神奇的品优购世界");

        //创建writer 对象
        FileWriter out = new FileWriter(new File("E:\\Java60\\MyPinyougou\\itheima-freemarker\\src\\main\\resources\\html\\test.html"));

        //输出
        template.process(map,out);

        //关闭资源
        out.close();



    }
}
