package com.codeforcommunity.dto;

public class ClientErrorResponse implements IDTO {

  private String status = "BAD REQUEST";
  private String reason;

  public ClientErrorResponse(String reason) {
    this.reason = reason;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
