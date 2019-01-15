package kr.jadekim.rxjava.websocket.processor;

import io.reactivex.*;
import kr.jadekim.rxjava.websocket.annotation.Channel;
import kr.jadekim.rxjava.websocket.annotation.Message;
import kr.jadekim.rxjava.websocket.annotation.Param;
import kr.jadekim.rxjava.websocket.filter.ChannelFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

public final class WebSocketClientProxy implements InvocationHandler {

    private WebSocket io;

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
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        Type returnType = method.getGenericReturnType();
        Class rawType = getRawType(returnType);
        Type responseType = Object.class;

        if (returnType instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) returnType).getActualTypeArguments();

            if (typeArguments.length > 0) {
                responseType = typeArguments[0];
            }
        }

        Map<String, Object> parameterMap = getParameterMap(method.getParameterAnnotations(), args);

        if (method.isAnnotationPresent(Channel.class)) {
            return processChannel(method.getAnnotation(Channel.class), parameterMap, rawType, responseType);
        } else if (method.isAnnotationPresent(Message.class)) {
            Completable sendCompletable = io.sendMessage(method.getAnnotation(Message.class).value(), parameterMap);

            if (rawType == Completable.class) {
                return sendCompletable;
            }

            sendCompletable.blockingAwait();
        } else if ("disconnect".equals(method.getName())) {
            io.disconnect();
        }

        return null;
    }

    private Object processChannel(Channel channelInfo, Map<String, Object> parameterMap, Class rawType, Type responseType) {
        String channel = channelInfo.value();
        ChannelFilter filter = createChannelFilter(channel, channelInfo.filter(), parameterMap);

        ChannelStream stream = io.getStream(channel, responseType, filter);

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

    private Map<String, Object> getParameterMap(Annotation[][] parameterAnnotations, Object[] args) {
        Map<String, Object> parameterMap = new HashMap<String, Object>();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            parameterMap.put(Integer.toString(i), args[i]);

            Annotation[] annotations = parameterAnnotations[i];
            if (annotations == null || annotations.length == 0) {
                continue;
            }

            for (Annotation annotation : annotations) {
                if (annotation instanceof Param) {
                    parameterMap.put(((Param) annotation).value(), args[i]);
                    break;
                }
            }
        }

        return parameterMap;
    }

    private ChannelFilter createChannelFilter(String channel, Class<? extends ChannelFilter> filterClass, Map<String, Object> parameterMap) {
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

    private Class<?> getRawType(Type type) {
        if (type == null) {
            return null;
        }

        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();

            if (!(rawType instanceof Class)) {
                throw new IllegalArgumentException();
            }

            return (Class<?>) rawType;
        }

        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();
        }

        if (type instanceof TypeVariable) {
            return Object.class;
        }

        if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);
        }

        throw new IllegalArgumentException();
    }
}