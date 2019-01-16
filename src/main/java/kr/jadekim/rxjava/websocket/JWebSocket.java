package kr.jadekim.rxjava.websocket;

import kr.jadekim.rxjava.websocket.annotation.WebSocketClient;
import kr.jadekim.rxjava.websocket.httpclient.Connection;
import kr.jadekim.rxjava.websocket.httpclient.ConnectionFactory;
import kr.jadekim.rxjava.websocket.httpclient.LazyConnectionFactory;
import kr.jadekim.rxjava.websocket.inbound.InboundParser;
import kr.jadekim.rxjava.websocket.outbound.OutboundSerializer;
import kr.jadekim.rxjava.websocket.processor.WebSocket;
import kr.jadekim.rxjava.websocket.processor.WebSocketClientProxy;

public class JWebSocket {

    private ConnectionFactory connectionFactory;
    private InboundParser parser;
    private OutboundSerializer serializer;

    public JWebSocket(ConnectionFactory connectionFactory, InboundParser parser, OutboundSerializer serializer, boolean isLazy) {
        this.connectionFactory = isLazy ? new LazyConnectionFactory(connectionFactory) : connectionFactory;
        this.parser = parser;
        this.serializer = serializer;
    }

    public <T> T create(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(WebSocketClient.class)) {
            return null;
        }

        return create(clazz, clazz.getAnnotation(WebSocketClient.class).url());
    }

    public <T> T create(Class<T> clazz, String url) {
        Connection connection = connectionFactory.connect(url);
        WebSocket webSocket = new WebSocket(connection, parser, serializer);

        return WebSocketClientProxy.create(webSocket, clazz);
    }

    public static class Builder {

        private ConnectionFactory connectionFactory;
        private InboundParser parser;
        private OutboundSerializer serializer;
        private boolean isLazy = false;

        public Builder connectionFactory(ConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;

            return this;
        }

        public Builder parser(InboundParser parser) {
            this.parser = parser;

            return this;
        }

        public Builder serializer(OutboundSerializer serializer) {
            this.serializer = serializer;

            return this;
        }

        public Builder lazy(boolean enable) {
            this.isLazy = enable;

            return this;
        }

        public JWebSocket build() {
            return new JWebSocket(connectionFactory, parser, serializer, isLazy);
        }
    }
}
