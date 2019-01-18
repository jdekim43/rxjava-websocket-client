package kr.jadekim.rxjava.websocket.httpclient;

import io.reactivex.Observable;

public interface Connection {

    String getUrl();

    Observable<String> getInboundStream();

    boolean sendMessage(String message);

    void disconnect();
}
