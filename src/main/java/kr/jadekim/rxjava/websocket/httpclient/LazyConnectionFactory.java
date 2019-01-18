package kr.jadekim.rxjava.websocket.httpclient;

public class LazyConnectionFactory implements ConnectionFactory {

    private ConnectionFactory originConnectionFactory;

    public LazyConnectionFactory(ConnectionFactory connectionFactory) {
        this.originConnectionFactory = connectionFactory;
    }

    @Override
    public Connection connect(String url, boolean isErrorPropagation) {
        return new LazyConnection(originConnectionFactory, url, isErrorPropagation);
    }

    public ConnectionFactory getOriginConnectionFactory() {
        return originConnectionFactory;
    }
}
