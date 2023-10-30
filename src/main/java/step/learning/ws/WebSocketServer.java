package step.learning.ws;

import com.google.inject.Inject;
import step.learning.dao.ChatDao;
import step.learning.dto.entities.ChatMessage;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

@ServerEndpoint(value = "/chat", configurator = WebSocketConfigurator.class)
public class WebSocketServer {

    private final static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private final ChatDao chatDao;

    @Inject
    public WebSocketServer(ChatDao chatDao) {
        this.chatDao = chatDao;
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig sec) {
        session.getUserProperties().put("user", sec.getUserProperties().get("user"));
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        String user = (String) session.getUserProperties().get("user");
        chatDao.addAsync(new ChatMessage(user, message));
        sendAll(user + ": " + message);
    }

    @OnError
    public void onError(Throwable e, Session session) {
        System.err.println("onError: " + e.getMessage());

    }

    private void sendAll(String message) {
        for(Session session : sessions) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                System.err.println("sendAll: " + e.getMessage());
            }
        }
    }
}
