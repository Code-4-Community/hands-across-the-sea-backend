package com.codeforcommunity.enums;

public enum AssignedPersonTitle {
  LIBRARIAN("LIBRARIAN"),
  SCHOOL_SECRETARY("SCHOOL_SECRETARY"),
  CLASSROOM_TEACHER("CLASSROOM_TEACHER"),
  APPRENTICE("APPRENTICE"),
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
