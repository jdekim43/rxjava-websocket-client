package kr.jadekim.rxjava.websocket.annotation;

import kr.jadekim.rxjava.websocket.httpclient.ConnectionFactory;
import kr.jadekim.rxjava.websocket.httpclient.okhttp.OkHttpConnectionFactory;
import kr.jadekim.rxjava.websocket.inbound.DefaultInboundParser;
import kr.jadekim.rxjava.websocket.inbound.InboundParser;
import kr.jadekim.rxjava.websocket.listener.SimpleWebSocketEventListener;
import kr.jadekim.rxjava.websocket.listener.WebSocketEventListener;
import kr.jadekim.rxjava.websocket.outbound.DefaultOutboundSerializer;
import kr.jadekim.rxjava.websocket.outbound.OutboundSerializer;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebSocketClient {

    String url();
    Class<? extends ConnectionFactory> connectionFactory() default OkHttpConnectionFactory.class;
    Class<? extends InboundParser> parser() default DefaultInboundParser.class;
    Class<? extends OutboundSerializer> serializer() default DefaultOutboundSerializer.class;
    Class<? extends WebSocketEventListener> listener() default SimpleWebSocketEventListener.class;
    boolean isLazy() default false;
    boolean isErrorPropagation() default true;
}
