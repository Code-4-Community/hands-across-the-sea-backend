package com.codeforcommunity.rest;

import com.codeforcommunity.JacksonMapper;
import com.codeforcommunity.api.IProcessor;
import com.codeforcommunity.auth.AuthProcessor;
import com.codeforcommunity.auth.AuthProcessorImpl;
import com.codeforcommunity.auth.exceptions.AuthException;
import com.codeforcommunity.dto.MemberReturn;
import com.codeforcommunity.utils.Logger;
import com.codeforcommunity.validation.RequestValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
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
  private AuthProcessor auth = new AuthProcessorImpl();

  public ApiRouter(IProcessor processor) throws Exception { //todo handle this exception
    this.processor = processor;
  }

  public ApiRouter(IProcessor processor, RequestValidator validator) throws Exception { //todo handle this exception
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

    Route postLoginRoute = router.route().path("/api/v1/user/login");
    postLoginRoute.handler(this::handlePostUserLoginRoute);

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

  private void handlePostUserLoginRoute(RoutingContext ctx) {

    System.out.print("poop");

    HttpServerRequest req = ctx.request();
    HttpServerResponse res = ctx.response();
    //todo validate

    req.bodyHandler(buffer -> {
      Logger.log("big stuff");
      String bod = new String(buffer.getString(0, buffer.length()));
      Logger.log(bod);
      try {
        res.setStatusCode(ok).putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("Access-Control-Allow-Methods", "DELETE, POST, GET, OPTIONS")
                .putHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With")
                .end(JacksonMapper.getMapper().writeValueAsString(auth.login(bod)));
      } catch (AuthException ae) {
        Logger.log(ae.getMessage());
        res.setStatusCode(client_error).end();
      } catch (Exception e) {
        res.setStatusCode(server_error).end();
      }
    });

  }
}
