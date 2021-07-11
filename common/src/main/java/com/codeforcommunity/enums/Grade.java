package com.codeforcommunity.enums;

public enum Grade {
  KINDERGARTEN("kindergarten"),
  FIRST_GRADE("first_grade"),
  SECOND_GRADE("second_grade"),
  THIRD_GRADE("third_grade"),
  FOURTH_GRADE("fourth_grade"),
  FIFTH_GRADE("fifth_grade"),
  SIXTH_GRADE("sixth_grade"),
  FORM_ONE("form_one"),
  FORM_TWO("form_two"),
  FORM_THREE("form_three"),
  FORM_FOUR("form_four"),
  FORM_FIVE("form_five");

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

  public static String[] toStringArray(Grade[] gradesAttended) {
    String[] stringGradesAttended = new String[gradesAttended.length];

    for (int i = 0; i < gradesAttended.length; i++) {
      stringGradesAttended[i] = gradesAttended[i].toString();
    }

    return stringGradesAttended;
  }

  public static Grade[] from(String[] stringGradesAttended) {
    Grade[] gradesAttended = new Grade[stringGradesAttended.length];
    for (int i = 0; i < stringGradesAttended.length; i++) {
      gradesAttended[i] = Grade.from(stringGradesAttended[i]);
    }
    return gradesAttended;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
