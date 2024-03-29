package com.app.aptannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动设置Bundle
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface AutoBundle{}
