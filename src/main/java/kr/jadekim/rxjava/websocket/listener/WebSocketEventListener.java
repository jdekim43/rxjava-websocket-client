package kr.jadekim.rxjava.websocket.listener;

import kr.jadekim.rxjava.websocket.filter.ChannelFilter;
import kr.jadekim.rxjava.websocket.connection.Connection;

import java.util.Map;

public interface WebSocketEventListener {

    //connection
    void onConnectSocket();

    void onConnectedSocket(Connection connection);

    void onErrorSocket(Throwable e);

    void onDisconnectSocket(Connection connection);

    void onDisconnectedSocket(Connection connection);

    //stream router
    void onStartInboundStream();

    void onErrorInboundStream(Throwable e);

    void onStopInboundStream();

    //channel distributor
    void onStartChannelStream(String channel);

    void onErrorChannelStream(String channel, Throwable e);

    void onStopChannelStream(String channel);

    //channel stream
    void onStartFilteredStream(String channel, ChannelFilter filter);

    void onErrorFilteredStream(String channel, ChannelFilter filter, Throwable e);

    void onStopFilteredStream(String channel, ChannelFilter filter);

    //observable
    void onSubscribeFilteredStream(String channel, ChannelFilter filter);

    void onUnsubscribeFilteredStream(String channel, ChannelFilter filter);

    //message sender
    void onAddMessageQueue(String messageType, Map<String, Object> parameterMap);

    void onSendMessage(String messageType, Map<String, Object> parameterMap, String message);

    void onCompleteSendMessage(String messageType, Map<String, Object> parameterMap, String message);

    void onErrorSendMessage(String messageType, Map<String, Object> parameterMap, String message);
}
