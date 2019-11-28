//package com.netease.bdms.dsweb.websocket;
//
//import com.alibaba.fastjson.JSONObject;
//import com.netease.bdms.dsweb.common.Constants;
//import com.netease.bdms.dsweb.common.DsException;
//import com.netease.bdms.dsweb.domain.BdmsUser;
//import com.netease.bdms.dsweb.domain.Worker;
//import com.netease.bdms.dsweb.init.ApplicationContextHolder;
//import com.netease.bdms.dsweb.interceptor.SessionInterceptor;
//import com.netease.bdms.dsweb.param.task.MonitorQuery;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpSession;
//import javax.websocket.*;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.util.*;
//
//
//@ServerEndpoint(value = "/websocket", configurator = HTTPSessionConfig.class)
//@Component
//public class WebSocket {
//    private static final Logger logger = LoggerFactory.getLogger(WebSocket.class);
//
//    private Session session;
//    private String email;
//    private String product;
//
//    private MonitorQuery query;
//
//    private WebSocketManager webSocketManager;
//    private TaskService taskService;
//
//    private Timer taskTimer = new Timer();
//    private static final Long TASK_INTERVAL = 1 * 1000l;   //1秒推送一次数据
//
//    @Value("${login.use_bdms}")
//    private Boolean use_bdms;
//
//
//
//    private void initSocket(EndpointConfig config, Session session) throws Exception {
//        this.session = session;
//        HttpSession httpSession = (HttpSession) config.getUserProperties().get(Constants.WEBSOCKET_HTTP_SESSION);
//        if (httpSession == null) {
//            throw new DsException(ErrorCode.SESSION_INVALID, "invalid session, session is null");
//        }
//
//        this.webSocketManager = ApplicationContextHolder.get().getBean(WebSocketManager.class);
//        if (this.webSocketManager == null) {
//            throw new DsException(500, "server error, webSocketManager is null");
//        }
//        use_bdms = webSocketManager.use_bdms;
//
//        if (use_bdms) {
//            BdmsUser bdmsUser = (BdmsUser) httpSession.getAttribute(SessionInterceptor.BDMSUSER);
//            if (bdmsUser == null) {
//                throw new DsException(ErrorCode.SESSION_INVALID, "invalid session, worker is null");
//            }
//            this.email = bdmsUser.getEmail();
//            if (this.email == null) {
//                throw new DsException(ErrorCode.SESSION_INVALID, "invalid session, email is null");
//            }
//            this.product = bdmsUser.getProduct();
//            if (this.product == null) {
//                throw new DsException(ErrorCode.SESSION_INVALID, "invalid session, product is null");
//            }
//
//            this.webSocketManager = ApplicationContextHolder.get().getBean(WebSocketManager.class);
//            if (this.webSocketManager == null) {
//                throw new DsException(500, "server error, webSocketManager is null");
//            }
//
//            this.taskService = ApplicationContextHolder.get().getBean(TaskService.class);
//            if (this.taskService == null) {
//                throw new DsException(500, "server error, taskService is null");
//            }
//        } else {
//            Worker worker = (Worker) httpSession.getAttribute(SessionInterceptor.WORKER);
//            if (worker == null) {
//                throw new DsException(ErrorCode.SESSION_INVALID, "invalid session, worker is null");
//            }
//            this.email = worker.getEmail();
//            if (this.email == null) {
//                throw new DsException(ErrorCode.SESSION_INVALID, "invalid session, email is null");
//            }
//            this.product = worker.getProduct();
//            if (this.product == null) {
//                throw new DsException(ErrorCode.SESSION_INVALID, "invalid session, product is null");
//            }
//
//            this.webSocketManager = ApplicationContextHolder.get().getBean(WebSocketManager.class);
//            if (this.webSocketManager == null) {
//                throw new DsException(500, "server error, webSocketManager is null");
//            }
//
//            this.taskService = ApplicationContextHolder.get().getBean(TaskService.class);
//            if (this.taskService == null) {
//                throw new DsException(500, "server error, taskService is null");
//            }
//        }
//
//        query = new MonitorQuery();
//        query.init();
//    }
//
//    /**
//     * 建立连接时调用，将连接放入连接池
//     *
//     * @param config
//     * @param session
//     */
//    @OnOpen
//    public void onOpen(EndpointConfig config, final Session session) throws Exception {
//        try {
//            initSocket(config, session);
//        } catch (Exception e) {
//            logger.error("onOpen ", e);
//            logger.error(e.getMessage());
//            close();
//            return;
//        }
//
//        webSocketManager.addSocketToUser(this.email, this);
//        taskTimer.schedule(new TaskThread(), 0, TASK_INTERVAL);
//
//        logger.info("[WEBSOCKET] onOpen, " + this.email);
//    }
//
//    /**
//     * 收到客户端发送的消息时调用
//     *
//     * @param message
//     */
//    @OnMessage
//    public synchronized void onMessage(String message) throws Exception {
//        logger.info("email is " + this.email + ", on message is " + message);
//
//        MonitorQuery currQuery = JSONObject.parseObject(message, MonitorQuery.class);
//        currQuery.realTimeParamValidate();
//
//        // synchronized (query) {
//        query = currQuery;
//        // }
//    }
//
//    /**
//     * 关闭连接时被调用，将连接从连接池移除
//     *
//     * @return
//     */
//    @OnClose
//    public void onClose() {
//        webSocketManager.removeSocketFromUser(this.email, this);
//        logger.info("[WEBSOCKET] onClose, " + this.email);
//    }
//
//    /**
//     * The error event will only ever be fired prior to then also firing the close event,
//     * at least by implementations that properly implement the specification,
//     * ie you will get error andclose as a pair, or just close by itself
//     *
//     * @param session
//     * @param error
//     */
//    @OnError
//    public void onError(Session session, Throwable error) {
//        logger.error(String.format("[WEBSOCKET] onError, " + this.email), error);
//    }
//
//
//    private void sendMessage(String message) {
//        try {
//            this.session.getBasicRemote().sendText(message);
//        } catch (IOException e) {
//            logger.error("[WEBSOCKET] sendMessage error, " + this.email + ", " + message, e);
//            //从连接池中移除本次连接
//            webSocketManager.removeSocketFromUser(this.email, this);
//            close();
//        }
//    }
//
//    private boolean isOpen() {
//        if (session != null) {
//            return session.isOpen();
//        }
//        return false;
//    }
//
//    public void close() {
//        if (isOpen()) {
//            try {
//                session.close();    //关闭本次连接
//            } catch (IOException e) {
//                logger.error("[WEBSOCKET] session close error, " + this.email, e);
//            }
//        }
//    }
//
//    class TaskThread extends TimerTask {
//        @Override
//        public void run() {
//            try {
//                //连接关闭，取消定时器
//                if (!isOpen()) {
//                    taskTimer.cancel();
//                    return;
//                }
//
//                // synchronized (query) {
//                if (query.getTypes().isEmpty()) {
//                    // 不需要获取实时数据，定时ping消息即可
//                    final byte[] bs = {'p'};
//                    final ByteBuffer bb = ByteBuffer.wrap(bs);
//                    session.getBasicRemote().sendPing(bb);
//                } else if (query.getTypes().contains(Constants.RealTimeDataType.FILE_MONITOR.toString())) {
//                    // 获取文件监控信息
//                    query.pageParamValidate();
//
//                    if (query.getTaskId() != null && query.getAgentId() != null) {
//                        JSONObject data = taskService.getFileMonitorData(
//                                product, query.getTaskId(), query.getAgentId(), query.getPage(), query.getPageSize());
//                        sendMessage(ResponseHelper.genSuccessResultString(data));
//                    } else {
//                        logger.info("wait for query");
//                    }
//                } else {
//                    String types = "";
//                    // 获取采集速率、采集延迟、参数延迟的一个或多个
//                    if (query.getTypes().contains(Constants.RealTimeDataType.COLLECT_SPEED.toString())) {
//                        types += "0;";
//                    }
//                    if (query.getTypes().contains(Constants.RealTimeDataType.COLLECT_DELAY.toString())) {
//                        types += "1;";
//                    }
//                    if (query.getTypes().contains(Constants.RealTimeDataType.TRANSMISSION_DELAY.toString())) {
//                        types += "2;";
//                    }
//
//                    if (!types.isEmpty()) {
//                        types = types.substring(0, types.length() - 1);
//                    }
//
//                    if (query.getTaskId() != null) {
//                        JSONObject data = taskService.getRealtimeData(
//                                product, query.getTaskId(), query.getAgentId(), types);
//                        sendMessage(ResponseHelper.genSuccessResultString(data));
//                    } else {
//                        logger.info("wait for query ……");
//                    }
//                }
//                // }
//            } catch (Exception e) {
//                logger.error("[WEBSOCKET] sendmsg error", e);
//                // 某次调用异常，不取消定时器
//                // taskTimer.cancel();
//            }
//        }
//    }
//
//}
