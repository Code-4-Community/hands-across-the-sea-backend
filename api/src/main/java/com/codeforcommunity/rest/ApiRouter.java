package com.codeforcommunity.rest;

import com.codeforcommunity.api.IProcessor;
import com.codeforcommunity.auth.AuthProcessor;
import com.codeforcommunity.auth.AuthProcessorImpl;
import com.codeforcommunity.auth.exceptions.AuthException;
import com.codeforcommunity.dto.MemberReturn;
import com.codeforcommunity.utils.Logger;
import com.codeforcommunity.dto.*;
import com.codeforcommunity.validation.RequestValidator;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.*;

public class ApiRouter {
    private final IProcessor processor;

    //todo replace this field with jwt auth implementation when ready
    private RequestValidator authValidator = req -> req.headers() != null;

    public ApiRouter(IProcessor processor) {
        this.processor = processor;
    }

    public ApiRouter(IProcessor processor, RequestValidator validator) {
        this(processor);
        this.authValidator = validator;
    }

    /**
     * Initialize a router and register all route handlers on it.
     */
    public Router initializeRouter(Vertx vertx) {

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create(false));

        registerGetNoteRoute(router);
        registerPostNoteRoute(router);
        registerPutNoteRoute(router);
        registerDeleteNoteRoute(router);
        registerLoginUser(router);
        registerRefreshUser(router);


        return router;
    }

    private void registerGetNoteRoute(Router router) {
        Route getNoteRoute = router.route(HttpMethod.GET, HttpConstants.noteRoute);
        getNoteRoute.handler(this::handleGetNoteRoute);
    }

    private void registerPostNoteRoute(Router router) {
        Route postNoteRoute = router.route(HttpMethod.POST, HttpConstants.noteRoute);
        postNoteRoute.handler(this::handlePostNoteRoute);
    }

    private void registerPutNoteRoute(Router router) {
        Route putNoteRoute = router.route(HttpMethod.PUT, HttpConstants.noteRoute + "/:" + HttpConstants.noteIdParam);
        putNoteRoute.handler(this::handlePutNoteRoute);
    }

    private void registerDeleteNoteRoute(Router router) {
        Route deleteNoteRoute = router.route(HttpMethod.DELETE, HttpConstants.noteRoute + "/:" + HttpConstants.noteIdParam);
        deleteNoteRoute.handler(this::handleDeleteNoteRoute);
    }

    private void registerLoginUser(Router router) {
        Route loginUserRoute = router.route(HttpMethod.POST, "/api/v1/user/login");
        loginUserRoute.handler(this::handlePostUserLoginRoute);
    }

    private void registerRefreshUser(Router router) {
        Route refreshUserRoute = router.route(HttpMethod.POST, "/api/v1/user/login/refresh");
        refreshUserRoute.handler(this::handlePostRefreshUser);
    }


    private void handleGetNoteRoute(RoutingContext ctx) {

        if (authorized(ctx.request())) {
            endUnauthorized(ctx.response());
            return;
        }

        Optional<String> optionalNoteId;

        try {
            optionalNoteId = Optional.ofNullable(ctx.request().getParam(HttpConstants.noteIdParam));
        } catch (Exception e) {
            endClientError(ctx.response());
            return;
        }

        try {
            List<FullNote> notes;
            if (optionalNoteId.isPresent()) {
                int noteId = Integer.parseInt(optionalNoteId.get());
                notes = Collections.singletonList(processor.getANote(noteId));
            } else {
                notes = processor.getAllNotes();
            }
            NotesResponse response = new NotesResponse(HttpConstants.okMessage, notes);
            end(ctx.response(), HttpConstants.ok_code, JsonObject.mapFrom(response).encode());
        } catch (Exception e) {
            endServerError(ctx.response(), e);
        }
    }

    private void handlePostNoteRoute(RoutingContext ctx) {

        System.out.println("handling post note");

	if (authorized(ctx.request())) {
            endUnauthorized(ctx.response());
            return;
        }

        NotesRequest requestBody;

        try {
            requestBody = ctx.getBodyAsJson().mapTo(NotesRequest.class);
            System.out.println(requestBody.getNotes().get(0).getContent());
            System.out.println(requestBody.getNotes().get(0).getTitle());
            assert requestBody != null;
        } catch (Exception e) {
            System.out.println("client error bb");
            endClientError(ctx.response());
            return;
        }

        try {
            List<FullNote> notes = processor.createNotes(requestBody.getNotes());
            NotesResponse response = new NotesResponse(HttpConstants.okMessage, notes);
            end(ctx.response(), HttpConstants.created_code, JsonObject.mapFrom(response).encode());
        } catch (Exception e) {
            this.endServerError(ctx.response(), e);
        }
    }

    private void handlePutNoteRoute(RoutingContext ctx) {

        if (authorized(ctx.request())) {
            endUnauthorized(ctx.response());
            return;
        }

        NoteRequest requestBody;

        try {
            requestBody = ctx.getBodyAsJson().mapTo(NoteRequest.class);
        } catch (Exception e) {
            endClientError(ctx.response());
            return;
        }

        try {
            HttpServerRequest request = ctx.request();
            int noteId = Integer.parseInt(request.getParam(HttpConstants.noteIdParam));
            FullNote updatedNote = processor.updateNote(noteId, requestBody.getNote());
            NoteResponse response = new NoteResponse(HttpConstants.okMessage, updatedNote);
            end(ctx.response(), HttpConstants.ok_code, JsonObject.mapFrom(response).encode());
        } catch (Exception e) {
            endServerError(ctx.response(), e);
        }
    }

    private void handleDeleteNoteRoute(RoutingContext ctx) {

        if (authorized(ctx.request())) {
            endUnauthorized(ctx.response());
            return;
        }

        int noteId;

        try {
            noteId = Integer.parseInt(ctx.request().getParam(HttpConstants.noteIdParam));
        } catch (Exception e) {
            endClientError(ctx.response());
            return;
        }

        try {
            processor.deleteNote(noteId);
            end(ctx.response(), HttpConstants.ok_code);
        } catch (Exception e) {
            endServerError(ctx.response(), e);
        }
    }

    /**
     * Add a handler for getting all members.
     */
    private void handleGetMemberRoute(RoutingContext ctx) {

        if (authorized(ctx.request())) {
            endUnauthorized(ctx.response());
            return;
        }

        try {
            List<MemberReturn> members = processor.getAllMembers();
            String memberJson = JsonObject.mapFrom(members).encode();
            end(ctx.response(), HttpConstants.ok_code, memberJson);
        } catch (Exception e) {
            endServerError(ctx.response(), e);
        }
    }

    private boolean authorized(HttpServerRequest req) {
        return (!authValidator.validateRequest(req));
    }

    private void endClientError(HttpServerResponse resp) {
        String errorResponse = JsonObject.mapFrom(new ClientErrorResponse(HttpConstants.clientErrorMessage)).encode();
        System.out.println(errorResponse);
        end(resp, HttpConstants.client_error_code, errorResponse);
    }

    private void endUnauthorized(HttpServerResponse resp) { //todo remove repeat code
        String errorResponse = JsonObject.mapFrom(new ClientErrorResponse(HttpConstants.unauthorizedMessage)).encode();
        end(resp, HttpConstants.unauthorized_code, errorResponse);
    }

    private void endServerError(HttpServerResponse resp, Exception e) {
        e.printStackTrace();
        String errorResponse = JsonObject.mapFrom(new ServerErrorResponse(e.getMessage())).encode();
        end(resp, HttpConstants.server_error_code, errorResponse);
    }

    private void end(HttpServerResponse response, int statusCode) {
        end(response, statusCode, null);
    }

    private void end(HttpServerResponse response, int statusCode, String jsonBody) {

        HttpServerResponse finalResponse = response.setStatusCode(statusCode)
                .putHeader(HttpConstants.contentType, HttpConstants.applicationJson);
        if (jsonBody == null || jsonBody.equals("")) {
            finalResponse.end();
        } else {
            finalResponse.end(jsonBody);
        }
    }

  private void handlePostUserLoginRoute(RoutingContext ctx) { //todo move above the helper methods

        //todo logger methods

      AuthProcessor auth;
    try {
         auth = new AuthProcessorImpl(); //todo figure this out
    } catch (Exception e) {
        Logger.log("error");
        Logger.log(e.getMessage());
        return;
    }

    Logger.log(ctx.getBodyAsString());
    String reqBody = ctx.getBodyAsString();

      try {
          String jsonReturn = JsonObject.mapFrom(new HashMap<String, String>() {{ //todo refactor names in auth class
              put("access_token", auth.login(reqBody)[0]); //todo make sure these map correctly
              put("refresh_token", auth.login(reqBody)[1]); //todo clean this up
          }}).encode();
          Logger.log("json: " + jsonReturn);
          ctx.response().setStatusCode(200).putHeader("Access-Control-Allow-Origin", "*")
                  .putHeader("Access-Control-Allow-Methods", "DELETE, POST, GET, OPTIONS")
                  .putHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With")
                  .end(jsonReturn);
      } catch (AuthException ae) {
          endUnauthorized(ctx.response());
      } catch (Exception e) { }


  }

  private void handlePostRefreshUser(RoutingContext ctx) {

      AuthProcessor auth;
      try {
          auth = new AuthProcessorImpl(); //todo figure this out
      } catch (Exception e) {
          Logger.log("error");
          Logger.log(e.getMessage());
          return;
      }

      try {

          Map<String, String> bodyMap = new ObjectMapper().readValue(ctx.getBodyAsString(), HashMap.class);
          String body = bodyMap.get("refresh_token");

          end(ctx.response(), HttpConstants.created_code, JsonObject.mapFrom(new HashMap<String, String>() {{ //todo refactor names in auth class
              put("access_token", auth.refresh(body)); //todo make sure these map correctly
          }}).encode()); //todo refactor to no use exceptions as flow control

      } catch (Exception e) {
          endUnauthorized(ctx.response());

      }
  }
}
