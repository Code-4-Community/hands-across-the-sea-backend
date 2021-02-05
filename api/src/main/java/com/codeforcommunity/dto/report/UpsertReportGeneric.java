package com.codeforcommunity.dto.report;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class UpsertReportGeneric extends ApiDto {

  private Integer numberOfChildren;
  private Integer numberOfBooks;
  private Integer mostRecentShipmentYear;

  public Integer getNumberOfChildren() {
    return numberOfChildren;
  }

  public void setNumberOfChildren(Integer numberOfChildren) {
    this.numberOfChildren = numberOfChildren;
  }

  public Integer getNumberOfBooks() {
    return numberOfBooks;
  }

  public void setNumberOfBooks(Integer numberOfBooks) {
    this.numberOfBooks = numberOfBooks;
  }

  public Integer getMostRecentShipmentYear() {
    return mostRecentShipmentYear;
  }

  public void setMostRecentShipmentYear(Integer mostRecentShipmentYear) {
    this.mostRecentShipmentYear = mostRecentShipmentYear;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    List<String> fields = new ArrayList<String>();
    if (numberOfChildren == null) {
      fields.add("numberOfChildren");
    }
    if (numberOfBooks == null) {
      fields.add("numberOfBooks");
    }
    if (mostRecentShipmentYear == null) {
      fields.add("mostRecentShipmentYear");
    }
    return fields;
  }
}
