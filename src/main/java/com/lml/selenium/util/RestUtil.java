package com.lml.selenium.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.selenium.exception.RestException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author yugi
 * @apiNote 请求工具类
 * @since 2019-04-08
 */
@UtilityClass
@Slf4j
public class RestUtil {

    private RestTemplate restTemplate = new RestTemplate();

    /**
     * post方法
     *
     * @param url        请求url
     * @param obj        post请求的参数
     * @param returnType 返回值的类
     * @param headers    请求头
     * @return 返回值的类
     */
    public <T> T post(String url, Object obj, Class<T> returnType, Map<String, String> headers) {
        return request(url, HttpMethod.POST, obj, returnType, headers);
    }

    /**
     * post方法
     *
     * @param url        请求url
     * @param obj        post请求的参数
     * @param returnType 返回值的类
     * @return 返回值的类
     */
    public <T> T post(String url, Object obj, Class<T> returnType) {
        return post(url, obj, returnType, null);
    }

    /**
     * get方法
     *
     * @param url        请求url
     * @param returnType 返回值的类
     * @param headers    请求头
     * @return 返回值的类
     */
    public <T> T get(String url, Class<T> returnType, Map<String, String> headers) {
        return request(url, HttpMethod.GET, null, returnType, headers);
    }

    /**
     * get方法
     *
     * @param url        请求url
     * @param returnType 返回值的类
     * @return 返回值的类
     */
    public <T> T get(String url, Class<T> returnType) {
        return get(url, returnType, null);
    }

    /**
     * put方法
     *
     * @param url        请求url
     * @param returnType 返回值的类
     * @param headers    请求头
     * @return 返回值的类
     */
    public <T> T put(String url, Class<T> returnType, Map<String, String> headers) {
        return request(url, HttpMethod.PUT, null, returnType, headers);
    }

    /**
     * put方法
     *
     * @param url        请求url
     * @param returnType 返回值的类
     * @return 返回值的类
     */
    public <T> T put(String url, Class<T> returnType) {
        return put(url, returnType, null);
    }

    /**
     * delete方法
     *
     * @param url        请求url
     * @param returnType 返回值的类
     * @param headers    请求头
     * @return 返回值的类
     */
    public <T> T delete(String url, Class<T> returnType, Map<String, String> headers) {
        return request(url, HttpMethod.DELETE, null, returnType, headers);
    }

    /**
     * delete方法
     *
     * @param url        请求url
     * @param returnType 返回值的类
     * @return 返回值的类
     */
    public <T> T delete(String url, Class<T> returnType) {
        return delete(url, returnType, null);
    }

    @SuppressWarnings("unchecked")
    private <T> T request(String url, HttpMethod method, Object obj, Class<T> returnType, Map<String, String> headers) {
        try {
            //获取header信息
            HttpHeaders requestHeaders = new HttpHeaders();
            if (MapUtils.isNotEmpty(headers)) {
                for (Map.Entry<String, String> head : headers.entrySet()) {
                    requestHeaders.set(head.getKey(), head.getValue());
                }
            }
            HttpEntity<Object> requestEntity = new HttpEntity<>(obj, requestHeaders);
            log.info("请求地址:{}", url);
            ResponseEntity<T> exchange = restTemplate.exchange(url, method, requestEntity, returnType);
            checkResponseEntityStatus(exchange);

            T body = exchange.getBody();
            if (body == null || !JSONUtil.isJson(body.toString())) {
                return body;
            }
            boolean needToConvert = false;
            if (returnType.getName().equals(JSONObject.class.getName())) {
                // 如果是JSONObject类,则把他当成String.class请求,然后返回来的时候再进行转换
                returnType = (Class<T>) String.class;
                needToConvert = true;
            }
            return needToConvert ? JSONUtil.toBean(body.toString(), returnType) : exchange.getBody();
        }
        catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new RestException("请求发生异常" + e.getMessage());
        }
    }


    /**
     * 校验ResponseEntity的状态码
     *
     * @param entity 远程调用返回来的ResponseEntity
     */
    private <T> void checkResponseEntityStatus(ResponseEntity<T> entity) {
        log.info("请求回来的参数是:{}", entity);
        if (!HttpStatus.OK.equals(entity.getStatusCode())) {
            throw new RestException("发生错误!状态码是:" + entity.getStatusCodeValue());
        }
    }

}
