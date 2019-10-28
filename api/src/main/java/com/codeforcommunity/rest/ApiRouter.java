package com.codeforcommunity.rest;

import com.codeforcommunity.JacksonMapper;
import com.codeforcommunity.api.IProcessor;
import com.codeforcommunity.dto.MemberReturn;
import com.codeforcommunity.validation.RequestValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class ApiRouter {
  private final IProcessor processor;

  private static int client_error = 400;
  private static int server_error = 500;
  private static int ok = 200;
  // anonymous default validator implementation checks if incoming requests contain header
  private RequestValidator getMemberRequestValidator = req -> req.headers() != null;



  public ApiRouter(IProcessor processor) {
    this.processor = processor;
  }

  public ApiRouter(IProcessor processor, RequestValidator validator) {
    this(processor);
    this.getMemberRequestValidator = validator;
  }

  /**
   * Initialize a router and register all route handlers on it.
   */
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    Route getMemberRoute = router.route().path("/api/v1/members");
    getMemberRoute.handler(this::handleGetMemberRoute);

    return router;
  }

  /**
   * Add a handler for getting all members.
   */
  private void handleGetMemberRoute(RoutingContext ctx) {

    HttpServerResponse response = ctx.response();
    response.putHeader("content-type", "application/json");

    if(!getMemberRequestValidator.validateRequest(ctx.request())) {
      response.setStatusCode(client_error).end("malformed request, please try again");
      return;
    }

    try {
      List<MemberReturn> members = processor.getAllMembers();
      String memberJson = JacksonMapper.getMapper().writeValueAsString(members);
      response.setStatusCode(ok).end(memberJson);
    } catch (Exception e) {
      response.setStatusCode(server_error).end("internal server error");
      return;
    }
  }
}
