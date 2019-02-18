package kr.jadekim.rxjava.websocket.connection;

import kr.jadekim.rxjava.websocket.listener.WebSocketEventListener;

public class LazyConnectionFactory implements ConnectionFactory {

    private ConnectionFactory originConnectionFactory;

    public LazyConnectionFactory(ConnectionFactory connectionFactory) {
        this.originConnectionFactory = connectionFactory;
    }

    @Override
    public Connection connect(String url, boolean isErrorPropagation, WebSocketEventListener listener) {
        return new LazyConnection(originConnectionFactory, url, isErrorPropagation, listener);
    }

    public ConnectionFactory getOriginConnectionFactory() {
        return originConnectionFactory;
    }
}
