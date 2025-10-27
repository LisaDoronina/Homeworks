package com.mipt.elizavetadoronina.annotationsClasses.test;

import com.mipt.elizavetadoronina.annotationsClasses.ValidationResult;
import com.mipt.elizavetadoronina.annotationsClasses.Validator;

public class Test {
  public static void main(String[] args) {
    System.out.println("Unit tests");

    System.out.println("Тест успешной валидации:");
    User user = new User("Лиза", "email@gmail.com", 18, "strongPa33wor5");
    ValidationResult result = Validator.validate(user);
    if (result.isValid()) {
      System.out.println("SUCCESS");
    } else {
      result.getErrors().forEach(System.out::println);
    }

    System.out.println("\n2. Тест с ошибками:");
    User invalidUser = new User("А", "invalid-email", 200, "123");
    ValidationResult result2 = Validator.validate(invalidUser);
    System.out.println("Результат: " + (result2.isValid() ? "SUCCESS" : "ERROR"));
    if (!result2.isValid()) {
      System.out.println("Найдено ошибок: " + result2.getErrors().size());
      result2.getErrors().forEach(System.out::println);
    }

    System.out.println("\n3. Тест с null объектом:");
    ValidationResult result3 = Validator.validate(null);
    System.out.println("Результат: " + (result3.isValid() ? "SUCCESS" : "ERROR"));
    if (!result3.isValid()) {
      result3.getErrors().forEach(System.out::println);
    }
  }
}
