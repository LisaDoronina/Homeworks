package com.mipt.elizavetadoronina.annotationsClasses.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Range {
  long min() default Long.MIN_VALUE;
  long max() default Long.MAX_VALUE;
  String message();
}