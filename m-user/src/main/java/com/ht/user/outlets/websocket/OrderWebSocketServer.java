//package com.ht.user.outlets.websocket;
//
//import com.alibaba.fastjson.JSONObject;
//import com.ht.user.sysconstant.entity.DicConstant;
//import com.ht.user.sysconstant.service.DicConstantService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.websocket.*;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@ServerEndpoint("/order/webSocket/{cashId}/{terId}")
//@Component
//public class OrderWebSocketServer {
//
//    private Logger logger = LoggerFactory.getLogger(OrderWebSocketServer.class);
//
//    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
//    private static AtomicInteger onlineNum = new AtomicInteger();
//
//    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
//    private static ConcurrentHashMap<String, ConcurrentHashMap<String,Session>> sessionPools = new ConcurrentHashMap<>();
//
//    //发送消息
//    public void sendMessage(Session session, String message) throws IOException {
//        if(session != null){
//            synchronized (session) {
////                System.out.println("发送数据：" + message);
//                session.getBasicRemote().sendText(message);
//            }
//        }
//    }
//
//    //给指定用户发送信息
//    public void sendInfo(String cashId, String message){
//        ConcurrentHashMap<String,Session> sessionMap = sessionPools.get(cashId);
//        try {
//            if (sessionMap!=null && sessionMap.size()>0) {
//                for (Session session : sessionMap.values()) {
//                    sendMessage(session, message);
//                }
//            }else {
//                logger.info("该cashId并无连接设备");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    //建立连接成功调用
//    @OnOpen
//    public void onOpen(Session session, @PathParam(value = "cashId") String cashId, @PathParam(value = "terId") String terId) {
//
//
//
//        ConcurrentHashMap<String,Session> sessionMap = sessionPools.get(cashId);
//        if (sessionMap!=null && sessionMap.size()>0){
//            sessionMap.put(terId,session);
//            sessionPools.put(cashId, sessionMap);
//        }else {
//            ConcurrentHashMap<String,Session> sessionMapSave = new ConcurrentHashMap<>();
//            sessionMapSave.put(terId,session);
//            sessionPools.put(cashId, sessionMapSave);
//        }
//        addOnlineCount();
//        logger.info(cashId + "加入webSocket！当前连接数为" + onlineNum);
//        try {
//            sendMessage(session, "success");
//        } catch (IOException e) {
//            logger.info(cashId + "加入webSocket异常:" + e.getMessage());
//        }
//    }
//
//    //关闭连接时调用
//    @OnClose
//    public void onClose(@PathParam(value = "cashId") String cashId){
//        sessionPools.remove(cashId);
//        subOnlineCount();
//        logger.info(cashId + "断开webSocket连接！当前连接数为" + onlineNum);
//    }
//
//    //收到客户端信息
//    @OnMessage
//    public void onMessage(String message) throws IOException{
//        logger.info("客户端：" + message + ",接收");
//    }
//
//    //错误时调用
//    @OnError
//    public void onError(Session session, Throwable throwable){
//        logger.info("WebSocket异常报错");
//        throwable.printStackTrace();
//    }
//
//    public static void addOnlineCount(){
//        onlineNum.incrementAndGet();
//    }
//
//    public static void subOnlineCount() {
//        onlineNum.decrementAndGet();
//    }
//}
