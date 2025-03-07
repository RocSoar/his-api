package com.roc.his.api.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.felord.payment.PayException;
import cn.hutool.json.JSONObject;
import com.roc.his.api.exception.HisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {
    /*
     * 捕获异常，并且返回500状态码
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JSONObject exceptionHandler(Exception e) {
        JSONObject json = new JSONObject();
        if (e instanceof HttpMessageNotReadableException) {
            HttpMessageNotReadableException exception = (HttpMessageNotReadableException) e;
            log.error("error: {}", exception.getMessage());
            json.set("error", "请求未提交数据或者数据有误");
        } else if (e instanceof MissingServletRequestPartException) {
            MissingServletRequestPartException exception = (MissingServletRequestPartException) e;
            log.error("error: {}", exception.getMessage());
            json.set("error", "请求提交数据错误");
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            HttpRequestMethodNotSupportedException exception = (HttpRequestMethodNotSupportedException) e;
            log.error("error: {}", exception.getMessage());
            json.set("error", "HTTP请求方法类型错误");
        } else if (e instanceof MethodArgumentNotValidException) {
            //没有通过后端验证产生的异常
            Map map = new HashMap<>();
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
                map.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            json.set("error", map);
        } else if (e instanceof BindException) {
            //Web方法参数数据类型转换异常，比如String[]数组类型的参数，你上传的数据却是String类型
            BindException exception = (BindException) e;
            String defaultMessage = exception.getFieldError().getDefaultMessage();
            log.error(defaultMessage, exception);
            json.set("error", defaultMessage);
        } else {
            //处理其余的异常
            log.error("执行异常", e);
            json.set("error", "执行异常");
        }
        return json;
    }

    /*
     * 捕获自定义业务异常
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JSONObject hisExceptionHandler(HisException e) {
        JSONObject json = new JSONObject();
        log.error("业务异常: ", e);
        json.set("error", e.getMsg());
        return json;
    }

    /*
     * 捕获SaToken的未登录异常，并且返回401状态码
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public JSONObject unLoginHandler(NotLoginException e) {
        JSONObject json = new JSONObject();
        json.set("error", e.getMessage());
        return json;
    }

    /*
     * 捕获SaToken的无权限异常，并且返回401状态码
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public JSONObject notPermissionHandler(NotPermissionException e) {
        JSONObject json = new JSONObject();
        json.set("error", e.getMessage());
        return json;
    }

    /*
     * 捕获微信支付异常
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JSONObject notPermissionHandler(PayException e) {
        JSONObject json = new JSONObject();
        log.error("微信支付异常", e);
        json.set("error", "微信支付异常");
        return json;
    }
}
