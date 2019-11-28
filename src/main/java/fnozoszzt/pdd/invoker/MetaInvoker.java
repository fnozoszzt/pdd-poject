//package com.netease.bdms.dsweb.invoker;
//
//import com.alibaba.fastjson.JSONObject;
//import com.netease.bdms.dsweb.common.Constants;
//import com.netease.bdms.dsweb.common.DsException;
//import com.netease.bdms.dsweb.util.AuthUtil;
//import com.netease.bdms.dsweb.util.RestTemplateUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.net.URLEncoder;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @ClassName: MetaInvoker
// * @Author: lipeisheng
// * @Date: 2018/9/7 14:44
// * @Description: TODO
// * @Version: 1.0
// */
//@Service
//public class MetaInvoker {
//    @Autowired
//    private RestTemplateUtil restTemplateUtil;
//    @Autowired
//    private Constants constants;
//    @Autowired
//    private AuthUtil authUtil;
//
//
//    /**
//     * @Description //从用户池获取全部用户信息
//     * @Author lipeisheng
//     * @Date 20:47 2018/9/7
//     * @Param []
//     * @return com.alibaba.fastjson.JSONObject
//     */
//    public JSONObject getAllUsers() throws Exception{
//        String uri = "/v1/meta/users/list";
//        String url = "http://bdmsmeta" + uri + "?apiKey={apiKey}&token={token}";
//
//        Map<String,Object> param = new HashMap<>();
//        param.put("apiKey",URLEncoder.encode(constants.getApiKey(),"utf-8"));
//        param.put("token",URLEncoder.encode(authUtil.generateToken(constants.getMasterKey(),uri), "utf-8"));
//
//        JSONObject result = restTemplateUtil.getForObject(url, JSONObject.class,param);
//
//        if (!Constants.SUCCESS_CODE.equals(result.getInteger("code"))) {
//            throw new DsException(ErrorCode.HTTPUTIL_REQUEST_FAIL, result.getString("msg"));
//        }
//        return result;
//    }
//
//    /**
//     * @Description //根据email获取对应name
//     * @Author lipeisheng
//     * @Date 20:47 2018/9/7
//     * @Param []
//     * @return com.alibaba.fastjson.JSONObject
//     */
//    public JSONObject getUserName(String email) throws Exception{
//        String uri = "/v1/meta/username/get";
//        String url = "http://bdmsmeta" + uri + "?apiKey={apiKey}&token={token}&email={email}";
//
//        Map<String,Object> param = new HashMap<>();
//        param.put("apiKey",URLEncoder.encode(constants.getApiKey(),"utf-8"));
//        param.put("token",URLEncoder.encode(authUtil.generateToken(constants.getMasterKey(),uri), "utf-8"));
//        param.put("email",email);
//
//        JSONObject result = restTemplateUtil.getForObject(url, JSONObject.class,param);
//
//        if (!Constants.SUCCESS_CODE.equals(result.getInteger("code"))) {
//            throw new DsException(ErrorCode.HTTPUTIL_REQUEST_FAIL, result.getString("msg"));
//        }
//        return result;
//    }
//
//    /**
//     * @Description //根据email获取其产品账号，admin获取全部产品账号
//     * @Author lipeisheng
//     * @Date 20:49 2018/9/7
//     * @Param [email]
//     * @return com.alibaba.fastjson.JSONObject
//     */
//    public JSONObject getAccount(String email) throws Exception{
//        String uri = "/v1/meta/account/list";
//        String url = "http://bdmsmeta" + uri + "?apiKey={apiKey}&token={token}&service={service}&email={email}";
//
//        Map<String,Object> param = new HashMap<>();
//        param.put("apiKey",URLEncoder.encode(constants.getApiKey(),"utf-8"));
//        param.put("token",URLEncoder.encode(authUtil.generateToken(constants.getMasterKey(),uri), "utf-8"));
//        param.put("email",email);
//        param.put("service", Constants.SERVICE_NAME);
//
//        JSONObject result = restTemplateUtil.getForObject(url, JSONObject.class,param);
//
//        if (!Constants.SUCCESS_CODE.equals(result.getInteger("code"))) {
//            throw new DsException(ErrorCode.HTTPUTIL_REQUEST_FAIL, result.getString("msg"));
//        }
//        return result;
//    }
//
//    /**
//     * @Description //获取个人或者项目的keytab
//     * @Author lipeisheng
//     * @Date 20:51 2018/9/7
//     * @Param [user]
//     * @return com.alibaba.fastjson.JSONObject
//     */
//    public JSONObject getUserKey(String user) throws Exception{
//        String uri = "/v1/meta/keytab/get";
//        String url = "http://bdmsmeta" + uri + "?apiKey={apiKey}&token={token}&user={user}";
//
//        Map<String,Object> param = new HashMap<>();
//        param.put("apiKey",URLEncoder.encode(constants.getApiKey(),"utf-8"));
//        param.put("token",URLEncoder.encode(authUtil.generateToken(constants.getMasterKey(),uri), "utf-8"));
//        param.put("user",user);
//
//        JSONObject result = restTemplateUtil.getForObject(url, JSONObject.class,param);
//        if (!Constants.SUCCESS_CODE.equals(result.getInteger("code"))) {
//            throw new DsException(ErrorCode.HTTPUTIL_REQUEST_FAIL, result.getString("msg"));
//        }
//        return result;
//    }
//
//    /**
//     * @Description //获取hive_site.xml文件
//     * @Author lipeisheng
//     * @Date 20:53 2018/9/7
//     * @Param [clusterId, product]
//     * @return com.alibaba.fastjson.JSONObject
//     */
//    public JSONObject getXmlByHiveid(String clusterId, String product) throws Exception{
//        String uri = "/v1/meta/hivesite/get";
//        String url = "http://bdmsmeta" + uri + "?apiKey={apiKey}&token={token}&product={product}&clusterId={clusterId}";
//
//        Map<String,Object> param = new HashMap<>();
//        param.put("apiKey",URLEncoder.encode(constants.getApiKey(),"utf-8"));
//        param.put("token",URLEncoder.encode(authUtil.generateToken(constants.getMasterKey(),uri), "utf-8"));
//        param.put("product",product);
//        param.put("clusterId",clusterId);
//
//        JSONObject result = restTemplateUtil.getForObject(url, JSONObject.class,param);
//
//        if (!Constants.SUCCESS_CODE.equals(result.getInteger("code"))) {
//            throw new DsException(ErrorCode.HTTPUTIL_REQUEST_FAIL, result.getString("msg"));
//        }
//        return result;
//    }
//
//    /**
//     * @Description //获取产品对应的集群信息
//     * @Author lipeisheng
//     * @Date 18:51 2018/9/10
//     * @Param [clusterId, product]
//     * @return com.alibaba.fastjson.JSONObject
//     */
//    public JSONObject getClusters(String product) throws Exception{
//        String uri = "/v1/meta/cluster/list";
//        String url = "http://bdmsmeta" + uri + "?apiKey={apiKey}&token={token}&product={product}";
//
//        Map<String,Object> param = new HashMap<>();
//        param.put("apiKey",URLEncoder.encode(constants.getApiKey(),"utf-8"));
//        param.put("token",URLEncoder.encode(authUtil.generateToken(constants.getMasterKey(),uri), "utf-8"));
//        param.put("product",product);
//
//        JSONObject result = restTemplateUtil.getForObject(url, JSONObject.class, param);
//
//        if (!Constants.SUCCESS_CODE.equals(result.getInteger("code"))) {
//            throw new DsException(ErrorCode.HTTPUTIL_REQUEST_FAIL, result.getString("msg"));
//        }
//        return result;
//    }
//
//    /**
//     * @Description //获取项目中用户信息
//     * @Author lipeisheng
//     * @Date 14:50 2018/9/11
//     * @Param [product]
//     * @return com.alibaba.fastjson.JSONObject
//     */
//    public JSONObject getProductUsers(String product) throws Exception{
//        String uri = "/v1/meta/productusers/list";
//        String url = "http://bdmsmeta" + uri + "?apiKey={apiKey}&token={token}&product={product}";
//        Map<String,Object> param = new HashMap<>();
//        param.put("apiKey",URLEncoder.encode(constants.getApiKey(),"utf-8"));
//        param.put("token",URLEncoder.encode(authUtil.generateToken(constants.getMasterKey(),uri), "utf-8"));
//        param.put("product",product);
//
//        JSONObject result = restTemplateUtil.getForObject(url, JSONObject.class,param);
//
//        if (!Constants.SUCCESS_CODE.equals(result.getInteger("code"))) {
//            throw new DsException(ErrorCode.HTTPUTIL_REQUEST_FAIL, result.getString("msg"));
//        }
//        return result;
//    }
//
//    public JSONObject isAdmin(String email) throws Exception{
//        String uri = "/v1/account/user/is-admin";
//        String url = "http://bdmsmeta" + uri + "?apiKey={apiKey}&token={token}&service={service}&email={email}";
//        Map<String,Object> param = new HashMap<>();
//        param.put("apiKey",URLEncoder.encode(constants.getApiKey(),"utf-8"));
//        param.put("token",URLEncoder.encode(authUtil.generateToken(constants.getMasterKey(),uri), "utf-8"));
//        param.put("service",Constants.SERVICE_NAME);
//        param.put("email", email);
//
//        JSONObject result = restTemplateUtil.getForObject(url, JSONObject.class,param);
//
//        if (!Constants.SUCCESS_CODE.equals(result.getInteger("code"))) {
//            throw new DsException(ErrorCode.HTTPUTIL_REQUEST_FAIL, result.getString("msg"));
//        }
//        return result;
//    }
//
//    public JSONObject isUserBounded (String email, String product) throws Exception{
//
//        String uri = "/v1/account/user-bound/check";
//        String url = "http://bdmsmeta" + uri +
//                "?apiKey={apiKey}&token={token}&service={service}&email={email}&product={product}";
//        Map<String,Object> param = new HashMap<>();
//        param.put("apiKey",URLEncoder.encode(constants.getApiKey(),"utf-8"));
//        param.put("token",URLEncoder.encode(authUtil.generateToken(constants.getMasterKey(),uri), "utf-8"));
//        param.put("service",Constants.SERVICE_NAME);
//        param.put("email", email);
//        param.put("product", product);
//
//        JSONObject result = restTemplateUtil.getForObject(url, JSONObject.class,param);
//
//        if (!Constants.SUCCESS_CODE.equals(result.getInteger("code"))) {
//            throw new DsException(ErrorCode.HTTPUTIL_REQUEST_FAIL, result.getString("msg"));
//        }
//        return result;
//    }
//}
