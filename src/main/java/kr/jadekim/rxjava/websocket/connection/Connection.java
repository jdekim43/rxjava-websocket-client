package kr.jadekim.rxjava.websocket.connection;

import io.reactivex.rxjava3.core.Observable;

public interface Connection {

    String getUrl();

    Observable<String> getInboundStream();

    boolean sendMessage(String message);

    void disconnect();
}
