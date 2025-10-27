package com.mipt.elizavetadoronina.annotationsClasses;

import com.mipt.elizavetadoronina.annotationsClasses.annotations.*;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class Validator {
  private static final String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
  private static final Pattern emailPattern = Pattern.compile(emailRegex);

  public static ValidationResult validate(Object obj) {
    ValidationResult result = new ValidationResult();
    if (obj == null) {
      result.addError("Validated object is null");
      return result;
    }

    Class<?> cl = obj.getClass();
    Field[]fields = cl.getDeclaredFields();

    for (Field field : fields) {
      field.setAccessible(true);
      validateField(field, obj, result);
    }

    return result;
  }

  private static void validateField(Field field, Object obj, ValidationResult result) {
    try {
      Object value = field.get(obj);

      if (field.isAnnotationPresent(NotNull.class)) {
        NotNull annotation = field.getAnnotation(NotNull.class);
        if (value == null) {
          result.addError(annotation.message());
          return;
        }
      }

      if (value == null) return;

      if (field.isAnnotationPresent(Size.class)) {
        Size annotation = field.getAnnotation(Size.class);
        if (value instanceof String) {
          String str = (String) value;
          if (str.length() < annotation.min() || str.length() > annotation.max()) {
            result.addError(annotation.message());
          }
        }
      }

      if (field.isAnnotationPresent(Range.class)) {
        Range annotation = field.getAnnotation(Range.class);
        if (value instanceof Number) {
          Number number = (Number) value;
          long longValue = number.longValue();
          if (longValue < annotation.min() || longValue > annotation.max()) {
            result.addError(annotation.message());
          }
        }
      }
      if (field.isAnnotationPresent(Email.class)) {
        Email annotation = field.getAnnotation(Email.class);
        if (value instanceof String) {
          String email = (String) value;
          if (!emailPattern.matcher(email).matches()) {
            result.addError(annotation.message());
          }
        }
      }
    } catch (IllegalAccessException e) {
      result.addError("Cannot access field: " + field.getName());
    }
  }
}