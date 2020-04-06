package kr.jadekim.rxjava.websocket.processor;

import io.reactivex.rxjava3.core.Completable;
import kr.jadekim.rxjava.websocket.connection.Connection;
import kr.jadekim.rxjava.websocket.inbound.InboundParser;
import kr.jadekim.rxjava.websocket.listener.WebSocketEventListener;
import kr.jadekim.rxjava.websocket.outbound.OutboundSerializer;

import java.util.Map;

public class WebSocket {

    private Connection connection;
    private InboundParser parser;
    private OutboundSerializer serializer;
    private StreamRouter router;
    private MessageSender sender;
    private WebSocketEventListener listener;

    @SuppressWarnings("unchecked")
    public WebSocket(Connection connection, InboundParser parser, OutboundSerializer serializer, WebSocketEventListener listener) {
        this.connection = connection;
        this.parser = parser;
        this.serializer = serializer;
        this.router = new StreamRouter(parser, connection.getInboundStream(), listener);
        this.sender = new MessageSender(connection, serializer, listener);
        this.listener = listener;
    }

    public <Model> ChannelStream<Model> getStream(Subscription subscription) {
        return router.getStream(subscription);
    }

    public InboundParser getParser() {
        return parser;
    }

    public OutboundSerializer getSerializer() {
        return serializer;
    }

    public void disconnect() {
        listener.onDisconnectSocket(connection);

        connection.disconnect();
    }

    public Completable sendMessage(String messageType, Map<String, Object> parameterMap) {
        return sender.sendMessage(messageType, parameterMap);
    }
}
