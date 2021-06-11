package com.codeforcommunity.enums;

public enum Grade {
  FIRST_GRADE("First grade"),
  SECOND_GRADE("Second grade"),
  THIRD_GRADE("Third grade"),
  FOURTH_GRADE("Fourth grade"),
  FIFTH_GRADE("Fifth grade"),
  SIXTH_GRADE("Sixth grade"),
  FORM_ONE("Form one"),
  FORM_TWO("Form two"),
  FORM_THREE("Form three"),
  FORM_FOUR("Form four");

  private String name;

  Grade(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static Grade from(String name) {
    for (Grade gradeType : Grade.values()) {
      if (gradeType.name.equals(name)) {
        return gradeType;
      }
    }
    throw new IllegalArgumentException(
        String.format("Given name `%s` doesn't correspond to any `Grade`", name));
  }
}
