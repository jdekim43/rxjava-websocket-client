package kr.jadekim.rxjava.websocket.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Params {

    Param[] value();
}
