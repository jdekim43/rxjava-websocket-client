package kr.jadekim.rxjava.websocket.processor;

import kr.jadekim.rxjava.websocket.annotation.Channel;
import kr.jadekim.rxjava.websocket.filter.ChannelFilter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public class Subscription {

    private int channelId;
    private int streamId;
    private Class clientClass;
    private WebSocketClientProxy proxy;
    private String channel;
    private Map<String, Object> parameterMap;
    private Type returnType;
    private Class rawType;
    private Type responseType;
    private Class<? extends ChannelFilter> filterClass;
    private String onStartMethod;
    private String onStopMethod;

    Subscription(Class clientClass, WebSocketClientProxy proxy, Channel channelInfo, Map<String, Object> parameterMap, Type returnType) {
        this.clientClass = clientClass;
        this.proxy = proxy;
        this.channel = channelInfo.value();
        this.parameterMap = parameterMap;
        this.returnType = returnType;
        this.rawType = Utils.getRawType(returnType);
        this.responseType = getResponseType(returnType);
        this.filterClass = channelInfo.filter();
        this.onStartMethod = channelInfo.onStart();
        this.onStopMethod = channelInfo.onStop();

        this.channelId = Utils.hashCode(this.channel, this.returnType);
        this.streamId = Utils.hashCode(this.channel, this.returnType, this.parameterMap, filterClass);
    }

    public int getChannelId() {
        return channelId;
    }

    public int getStreamId() {
        return streamId;
    }

    public String getChannel() {
        return channel;
    }

    public Map<String, Object> getParameterMap() {
        return Collections.unmodifiableMap(parameterMap);
    }

    public Type getReturnType() {
        return returnType;
    }

    public Class getRawType() {
        return rawType;
    }

    public Type getResponseType() {
        return responseType;
    }

    public Class<? extends ChannelFilter> getFilterClass() {
        return filterClass;
    }

    public String getOnStartMethod() {
        return onStartMethod;
    }

    public String getOnStopMethod() {
        return onStopMethod;
    }

    void runOnStart() {
        if (onStartMethod == null || "".equals(onStartMethod)) {
            return;
        }

        runClientMethod(onStartMethod);
    }

    void runOnStop() {
        if (onStopMethod == null || "".equals(onStopMethod)) {
            return;
        }

        runClientMethod(onStopMethod);
    }

    ChannelFilter createFilter() {
        try {
            return filterClass.getConstructor(String.class, Map.class).newInstance(channel, parameterMap);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Not Found Accessible Constructor in " + filterClass.getName());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Not Found Accessible Constructor in " + filterClass.getName());
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Not Found Accessible Constructor in " + filterClass.getName());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Not Found Accessible Constructor in " + filterClass.getName());
        }
    }

    private Type getResponseType(Type returnType) {
        if (returnType instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) returnType).getActualTypeArguments();

            if (typeArguments.length > 0) {
                return typeArguments[0];
            }
        }

        return String.class;
    }

    private void runClientMethod(String sentence) {
        proxy.runMethod(clientClass, sentence, parameterMap);
    }
}
