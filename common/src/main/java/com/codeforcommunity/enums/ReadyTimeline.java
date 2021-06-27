package com.codeforcommunity.enums;

public enum ReadyTimeline {
  UPCOMING_SCHOOL_YEAR("upcoming_school_year"),
  YEAR_AFTER_NEXT("year_after_next"),
  MORE_THAN_TWO_YEARS("more_than_two_years");

  private String name;

  ReadyTimeline(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static ReadyTimeline from(String name) {
    for (ReadyTimeline readyTime : ReadyTimeline.values()) {
      if (readyTime.name.equals(name)) {
        return readyTime;
      }
    }
    throw new IllegalArgumentException(
        String.format("Given name `%s` doesn't correspond to any `ReadyTimeline`", name));
  }
}
