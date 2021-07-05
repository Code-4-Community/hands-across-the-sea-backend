package com.codeforcommunity.enums;

public enum AssignedPersonTitle {
  LIBRARIAN("librarian"),
  SCHOOL_SECRETARY("school_secretary"),
  CLASSROOM_TEACHER("classroom_teacher"),
  APPRENTICE("apprentice"),
  PCV("PCV"),
  OTHER("OTHER");

  private String name;

  AssignedPersonTitle(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static AssignedPersonTitle from(String name) {
    for (AssignedPersonTitle title : AssignedPersonTitle.values()) {
      if (title.name.equals(name)) {
        return title;
      }
    }
    throw new IllegalArgumentException(
        String.format("Given name `%s` doesn't correspond to any `AssignedPersonTitle`", name));
  }
}
