package com.pinyougou.user.controller;
/*
 *
 *    @苑帅
 *    @时间为：2019-07-10-14-54
 *
 */

import com.pinyougou.user.pojo.Person;
import entity.Error;
import entity.Result;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/person")
public class PersonController {


    /**
     * @Valid  指定交易的对象在参数中使用
     * person中的要加入注解 进行校验
     * BindingResult 错误的信息 存储的对象
     * @param person
     * @param result
     * @return
     */
    @RequestMapping("/add")
    public Result add(@Valid @RequestBody Person person, BindingResult result) {

        if (result.hasErrors()) {
            //执行错误信息
            List<FieldError> fieldErrors = result.getFieldErrors();

            Result result1 = new Result(false, "信息错误!");

            List<Error> errorList = result1.getErrorsList();

            for (FieldError fieldError : fieldErrors) {
                errorList.add(new Error(fieldError.getField(), fieldError.getDefaultMessage()));
            }
            return result1;
        }

        return new Result(true,"ok");
    }
}
