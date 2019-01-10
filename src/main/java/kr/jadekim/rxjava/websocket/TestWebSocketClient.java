package kr.jadekim.rxjava.websocket;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import kr.jadekim.rxjava.websocket.annotation.Channel;
import kr.jadekim.rxjava.websocket.annotation.Message;
import kr.jadekim.rxjava.websocket.annotation.Param;
import kr.jadekim.rxjava.websocket.annotation.WebSocketClient;

@WebSocketClient(url = "ws://localhost/ws")
public interface TestWebSocketClient {

    @Channel
    Flowable<String> subscribeAll();

    @Channel(value = "ticker", onSubscribe = "requestTicker(tradingPair)")
    Flowable<Object> subscribeTicker(@Param("tradingPair") String tradingPair);

    @Message
    Completable sendMessage(String message);

    @Message("requestTicker")
    Completable requestTicker(@Param("tradingPair") String tradingPair);
}
