//package com.netease.bdms.dsweb.websocket;
//
//import com.netease.bdms.dsweb.common.Constants;
//
//import javax.servlet.http.HttpSession;
//import javax.websocket.HandshakeResponse;
//import javax.websocket.server.HandshakeRequest;
//import javax.websocket.server.ServerEndpointConfig;
//
//
//public class HTTPSessionConfig extends ServerEndpointConfig.Configurator {
//    @Override
//    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
//    	HttpSession httpSession = (HttpSession) request.getHttpSession();
//        sec.getUserProperties().put(Constants.WEBSOCKET_HTTP_SESSION, httpSession);
//    }
//}
