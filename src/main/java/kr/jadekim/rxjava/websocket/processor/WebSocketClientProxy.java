package kr.jadekim.rxjava.websocket.processor;

import io.reactivex.*;
import kr.jadekim.rxjava.websocket.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebSocketClientProxy implements InvocationHandler {

    private static final Pattern METHOD_PATTERN = Pattern.compile("\\w+");

    private final WebSocket io;

    private WebSocketClientProxy(WebSocket io) {
        this.io = io;
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(WebSocket io, Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new WebSocketClientProxy(io));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Class clientClass = method.getDeclaringClass();

        if (clientClass == Object.class) {
            return method.invoke(this, args);
        }

        final Type returnType = method.getGenericReturnType();
        final Map<String, Object> parameterMap = getParameterMap(method, args);

        if (method.isAnnotationPresent(Channel.class)) {
            Channel channelInfo = method.getAnnotation(Channel.class);
            return processChannel(new Subscription(clientClass, this, channelInfo, parameterMap, returnType));
        } else if (method.isAnnotationPresent(Message.class)) {
            return processSendMessage(method, parameterMap, Utils.getRawType(returnType));
        } else if ("disconnect".equals(method.getName())) {
            io.disconnect();
        }

        return null;
    }

    void runMethod(Class clientClass, String sentence, Map<String, Object> parameterMap) {
        Matcher matcher = METHOD_PATTERN.matcher(sentence);

        if (!matcher.find()) {
            throw new IllegalArgumentException("올바르지 않은 값입니다. : " + sentence);
        }

        String onStartMethodName = matcher.group();

        for (Method method : clientClass.getMethods()) {
            if (!method.getName().equals(onStartMethodName)) {
                continue;
            }
            List<Object> params = new ArrayList<Object>();

            while(matcher.find()) {
                params.add(parameterMap.get(matcher.group()));
            }

            Object[] args = new Object[params.size()];
            params.toArray(args);

            try {
                Object result = invoke(null, method, args);

                if (method.isAnnotationPresent(Message.class) && result instanceof Completable) {
                    ((Completable) result).subscribe();
                }

                return;
            } catch (Throwable throwable) {
                throw new IllegalStateException("Fail to invoke onStartMethod : " + sentence, throwable);
            }
        }

        throw new IllegalArgumentException("Not Found Method : " + sentence);
    }

    private Object processChannel(Subscription subscription) {
        final ChannelStream stream = io.getStream(subscription);
        final Class rawType = subscription.getRawType();

        if (rawType == Observable.class) {
            return stream.asObservable();
        }

        if (rawType == Flowable.class) {
            return stream.asFlowable();
        }

        if (rawType == Single.class) {
            return stream.asSingle();
        }

        if (rawType == Maybe.class) {
            return stream.asMaybe();
        }

        if (rawType == Completable.class) {
            return stream.asCompletable();
        }

        return null;
    }

    private Object processSendMessage(Method method, Map<String, Object> parameterMap, Class rawType) {
        Completable sendCompletable = io.sendMessage(method.getAnnotation(Message.class).value(), parameterMap);

        if (rawType == Completable.class) {
            return sendCompletable;
        }

        sendCompletable.blockingAwait();

        return null;
    }

    private Map<String, Object> getParameterMap(Method method, Object[] args) {
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        final Map<String, Object> parameterMap = new HashMap<String, Object>();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            parameterMap.put(Integer.toString(i), args[i]);

            Annotation[] annotations = parameterAnnotations[i];
            if (annotations == null || annotations.length == 0) {
                continue;
            }

            for (Annotation annotation : annotations) {
                if (annotation instanceof Name) {
                    parameterMap.put(((Name) annotation).value(), args[i]);
                    break;
                }
            }
        }

        if (method.isAnnotationPresent(Params.class)) {
            Params params = method.getAnnotation(Params.class);

            for (Param param : params.value()) {
                parameterMap.put(param.name(), param.value());
            }
        }

        return parameterMap;
    }
}