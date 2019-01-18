package kr.jadekim.rxjava.websocket.httpclient;

public interface ConnectionFactory {

    Connection connect(String url, boolean isErrorPropagation);
}
