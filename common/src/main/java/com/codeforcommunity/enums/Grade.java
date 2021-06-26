package com.codeforcommunity.enums;

public enum Grade {
  KINDERGARTEN("KINDERGARTEN"),
  FIRST_GRADE("FIRST_GRADE"),
  SECOND_GRADE("SECOND_GRADE"),
  THIRD_GRADE("THIRD_GRADE"),
  FOURTH_GRADE("FOURTH_GRADE"),
  FIFTH_GRADE("FIFTH_GRADE"),
  SIXTH_GRADE("SIXTH_GRADE"),
  FORM_ONE("FORM_ONE"),
  FORM_TWO("FORM_TWO"),
  FORM_THREE("FORM_THREE"),
  FORM_FOUR("FORM_FOUR"),
  FORM_FIVE("FORM_FIVE");

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
