package com.codeforcommunity.enums;

public enum ApprenticeTitle {
  LIBRARIAN("librarian"),
  SCHOOL_SECRETARY("school_secretary"),
  CLASSROOM_TEACHER("classroom_teacher"),
  APPRENTICE("apprentice"),
  PCV("PCV"),
  OTHER("OTHER");

  private String name;

  ApprenticeTitle(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static ApprenticeTitle from(String name) {
    for (ApprenticeTitle title : ApprenticeTitle.values()) {
      if (title.name.equals(name)) {
        return title;
      }
    }
    throw new IllegalArgumentException(
        String.format("Given name `%s` doesn't correspond to any `ApprenticeTitle`", name));
  }
}
