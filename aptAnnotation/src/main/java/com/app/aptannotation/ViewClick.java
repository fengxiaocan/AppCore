package com.app.aptannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 设置View的点击事件
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface ViewClick {
    int[] value();
}
