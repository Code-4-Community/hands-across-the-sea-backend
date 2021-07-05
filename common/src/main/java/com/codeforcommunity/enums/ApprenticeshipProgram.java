package com.codeforcommunity.enums;

public enum ApprenticeshipProgram {
  OECS("oecs"),
  NEP("nep"),
  OTHER("OTHER");

  private String name;

  ApprenticeshipProgram(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static ApprenticeshipProgram from(String name) {
    for (ApprenticeshipProgram program : ApprenticeshipProgram.values()) {
      if (program.name.equals(name)) {
        return program;
      }
    }
    throw new IllegalArgumentException(
        String.format("Given name `%s` doesn't correspond to any `ApprenticeshipProgram`", name));
  }
}
