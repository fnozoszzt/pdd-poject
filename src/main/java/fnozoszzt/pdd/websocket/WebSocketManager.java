//package com.netease.bdms.dsweb.websocket;
//
//import com.google.common.collect.HashMultimap;
//import com.google.common.collect.Multimap;
//import com.google.common.collect.Multimaps;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import rx.Observable;
//import rx.functions.Action1;
//
//import javax.annotation.PostConstruct;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.concurrent.TimeUnit;
//
//
//@Service
//public class WebSocketManager {
//
//    private static final Logger logger = LoggerFactory.getLogger(WebSocketManager.class);
//    private static final Integer TIMER_INTERVAL = 5;    //minute
//    @Value("${login.use_bdms}")
//    public Boolean use_bdms;
//
//    //使用线程安全的multimap, 一个用户可以打开多个浏览器窗口，保存每个窗口连接
//    private Multimap<String, WebSocket> connMaps =
//            Multimaps.synchronizedMultimap(HashMultimap.<String, WebSocket>create());
//
//
//    //检测线程，打印 websocket 连接数
//    @PostConstruct
//    public void init(){
//
//        Observable.interval(TIMER_INTERVAL, TimeUnit.MINUTES).subscribe(new Action1<Long>() {
//            @Override
//            public void call(Long aLong) {
//                logger.info("[WEBSOCKET] connections num: " + connMaps.values().size());
//            }
//        });
//
//    }
//
//    public void addSocketToUser(String email, WebSocket socket) {
//        synchronized (connMaps) {
//            this.connMaps.put(email, socket);
//            logger.info("[WEBSOCKET] addSocketToUser, " + email);
//        }
//    }
//
//    public void removeSocketFromUser(String email, WebSocket socket) {
//        synchronized (connMaps){
//            //email不存在时返回 empty collection, 非null
//            Collection<WebSocket> sockets = connMaps.get(email);
//            if (sockets.isEmpty()) {
//                logger.warn("NO [WEBSOCKET] can be removed, " + email);
//                return;
//            }
//
//            sockets.remove(socket);
//            logger.info("[WEBSOCKET] removeSocketFromUser, " + email);
//        }
//    }
//
//    public void removeAllSocketsFromUser (String email) {
//        synchronized (connMaps){
//            Collection<WebSocket> sockets = connMaps.get(email);
//            if (sockets.isEmpty()) {
//                logger.warn("NO [WEBSOCKET] can be removed, " + email);
//                return;
//            }
//
//            Iterator<WebSocket> iter = sockets.iterator();
//            while (iter.hasNext()) {
//                WebSocket socket = iter.next();
//                socket.close();
//            }
//            connMaps.removeAll(email);
//
//            logger.info("[WEBSOCKET] remove all sockets from user, " + email);
//        }
//    }
//
//}