package kr.jadekim.rxjava.websocket.annotation;

import kr.jadekim.rxjava.websocket.filter.BypassFilter;
import kr.jadekim.rxjava.websocket.filter.ChannelFilter;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Channel {

    String value() default "";

    Class<? extends ChannelFilter> filter() default BypassFilter.class;

    String onStart() default "";

    String onStop() default "";
}
