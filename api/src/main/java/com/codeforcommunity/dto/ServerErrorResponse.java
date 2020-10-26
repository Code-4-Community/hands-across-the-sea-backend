package com.codeforcommunity.dto;

public class ServerErrorResponse {

  private String status = "SERVER ERROR";
  private String reason;

  public ServerErrorResponse(String reason) {
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
