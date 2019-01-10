package kr.jadekim.rxjava.websocket;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import kr.jadekim.rxjava.websocket.annotation.Channel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class WebSocketClientFactory {

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }

                if (method.isAnnotationPresent(Channel.class)) {
                    return Flowable.empty();
                }

                return Completable.complete();
            }
        });
    }
}
