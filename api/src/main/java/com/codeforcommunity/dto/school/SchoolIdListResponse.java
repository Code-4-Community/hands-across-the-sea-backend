package com.codeforcommunity.dto.school;

import java.util.List;
import java.util.Set;

public class SchoolIdListResponse {
  private Set<Integer> ids;

  public SchoolIdListResponse(Set<Integer> ids) {
    if (ids == null) {
      throw new IllegalArgumentException("Given `null` list of school id's");
    }
    this.ids = ids;
  }

  public Set<Integer> getIds() {
    return ids;
  }

  public void setIds(Set<Integer> ids) {
    this.ids = ids;
  }
}
