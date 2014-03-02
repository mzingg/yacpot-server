package com.yacpot.server.rest;

import com.yacpot.server.model.SecurityRole;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ResourceMapping {
  String pattern();

  String[] requireRoles() default StringUtils.EMPTY;

  Operation[] supportsOperations() default Operation.READ;
}
