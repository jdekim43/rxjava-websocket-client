package kr.jadekim.rxjava.websocket.annotation;

import java.lang.annotation.*;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    String name();

    String value();
}
