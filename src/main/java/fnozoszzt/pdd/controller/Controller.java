//package com.netease.bdms.dsweb.controller;
//
//import com.alibaba.fastjson.JSONObject;
//import com.netease.bdms.dsweb.common.*;
//import com.netease.bdms.dsweb.domain.Worker;
//import com.netease.bdms.dsweb.interceptor.SessionInterceptor;
//import com.netease.bdms.dsweb.invoker.MetaInvoker;
//import com.netease.bdms.dsweb.util.RedisUtil;
//import com.netease.bdms.dsweb.websocket.WebSocketManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.net.URLEncoder;
//import java.util.List;
//
//@Controller
//public class LoginController {
//
//    private static final Logger logger = LoggerFactory.getLogger("login");
//
//    @Autowired
//    private WebSocketManager socketManager;
//    @Autowired
//    private MetaInvoker metaInvoker;
//    @Autowired
//    private RedisUtil redisUtil;
//
//    @Value("${login.use_bdms}")
//    private Boolean use_bdms;
//    @Autowired
//    private BdmsLoginService loginService;
//
//    @RequestMapping("/login")
//    public void login (HttpServletRequest request, HttpServletResponse response) throws Exception {
//        logger.info("login use_bdms {}", use_bdms);
//        if (use_bdms) {
//            loginService.login(request, response);
//
//        } else {
//            String serverName = request.getServerName();
//            logger.info("server name is " + serverName);
//            String referer = request.getHeader("referer");
//            logger.info("referer is " + referer);
//
//            // 开发服务器，前端调试login fake
//            //if (referer != null && referer.startsWith("http://localhost:8000/")) {
//            if (referer != null && referer.contains("://localhost")) {
//                OpenIDHelper.loginfake(request, response);
//                response.sendRedirect(referer);
//                return;
//            }
//
//            String redirect = OpenIDHelper.getRedirectURL(
//                    request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/",
//                    request.getSession(), "/api/logon");
//
//            logger.info("redirect {}", redirect);
//            response.sendRedirect(redirect);
//        }
//    }
//
//    @RequestMapping("/logon")
//    public void logon (HttpServletRequest request, HttpServletResponse response) throws Exception{
//        logger.info("logon use_bdms {}", use_bdms);
//        if (use_bdms) {
//            loginService.logon(request, response);
//
//        } else {
//            HttpSession session = request.getSession();
//            logger.info("request servername is " + request.getServerName());
//
//            Worker worker = OpenIDHelper.checkAuth(request);
//            if (worker == null) {
//                logger.error("worker is null");
//                response.sendRedirect("/register");
//                return;
//            }
//
//            try {
//                // 从元数据服务获取当前email绑定的product列表
//                JSONObject accounts = metaInvoker.getAccount(worker.getEmail());
//                List<String> products = accounts.getJSONArray("result").toJavaList(String.class);
//
//                if (products == null || products.isEmpty()) {
//                    logger.error("no product can be found for user " + worker.getEmail());
//                    response.sendRedirect("/register");
//                    return;
//                }
//
//                String lastProduct = redisUtil.get(worker.getEmail());
//                if (lastProduct != null && products.contains(lastProduct)) {
//                    // 使用上次的账号信息
//                    worker.setProduct(lastProduct);
//                } else {
//                    redisUtil.set(worker.getEmail(), products.get(0));
//                    worker.setProduct(products.get(0));
//                }
//
//                session.setAttribute(SessionInterceptor.WORKER, worker);
//            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
//                response.sendRedirect("/register");
//                return;
//            }
//
//            Cookie c = new Cookie("username", URLEncoder.encode(worker.getUsername(), "utf-8"));
//            c.setPath("/");
//            response.addCookie(c);
//            c = new Cookie("email", URLEncoder.encode(worker.getEmail(), "utf-8"));
//            c.setPath("/");
//            response.addCookie(c);
//            c = new Cookie("product", String.valueOf(worker.getProduct()));
//            c.setPath("/");
//            response.addCookie(c);
//            response.setHeader("Cache-Control", "no-cache");
//
//            response.sendRedirect("/");
//        }
//    }
//
//    @RequestMapping(value = "/check/login", method = RequestMethod.GET)
//    @ResponseBody
//    public JSONObject checkLogin (HttpServletRequest request) throws Exception {
//        logger.info("checkLogin use_bdms {}", use_bdms);
//
//        if (use_bdms) {
//            return loginService.checkLogin(request);
//
//        } else {
//            JSONObject ret = new JSONObject();
//
//            HttpSession httpSession = request.getSession();
//            Worker worker = (Worker) httpSession.getAttribute(SessionInterceptor.WORKER);
//            if (worker == null) {
//                ret.put("logon", false);
//            } else {
//                ret.put("logon", true);
//            }
//
//            return ret;
//        }
//    }
//
//    @RequestMapping("/logout")
//    @ResponseBody
//    public JSONObject logout(HttpServletRequest request, HttpServletResponse response) throws Exception{
//        logger.info("logout use_bdms {}", use_bdms);
//
//        if (use_bdms) {
//            return loginService.logout(request, response);
//        } else {
//            HttpSession httpSession = request.getSession();
//            httpSession.removeAttribute(SessionInterceptor.WORKER);
//            httpSession.invalidate();
//
//            return ResponseHelper.genSuccessResult(null);
//        }
//    }
//
//    @RequestMapping("/clear")
//    @ResponseBody
//    public JSONObject clear(HttpServletRequest request, HttpServletResponse response) throws Exception{
//        logger.info("clear use_bdms {}", use_bdms);
//
//        if (use_bdms) {
//            return loginService.clear(request);
//        } else {
//            return ResponseHelper.genSuccessResult(null);
//        }
//    }
//
//    @RequestMapping(value = "/switch")
//    @ResponseBody
//    public JSONObject switchProduct(@RequestParam String product,
//                                    HttpServletRequest request,
//                                    HttpServletResponse response)  throws Exception{
//        if (use_bdms) {
//            return loginService.switchProduct(request, response, product);
//        } else {
//            HttpSession session = request.getSession();
//            Worker worker = (Worker) session.getAttribute(SessionInterceptor.WORKER);
//            // 调用元数据服务，判断email是否与将要切换的product绑定
//            JSONObject accounts = metaInvoker.getAccount(worker.getEmail());
//            List<String> products = accounts.getJSONArray("result").toJavaList(String.class);
//            if (!products.contains(product)) {
//                throw new DsException(ErrorCode.ILLEGAL_PARAMS, ErrorMessage.PRODUCT_ERROR);
//            }
//
//            worker.setProduct(product);
//            redisUtil.set(worker.getEmail(), product);
//
//            session.setAttribute(SessionInterceptor.WORKER, worker);
//
//            // 清除websocet连接
//            socketManager.removeAllSocketsFromUser(worker.getEmail());
//
//            // 更新cookie
//            Cookie c = new Cookie("username", URLEncoder.encode(worker.getUsername(), "utf-8"));
//            c.setPath("/");
//            response.addCookie(c);
//            c = new Cookie("email", URLEncoder.encode(worker.getEmail(), "utf-8"));
//            c.setPath("/");
//            response.addCookie(c);
//            c = new Cookie("product", String.valueOf(worker.getProduct()));
//            c.setPath("/");
//            response.addCookie(c);
//            response.setHeader("Cache-Control", "no-cache");
//
//            return ResponseHelper.genSuccessResult(null);
//        }
//    }
//}
