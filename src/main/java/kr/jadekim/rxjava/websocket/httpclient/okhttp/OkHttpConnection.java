package kr.jadekim.rxjava.websocket.httpclient.okhttp;

import io.reactivex.Observable;
import kr.jadekim.rxjava.websocket.httpclient.Connection;
import okhttp3.*;
import okio.ByteString;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class OkHttpConnection extends WebSocketListener implements Connection, Publisher<String> {

    private OkHttpClient okHttpClient;
    private Request request;
    private WebSocket webSocket;
    private Subscriber<? super String> subscriber;
    private boolean isOpened = false;

    public OkHttpConnection(OkHttpClient okHttpClient, Request request) {
        this.okHttpClient = okHttpClient;
        this.request = request;
    }

    @Override
    public Observable<String> getInboundStream() {
        return Observable.fromPublisher(this);
    }

    @Override
    public boolean sendMessage(String message) {
        if (webSocket == null) {
            return false;
        }

        return webSocket.send(message);
    }

    @Override
    public void disconnect() {
        if (webSocket == null) {
            return;
        }

        webSocket.close(1000, null);
    }

    @Override
    public void subscribe(Subscriber<? super String> s) {
        this.subscriber = s;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.isOpened = true;
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        subscriber.onNext(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        //do nothing (not support)
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        //do nothing
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        this.isOpened = false;
        subscriber.onComplete();
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        subscriber.onError(new OkHttpWebSocketException(t, response));
    }

    public OkHttpConnection connect() {
        if (webSocket != null && isOpened) {
            webSocket.close(1000, "Another socket is connected");
        }

        this.webSocket = okHttpClient.newWebSocket(request, this);

        return this;
    }

    public static class OkHttpWebSocketException extends RuntimeException {

        public final Response response;

        OkHttpWebSocketException(Throwable cause, Response response) {
            super(cause);
            this.response = response;
        }
    }
}
