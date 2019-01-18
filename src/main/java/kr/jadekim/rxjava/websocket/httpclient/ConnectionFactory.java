package kr.jadekim.rxjava.websocket.httpclient;

import kr.jadekim.rxjava.websocket.listener.WebSocketEventListener;

public interface ConnectionFactory {

    Connection connect(String url, boolean isErrorPropagation, WebSocketEventListener listener);
}
