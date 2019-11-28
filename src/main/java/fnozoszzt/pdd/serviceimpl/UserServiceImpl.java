//package com.netease.bdms.dsweb.serviceimpl;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.netease.bdms.dsweb.invoker.MetaInvoker;
//import com.netease.bdms.dsweb.param.BaseParam;
//import com.netease.bdms.dsweb.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class UserServiceImpl implements UserService {
//
//    // email 与 name映射
//    private static Map<String, String> userMaps = new HashMap<>();
//
//    @Autowired
//    private MetaInvoker metaInvoker;
//    @Autowired
//    private AuthService authService;
//
//    @Override
//    public JSONObject getUserProductList(BaseParam param) throws Exception {
//        authService.checkPermission(param);
//
//        JSONObject accounts = metaInvoker.getAccount(param.getEmail());
//        return ResponseHelper.genSuccessResult(accounts.getJSONArray("result"));
//    }
//
//    @Override
//    public String getUserName(String email) throws Exception {
//
//        // 首次被调用，全量查询
//        if (userMaps.isEmpty()) {
//            JSONArray users = metaInvoker.getAllUsers().getJSONArray("result");
//            synchronized (userMaps) {
//                for (Object one : users) {
//                    JSONObject user = (JSONObject) one;
//                    userMaps.put(user.getString("email"), user.getString("name"));
//                }
//            }
//        }
//
//        String name;
//        if (!userMaps.containsKey(email)) {
//            JSONObject ret = metaInvoker.getUserName(email);
//            name = ret.getJSONObject("result").getString("name");
//            synchronized (userMaps) {
//                userMaps.put(email, name);
//            }
//        } else {
//            name = userMaps.get(email);
//        }
//
//        return name;
//    }
//
//    @Override
//    public JSONObject getProductMembers (BaseParam param) throws Exception {
//        authService.checkPermission(param);
//
//        JSONObject productMembers = metaInvoker.getProductUsers(param.getProduct());
//        return ResponseHelper.genSuccessResult(productMembers.getJSONArray("result"));
//    }
//
//    // 获取用户的Principal和Keytab
//    @Override
//    public JSONObject getPrincipalAndKeytab(BaseParam param) throws Exception {
//        authService.checkPermission(param);
//
//        // 调用元数据服务
//        JSONObject dsJson = metaInvoker.getUserKey(param.getProduct());
//
//        // 封装前端所需的输出
//        String keytabName = param.getProduct()+".keytab";
//        JSONObject resultJson = dsJson.getJSONObject("result");
//        if(resultJson!=null) {
//            resultJson.put("keytab",keytabName);
//        }
//        return ResponseHelper.genSuccessResult(resultJson);
//    }
//
//    @Override
//    public JSONObject isAdmin (BaseParam param) throws Exception {
//
//        return metaInvoker.isAdmin(param.getEmail());
//    }
//
//}
