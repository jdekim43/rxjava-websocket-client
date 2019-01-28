package kr.jadekim.rxjava.websocket.processor;

import java.lang.reflect.*;
import java.util.Arrays;

class Utils {

    static int hashCode(Object... objects) {
        return Arrays.hashCode(objects);
    }

    static Class<?> getRawType(Type type) {
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
