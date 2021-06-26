package com.codeforcommunity.enums;

public enum ReadyTimeline {
  UPCOMING_SCHOOL_YEAR("UPCOMING_SCHOOL_YEAR"),
  YEAR_AFTER_NEXT("YEAR_AFTER_NEXT"),
  MORE_THAN_TWO_YEARS("MORE_THAN_TWO_YEARS");

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
