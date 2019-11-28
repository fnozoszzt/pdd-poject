//package com.netease.bdms.dsweb.interceptor;
//
//import com.alibaba.fastjson.JSONObject;
//import com.netease.bdms.dsweb.domain.BdmsUser;
//import com.netease.bdms.dsweb.domain.Worker;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.OutputStream;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class SessionInterceptor extends HandlerInterceptorAdapter {
//    private static Logger logger = Logger.getLogger(SessionInterceptor.class);
//
//    public static final String WORKER = "worker";
//    public static final String BDMSUSER = "BdmsUser";
//    public static final String TOKEN = "TOKEN";
//
//    @Value("${login.use_bdms}")
//    private Boolean use_bdms;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//
//        HttpSession httpSession = request.getSession();
//        logger.info("httpSession " + httpSession + " " + httpSession.getId() + " use_bdms " + use_bdms);
//
//        if (isExcluded(request.getServletPath())) {
//            return true;
//        }
//
//        if (use_bdms) {
//            BdmsUser worker = (BdmsUser) httpSession.getAttribute(BDMSUSER);
//            if (worker != null) {
//                //有效session
//            } else {
//                response.setCharacterEncoding("utf-8");
//                response.setContentType("application/json");
//                Map<String, Object> rtn = new HashMap<String, Object>();
//                rtn.put("code", ErrorCode.SESSION_INVALID);
//                byte[] buf = JSONObject.toJSONBytes(rtn);
//                OutputStream os = response.getOutputStream();
//                os.write(buf);
//                os.close();
//                logger.info("preHandle false");
//
//                return false;
//            }
//            logger.info("preHandle true");
//            return true;
//        } else {
//            Worker worker = (Worker) httpSession.getAttribute(WORKER);
//            if (worker != null) {
//                //有效session
//            } else {
//                response.setCharacterEncoding("utf-8");
//                response.setContentType("application/json");
//                Map<String, Object> rtn = new HashMap<String, Object>();
//                rtn.put("code", ErrorCode.SESSION_INVALID);
//                byte[] buf = JSONObject.toJSONBytes(rtn);
//                OutputStream os = response.getOutputStream();
//                os.write(buf);
//                os.close();
//                logger.info("preHandle false");
//
//                return false;
//            }
//            logger.info("preHandle true");
//            return true;
//        }
//    }
//
//    private boolean isExcluded(String url) {
//        if (url.startsWith("/login") || url.startsWith("/logon") || url.startsWith("/check/login")
//                || url.startsWith("/openapi")) {
//            return true;
//        }
//
//        if (url.startsWith("/clear")) {
//            return true;
//        }
//
//        return false;
//    }
//}
