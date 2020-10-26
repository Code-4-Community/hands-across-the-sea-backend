package com.codeforcommunity.rest;

public interface HttpConstants {
  int client_error_code = 400;
  int server_error_code = 500;
  int unauthorized_code = 401;
  int created_code = 201;
  int ok_code = 200;
  String contentType = "Content-Type";
  String applicationJson = "application/json";
  String unauthorizedMessage = "unauthorized access for request resources";
  String clientErrorMessage =
      "unable to handle request please check request format and consult documentation";
  String noteIdParam = "note_id";
  String okMessage = "OK";
  String noteRoute = "/api/note";
}
