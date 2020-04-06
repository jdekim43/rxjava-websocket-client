package kr.jadekim.rxjava.websocket.connection;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Supplier;
import kr.jadekim.rxjava.websocket.listener.WebSocketEventListener;

import java.util.concurrent.Callable;

public class LazyConnection implements Connection {

    private ConnectionFactory connectionFactory;
    private String url;
    private boolean isErrorPropagation;
    private WebSocketEventListener listener;

    private volatile Connection connection;
    private volatile Observable<String> stream;

    public LazyConnection(ConnectionFactory connectionFactory, String url, boolean isErrorPropagation, WebSocketEventListener listener) {
        this.connectionFactory = connectionFactory;
        this.url = url;
        this.isErrorPropagation = isErrorPropagation;
        this.listener = listener;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Observable<String> getInboundStream() {
        return Observable.defer(new Supplier<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> get() throws Throwable {
                if (connection == null) {
                    connect();
                }
                return stream;
            }
        });
    }

    @Override
    public boolean sendMessage(String message) {
        if (connection == null) {
            connect();
        }

        return connection.sendMessage(message);
    }

    @Override
    public void disconnect() {
        synchronized (this) {
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
    }

    public synchronized Connection connect() {
        if (connection == null) {
            connection = connectionFactory.connect(url, isErrorPropagation, listener);
            stream = connection.getInboundStream()
                    .doFinally(new Action() {
                        @Override
                        public void run() throws Exception {
                            disconnect();
                        }
                    })
                    .share();
        }

        return connection;
    }
}
