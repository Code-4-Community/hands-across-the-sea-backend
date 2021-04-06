package com.codeforcommunity.dto.user;


import java.util.List;

public class UserListResponse {

  private int count;
  private List<UserDataResponse> users;

  public UserListResponse(List<UserDataResponse> users) {
    if (users == null) {
      throw new IllegalArgumentException("Given `null` list of users");
    }

    this.users = users;
    this.count = users.size();
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<UserDataResponse> getUsers() {
    return users;
  }

  public void setUsers(List<UserDataResponse> users) {
    this.users = users;
  }
}
