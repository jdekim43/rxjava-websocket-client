package kr.jadekim.rxjava.websocket.connection.okhttp;

import kr.jadekim.rxjava.websocket.connection.Connection;
import kr.jadekim.rxjava.websocket.connection.ConnectionFactory;
import kr.jadekim.rxjava.websocket.listener.WebSocketEventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkHttpConnectionFactory implements ConnectionFactory {

    private OkHttpClient okHttpClient;
    private Request baseRequest;

    public OkHttpConnectionFactory() {
        this.okHttpClient = new OkHttpClient.Builder().build();
    }

    public OkHttpConnectionFactory(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public Connection connect(String url, boolean isErrorPropagation, WebSocketEventListener listener) {
        Request.Builder builder = baseRequest == null ? new Request.Builder() : baseRequest.newBuilder();
        Request request = builder.url(url).build();

        return new OkHttpConnection(url, okHttpClient, request, isErrorPropagation, listener).connect();
    }

    public void setBaseRequest(Request baseRequest) {
        this.baseRequest = baseRequest;
    }
}
