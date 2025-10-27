package com.mipt.elizavetadoronina.annotationsClasses.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Email {
  String message();
}