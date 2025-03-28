package com.roc.his.api.socket;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.roc.his.api.config.sa_token.StpCustomerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@ServerEndpoint(value = "/socket")
@Component
public class WebSocketService {
    public static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Session, ScheduledExecutorService> executorMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        log.info("websocket已连接, {}", session.getId());
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        executorMap.put(session, scheduler);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                session.getBasicRemote().sendPing(ByteBuffer.wrap("ping".getBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 0, 4, TimeUnit.MINUTES);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        Map map = session.getUserProperties();
        if (map.containsKey("userId")) {
            String userId = MapUtil.getStr(map, "userId");
            sessionMap.get(userId).close();
            sessionMap.remove(userId);
        }
        executorMap.get(session).shutdown();
        executorMap.remove(session);
        log.info("websocket已关闭, {}", session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject json = JSONUtil.parseObj(message);
        //执行的命令
        String opt = json.getStr("opt");
        //用户身份(业务端或者mis端)
        String identity = json.getStr("identity");
        //提交的令牌
        String token = json.getStr("token");
        String userId = null;
        if ("customer".equals(identity)) {
            userId = "customer_" + StpCustomerUtil.getLoginIdByToken(token).toString();
        } else {
            userId = "user_" + StpUtil.getLoginIdByToken(token).toString();
        }

        Map map = session.getUserProperties();
        map.put("userId", userId);

        if (sessionMap.containsKey(userId)) {
            sessionMap.replace(userId, session);
        } else {
            sessionMap.put(userId, session);
        }

        if ("ping".equals(opt)) {
            return;
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误", error);
    }

    public static void sendInfo(String message, String userId) {
        if (StrUtil.isNotBlank(userId) && sessionMap.containsKey(userId)) {
            Session session = sessionMap.get(userId);
            sendMessage(message, session);
        }
    }

    private static void sendMessage(String message, Session session) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("执行异常", e);
        }
    }
}

