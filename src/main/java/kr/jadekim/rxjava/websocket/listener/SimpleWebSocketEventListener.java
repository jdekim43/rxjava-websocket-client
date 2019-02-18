package kr.jadekim.rxjava.websocket.listener;

import kr.jadekim.rxjava.websocket.filter.ChannelFilter;
import kr.jadekim.rxjava.websocket.connection.Connection;

import java.util.Map;

public class SimpleWebSocketEventListener implements WebSocketEventListener {

    @Override
    public void onConnectSocket() {
        //do nothing
    }

    @Override
    public void onConnectedSocket(Connection connection) {
        //do nothing
    }

    @Override
    public void onErrorSocket(Throwable e) {
        //do nothing
    }

    @Override
    public void onDisconnectSocket(Connection connection) {
        //do nothing
    }

    @Override
    public void onDisconnectedSocket(Connection connection) {
        //do nothing
    }

    @Override
    public void onStartInboundStream() {
        //do nothing
    }

    @Override
    public void onErrorInboundStream(Throwable e) {
        //do nothing
    }

    @Override
    public void onStopInboundStream() {
        //do nothing
    }

    @Override
    public void onStartChannelStream(String channel) {
        //do nothing
    }

    @Override
    public void onErrorChannelStream(String channel, Throwable e) {
        //do nothing
    }

    @Override
    public void onStopChannelStream(String channel) {
        //do nothing
    }

    @Override
    public void onStartFilteredStream(String channel, ChannelFilter filter) {
        //do nothing
    }

    @Override
    public void onErrorFilteredStream(String channel, ChannelFilter filter, Throwable e) {
        //do nothing
    }

    @Override
    public void onStopFilteredStream(String channel, ChannelFilter filter) {
        //do nothing
    }

    @Override
    public void onSubscribeFilteredStream(String channel, ChannelFilter filter) {
        //do nothing
    }

    @Override
    public void onUnsubscribeFilteredStream(String channel, ChannelFilter filter) {
        //do nothing
    }

    @Override
    public void onAddMessageQueue(String messageType, Map<String, Object> parameterMap) {
        //do nothing
    }

    @Override
    public void onSendMessage(String messageType, Map<String, Object> parameterMap, String message) {
        //do nothing
    }

    @Override
    public void onCompleteSendMessage(String messageType, Map<String, Object> parameterMap, String message) {
        //do nothing
    }

    @Override
    public void onErrorSendMessage(String messageType, Map<String, Object> parameterMap, String message) {
        //do nothing
    }
}
