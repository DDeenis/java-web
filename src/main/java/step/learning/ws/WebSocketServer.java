package step.learning.ws;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import step.learning.dao.AuthTokenDao;
import step.learning.dao.ChatDao;
import step.learning.dto.entities.AuthToken;
import step.learning.dto.entities.ChatMessage;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint(value = "/chat", configurator = WebSocketConfigurator.class)
public class WebSocketServer {

    private final static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private final ChatDao chatDao;
    private final AuthTokenDao tokenDao;

    @Inject
    public WebSocketServer(ChatDao chatDao, AuthTokenDao tokenDao) {
        this.chatDao = chatDao;
        this.tokenDao = tokenDao;
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
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        JsonObject response = new JsonObject();
        String command = jsonObject.get("command").getAsString();
        String data = jsonObject.get("data").getAsString();
        switch (command) {
            case "auth":
                AuthToken token = tokenDao.getTokenByBearer(data);
                if(token == null) {
                    sendToSession(session, 403, "Token rejected");
                    try {
                        session.close();
                    } catch (IOException ignored) {}
                    return;
                }
                session.getUserProperties().put("auth", token.getSub());
                session.getUserProperties().put("nik", token.getNik());
                sendToSession(session, 202, token.getSub());
                break;

            case "chat":
                String sub = (String) session.getUserProperties().get("auth");
                String nik = (String) session.getUserProperties().get("nik");
                if(sub == null) {
                    sendToSession(session, 401, "Auth required");
                    return;
                }
                JsonObject messageObject = JsonParser.parseString(message).getAsJsonObject();
                String messageText = messageObject.get("data").getAsString();

                ChatMessage chatMessage = new ChatMessage(sub, message);
                chatDao.addAsync(chatMessage);
                JsonObject responseObject = new JsonObject();
                responseObject.addProperty("user", nik);
                responseObject.addProperty("message", messageText);
                sendAll(201, responseObject.toString());
                break;

            default:
                break;
        }
    }

    @OnError
    public void onError(Throwable e, Session session) {
        System.err.println("onError: " + e);

    }

    private void sendAll(int status, String data) {
        for(Session session : sessions) {
            sendToSession(session, status, data);
        }
    }

    private void sendToSession(Session session, int status, String data) {
        JsonObject response = new JsonObject();
        response.addProperty("status", status);
        response.addProperty("data", data);
        try {
            session.getBasicRemote().sendText(response.toString());
        } catch (IOException e) {
            System.err.println("auth: " + e.getMessage());
        }
    }
}
