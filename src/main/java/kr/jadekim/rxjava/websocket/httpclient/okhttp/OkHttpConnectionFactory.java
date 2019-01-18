package kr.jadekim.rxjava.websocket.httpclient.okhttp;

import kr.jadekim.rxjava.websocket.httpclient.Connection;
import kr.jadekim.rxjava.websocket.httpclient.ConnectionFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkHttpConnectionFactory implements ConnectionFactory {

    private OkHttpClient okHttpClient;
    private Request baseRequest;

    public OkHttpConnectionFactory(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public Connection connect(String url, boolean isErrorPropagation) {
        Request.Builder builder = baseRequest == null ? new Request.Builder() : baseRequest.newBuilder();
        Request request = builder.url(url).build();

        return new OkHttpConnection(okHttpClient, request, isErrorPropagation).connect();
    }

    public void setBaseRequest(Request baseRequest) {
        this.baseRequest = baseRequest;
    }
}
