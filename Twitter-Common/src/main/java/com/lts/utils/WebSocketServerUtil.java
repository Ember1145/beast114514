package com.lts.utils;

import com.alibaba.fastjson.JSON;
import com.lts.constant.RabbitConstant;
import com.lts.context.userContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

@Component
@ServerEndpoint("/webSocket/{uId}")
@Slf4j
@RequiredArgsConstructor
public class WebSocketServerUtil {

    private Session session;
    private static CopyOnWriteArraySet<WebSocketServerUtil> webSocketSet = new CopyOnWriteArraySet<>();
    private static ConcurrentHashMap<Long, WebSocketServerUtil> webSocketMap = new ConcurrentHashMap<>();
    private Long uId = null;
 //   private final StringRedisTemplate stringRedisTemplate;

//    @Autowired
//    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
//        WebSocketServerUtil.rabbitTemplate = rabbitTemplate;
//    }
public static boolean isUserOnline(Long userId) {
    return webSocketMap.containsKey(userId);
}
    @OnOpen
    public void onOpen(Session session, @PathParam("uId") Long uId) throws Exception {

        WebSocketServerUtil existingSession = webSocketMap.get(uId);
        if (existingSession != null) {
            sendInfo(JSON.toJSONString("FORCE_LOGOUT"),uId);
            existingSession.session.close(); // 这将关闭旧的WebSocket连接
        }
        this.session = session;
        this.uId = uId;
        webSocketMap.put(uId, this);
        webSocketSet.add(this);
//         stringRedisTemplate.opsForSet().add("online:",uId.toString());
        log.info("【websocket消息】有新的连接，总数：{}", webSocketMap.size());
    }

    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(uId)) {
            webSocketMap.remove(uId);
            webSocketSet.remove(this);
//            stringRedisTemplate.opsForSet().remove("online:",uId.toString());
        }
        log.info("【websocket消息】连接断开，总数：{}", webSocketSet.size());
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("【websocket消息】收到客户端发来的消息：{}", message);
    }

    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("Error sending message: ", e);
        }
    }

    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, Long uId) throws Exception {
        Long userId = userContext.getUserId();
        if(uId.equals(userId)){
            return;
        }
        if (webSocketMap.containsKey(uId)) {
            webSocketMap.get(uId).sendMessage(message);
            // 存储到Redis

        } else {
            log.error("用户" + uId + ",不在线！");
//            rabbitTemplate.convertAndSend(RabbitConstant.USER_NOTIFICATIONS_QUEUE, message);
            // 存储到Redis，标记为未读

            throw new Exception("连接已关闭，请刷新页面后重试");
        }
    }
}
