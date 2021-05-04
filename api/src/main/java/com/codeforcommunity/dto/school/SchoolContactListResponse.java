package com.codeforcommunity.dto.school;

import java.util.List;

public class SchoolContactListResponse {

  private Integer count;
  private List<SchoolContact> schoolContacts;

  public SchoolContactListResponse(List<SchoolContact> schoolContacts) {
    if (schoolContacts == null) {
      throw new IllegalArgumentException("Given `null` list of school contacts");
    }

    this.schoolContacts = schoolContacts;
    this.count = schoolContacts.size();
  }

  public List<SchoolContact> getSchoolContacts() {
    return schoolContacts;
  }

  public void setSchoolContacts(List<SchoolContact> schoolContacts) {
    this.schoolContacts = schoolContacts;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
