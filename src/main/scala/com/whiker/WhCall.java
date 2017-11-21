package com.whiker;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 方法可调用
 * Created by whiker on 2017/1/1.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface WhCall {
    int order() default 0;
}
