package com.changgou.exception;

import com.changgou.util.Result;
import com.changgou.util.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice//开启spring的公共异常处理
//@RestController返回json类型的数据
//@Controller//返回一个html对象 springmvc的注解
public class BaseExceptionHandler {

//    /**
//     * 处理空指针异常
//     * @return
//     */
//    @ExceptionHandler(value = NullPointerException.class)
//    public Result error1(NullPointerException e){
//        return new Result(false, StatusCode.ERROR,"系统异常" + e.getMessage());
//    }

    /**
     * 处理空指针异常
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error1(Exception e){
        return new Result(false, StatusCode.ERROR,"系统异常" + e.getMessage());
    }
}
