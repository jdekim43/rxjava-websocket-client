package kr.jadekim.rxjava.websocket;

import kr.jadekim.rxjava.websocket.annotation.WebSocketClient;
import kr.jadekim.rxjava.websocket.httpclient.Connection;
import kr.jadekim.rxjava.websocket.httpclient.ConnectionFactory;
import kr.jadekim.rxjava.websocket.httpclient.LazyConnectionFactory;
import kr.jadekim.rxjava.websocket.inbound.InboundParser;
import kr.jadekim.rxjava.websocket.listener.SimpleWebSocketEventListener;
import kr.jadekim.rxjava.websocket.listener.WebSocketEventListener;
import kr.jadekim.rxjava.websocket.outbound.OutboundSerializer;
import kr.jadekim.rxjava.websocket.processor.WebSocket;
import kr.jadekim.rxjava.websocket.processor.WebSocketClientProxy;

import java.lang.reflect.InvocationTargetException;

public class JWebSocket {

    private ConnectionFactory connectionFactory;
    private InboundParser parser;
    private OutboundSerializer serializer;
    private boolean isErrorPropagation;

    private JWebSocket() {
    }

    public static <T> T create(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(WebSocketClient.class)) {
            return null;
        }

        WebSocketClient config = clazz.getAnnotation(WebSocketClient.class);

        try {
            return new Builder()
                    .connectionFactory(config.connectionFactory().getConstructor().newInstance())
                    .parser(config.parser().getConstructor().newInstance())
                    .serializer(config.serializer().getConstructor().newInstance())
                    .lazy(config.isLazy())
                    .errorPropagation(config.isErrorPropagation())
                    .build()
                    .create(clazz, config.url(), config.listener().getConstructor().newInstance());
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Not Found Accessible Default Constructor");
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Not Found Accessible Default Constructor");
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Not Found Accessible Default Constructor");
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Not Found Accessible Default Constructor");
        }
    }

    public <T> T create(Class<T> clazz, String url) {
        return create(clazz, url, null);
    }

    public <T> T create(Class<T> clazz, String url, WebSocketEventListener listener) {
        if (listener == null) {
            listener = new SimpleWebSocketEventListener();
        }

        Connection connection = connectionFactory.connect(url, isErrorPropagation, listener);

        WebSocket webSocket = new WebSocket(connection, parser, serializer, listener);

        return WebSocketClientProxy.create(webSocket, clazz);
    }

    public static class Builder {

        private ConnectionFactory connectionFactory;
        private InboundParser parser;
        private OutboundSerializer serializer;
        private boolean isLazy = false;
        private boolean isErrorPropagation = true;

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

        public Builder errorPropagation(boolean enable) {
            this.isErrorPropagation = enable;

            return this;
        }

        public JWebSocket build() {
            JWebSocket webSocket = new JWebSocket();

            webSocket.connectionFactory = isLazy ? new LazyConnectionFactory(connectionFactory) : connectionFactory;
            webSocket.parser = parser;
            webSocket.serializer = serializer;
            webSocket.isErrorPropagation = isErrorPropagation;

            return webSocket;
        }
    }
}
