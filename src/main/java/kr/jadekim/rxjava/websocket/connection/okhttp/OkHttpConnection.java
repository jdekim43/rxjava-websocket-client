package kr.jadekim.rxjava.websocket.connection.okhttp;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import kr.jadekim.rxjava.websocket.connection.Connection;
import kr.jadekim.rxjava.websocket.listener.WebSocketEventListener;
import okhttp3.*;
import okio.ByteString;

public class OkHttpConnection extends WebSocketListener implements Connection, ObservableOnSubscribe<String> {

    private String url;
    private OkHttpClient okHttpClient;
    private Request request;
    private WebSocket webSocket;
    private ObservableEmitter<String> emitter;
    private boolean isErrorPropagation;
    private boolean isOpened = false;
    private WebSocketEventListener listener;

    public OkHttpConnection(String url, OkHttpClient okHttpClient, Request request, boolean isErrorPropagation, WebSocketEventListener listener) {
        this.url = url;
        this.okHttpClient = okHttpClient;
        this.request = request;
        this.isErrorPropagation = isErrorPropagation;
        this.listener = listener;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Observable<String> getInboundStream() {
        return Observable.create(this);
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
        webSocket = null;
    }

    @Override
    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        this.emitter = emitter;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        this.isOpened = true;

        listener.onConnectedSocket(this);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        if (emitter != null) {
            emitter.onNext(text);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        //do nothing (not support)
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        listener.onDisconnectSocket(this);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        this.isOpened = false;

        if (emitter != null) {
            emitter.onComplete();
        }

        listener.onDisconnectedSocket(this);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        this.isOpened = false;

        if (isErrorPropagation && emitter != null) {
            emitter.onError(new OkHttpWebSocketException(t, response));
        }

        listener.onErrorSocket(t);

        if (!isErrorPropagation) {
            listener.onDisconnectSocket(this);
            disconnect();
            listener.onDisconnectedSocket(this);
        }
    }

    public OkHttpConnection connect() {
        if (webSocket != null && isOpened) {
            throw new IllegalStateException("Another socket is connected");
        }

        listener.onConnectSocket();

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
