package fnozoszzt.pdd.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: RestTemplateUtil
 * @Author: lipeisheng
 * @Date: 2018/8/30 14:28
 * @Description: TODO
 * @Version: 1.0
 */
@Component
public class RestTemplateUtil {

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(RestTemplateUtil.class);

    /**
     * @Description //get请求
     * @Author lipeisheng
     * @Date 11:20 2018/9/4
     * @Param "http://SERVICE/api/test?name={1}", String.class, "lips"
     * @return T
     */
    public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) throws Exception{
        return restTemplate.getForEntity(url,responseType,uriVariables).getBody();
    }
    /**
     * @Description //get请求
     * @Author lipeisheng
     * @Date 11:21 2018/9/4
     * @Param "http://SERVICE1/hello?name={name}",String.class,map:map的key对应占位符的值
     * @return T
     */
    public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) throws Exception{
        return restTemplate.getForEntity(url,responseType,uriVariables).getBody();
    }

    /**
     * @Description //
     * @Author lipeisheng
     * @Date 11:24 2018/9/4
     * @Param http://SERVICE1/bye?sex={sex}",param,String.class,map：map对应接收bean，map对应url中基本参数。
     * @return T
     */
    public <T> T postForObject(String url, @Nullable Object request,
                               Class<T> responseType, Object... uriVariables) throws Exception{
        return restTemplate.postForEntity(url,request,responseType,uriVariables).getBody();
    }

    /**
     * @Description //post请求
     * @Author lipeisheng
     * @Date 11:19 2018/9/4
     * @Param http://SERVICE1/bye?sex={sex}",param,String.class,"nam"：map对应接收bean，sex对应其他基本参数。
     * @return T
     */
    public <T> T postForObject(String url, @Nullable Object request,
                               Class<T> responseType, Map<String, ?> uriVariables) throws Exception{
        return restTemplate.postForEntity(url,request,responseType,uriVariables).getBody();
    }

    /**
     * @Description //原生restTemplate的put请求不返回值
     * @Author lipeisheng
     * @Date 15:00 2018/8/30
     * @Param
     * @return T
     */
    public <T> T putForObject(String url, Object params, Class<T> bodyType) throws Exception{
        HttpMethod method = HttpMethod.PUT;
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        MimeType mimeType = MimeTypeUtils.parseMimeType("application/json");
        MediaType mediaType = new MediaType(mimeType.getType(), mimeType.getSubtype());
        // 请求体
        headers.setContentType(mediaType);
        //提供json转化功能
        ObjectMapper mapper = new ObjectMapper();
        String str = null;
        try {
            if (params != null) {
                str = mapper.writeValueAsString(params);
            }
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(),e);
            throw e;
        }
        // 发送请求
        HttpEntity<String> entity = new HttpEntity<>(str, headers);
        ResponseEntity<T> resultEntity = restTemplate.exchange(url, method, entity, bodyType);
        return resultEntity.getBody();
    }
    /**
     * @Description //原生restTemplate的delete请求不返回值,且使用自定义错误接收，原生delete不能传参。故修改如下
     * @Author lipeisheng
     * @Date 15:00 2018/8/30
     * @Param [url, params, bodyType]
     * @return T
     */
    public <T> T deleteForObject(String url, Object params, Class<T> bodyType) throws Exception{

        HttpMethod method = HttpMethod.DELETE;
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        MimeType mimeType = MimeTypeUtils.parseMimeType("application/json");
        MediaType mediaType = new MediaType(mimeType.getType(), mimeType.getSubtype());
        // 请求体
        headers.setContentType(mediaType);
        //提供json转化功能
        ObjectMapper mapper = new ObjectMapper();
        String str = null;
        try {
            if (params != null) {
                str = mapper.writeValueAsString(params);
            }
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(),e);
            throw e;
        }
        // 发送请求
        HttpEntity<String> entity = new HttpEntity<>(str, headers);
        ResponseEntity<T> resultEntity = restTemplate.exchange(url, method, entity, bodyType);
        return resultEntity.getBody();
    }
    /**
     * @Description //获取byte[]
     * @Author lipeisheng
     * @Date 20:49 2018/8/30
     * @Param [url]
     * @return byte[]
     */
    public byte[] download(String url) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        byte[] result = null;
        try {
            List list = new ArrayList<>();
            list.add(MediaType.valueOf("application/json"));
            headers.setAccept(list);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<byte[]>(headers),
                    byte[].class);

            result = response.getBody();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            throw e;
        }
        return result;
    }
}
