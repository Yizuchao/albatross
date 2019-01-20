package com.yogi.albatross.annotation;

import com.yogi.albatross.constants.common.FixedHeadType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Processor {
    FixedHeadType targetType();
}
