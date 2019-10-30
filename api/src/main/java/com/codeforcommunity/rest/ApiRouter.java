package com.codeforcommunity.rest;

import com.codeforcommunity.JacksonMapper;
import com.codeforcommunity.api.IProcessor;
import com.codeforcommunity.dto.MemberReturn;
import com.codeforcommunity.dto.NoteReturn;
import com.codeforcommunity.dto.NotesResponse;
import com.codeforcommunity.validation.RequestValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    registerGetNoteRoute(router);
    registerPostNoteRoute(router);
    registerPutNoteRoute(router);
    registerDeleteNoteRoute(router);


    return router;
  }

  private void registerGetNoteRoute(Router router) {
    Route getNoteRoute = router.route(HttpMethod.GET, "/api/note");
    getNoteRoute.handler(this::handleGetNoteRoute);
  }

  private void registerPostNoteRoute(Router router) {
    Route postNoteRoute = router.route(HttpMethod.POST, "/api/note");
    postNoteRoute.handler(this::handleGetMemberRoute);
  }

  private void registerPutNoteRoute(Router router) {
    Route putNoteRoute = router.route(HttpMethod.PUT, "/api/note/:note_id");
    putNoteRoute.handler(this::handleGetMemberRoute);
  }

  private void registerDeleteNoteRoute(Router router) {
    Route deleteNoteRoute = router.route(HttpMethod.DELETE, "/api/note:note_id");
    deleteNoteRoute.handler(this::handleGetMemberRoute);
  }

  private void handleGetNoteRoute(RoutingContext ctx) {
    HttpServerRequest request = ctx.request();
    Optional<String> optionalNoteId = Optional.ofNullable(request.getParam("note_id"));
    List<NoteReturn> notes;
    if (optionalNoteId.isPresent()) {
      int noteId = Integer.parseInt(optionalNoteId.get()); //TODO: Check if exception
      notes = Collections.singletonList(processor.getANote(noteId));
    } else {
      notes = processor.getAllNotes();
    }

    NotesResponse response = new NotesResponse("OK", notes);
    try {
      ctx.response().setStatusCode(200)
          .putHeader("content-type", "application/json")
          .end(JacksonMapper.getMapper().writeValueAsString(response));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      ctx.response().setStatusCode(500).end(e.getMessage());
    }
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
