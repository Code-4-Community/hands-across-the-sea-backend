package com.codeforcommunity.rest;

import com.codeforcommunity.api.IProcessor;
import com.codeforcommunity.dto.*;
import com.codeforcommunity.validation.RequestValidator;
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

    private void handleGetNoteRoute(RoutingContext ctx) {

        System.out.println("handling get request");

        if (!authValidator.validateRequest(ctx.request())) {
            unauthorized(ctx.response());
        }

        try {
            HttpServerRequest request = ctx.request();
            Optional<String> optionalNoteId = Optional.ofNullable(request.getParam(HttpConstants.noteIdParam));
            List<FullNote> notes;
            if (optionalNoteId.isPresent()) {
                int noteId = Integer.parseInt(optionalNoteId.get()); //TODO: Check if exception
                notes = Collections.singletonList(processor.getANote(noteId));
            } else {
                notes = processor.getAllNotes();
            }
            NotesResponse response = new NotesResponse(HttpConstants.okMessage, notes);
            end(ctx.response(), HttpConstants.ok_code, JsonObject.mapFrom(response).encode());
        } catch (Exception e) {
            this.clientError(ctx.response(), e);
        }
    }

    private void handlePostNoteRoute(RoutingContext ctx) {

        if (!authValidator.validateRequest(ctx.request())) {
            unauthorized(ctx.response());
        }

        try {
            NotesRequest requestBody = ctx.getBodyAsJson().mapTo(NotesRequest.class); //TODO: Exception handling
            List<FullNote> notes = processor.createNotes(requestBody.getNotes());
            NotesResponse response = new NotesResponse(HttpConstants.okMessage, notes);
            end(ctx.response(), HttpConstants.created_code, JsonObject.mapFrom(response).encode());
        } catch (Exception e) {
            this.clientError(ctx.response(), e);
        }
    }

    private void handlePutNoteRoute(RoutingContext ctx) {

        if (!authValidator.validateRequest(ctx.request())) {
            unauthorized(ctx.response());
        }

        try {
            HttpServerRequest request = ctx.request();
            NoteRequest requestBody = ctx.getBodyAsJson().mapTo(NoteRequest.class);
            int noteId = Integer.parseInt(request.getParam(HttpConstants.noteIdParam));
            FullNote updatedNote = processor.updateNote(noteId, requestBody.getNote());
            NoteResponse response = new NoteResponse(HttpConstants.okMessage, updatedNote);
            end(ctx.response(), HttpConstants.ok_code, JsonObject.mapFrom(response).encode());
        } catch (Exception e) {
            clientError(ctx.response(), e);
        }
    }

    private void handleDeleteNoteRoute(RoutingContext ctx) {

        if (!authValidator.validateRequest(ctx.request())) {
            unauthorized(ctx.response());
        }

        try {
            HttpServerRequest request = ctx.request();
            int noteId = Integer.parseInt(request.getParam(HttpConstants.noteIdParam));
            processor.deleteNote(noteId);
            end(ctx.response(), HttpConstants.ok_code);
        } catch (Exception e) {
            clientError(ctx.response(), e);
        }
    }

    /**
     * Add a handler for getting all members.
     */
    private void handleGetMemberRoute(RoutingContext ctx) {

        if (!authValidator.validateRequest(ctx.request())) {
            unauthorized(ctx.response());
        }

        try {
            List<MemberReturn> members = processor.getAllMembers();
            String memberJson = JsonObject.mapFrom(members).encode();
            end(ctx.response(), HttpConstants.ok_code, memberJson);
        } catch (Exception e) {
            clientError(ctx.response(), e);
        }
    }

    private void clientError(HttpServerResponse resp, Exception e) {
        e.printStackTrace();
        String errorResponse = JsonObject.mapFrom(new ClientErrorResponse(e.getMessage())).encode();
        this.end(resp, HttpConstants.client_error_code, errorResponse);
    }

    private void unauthorized(HttpServerResponse resp) {
        String errorResponse = JsonObject.mapFrom(new ClientErrorResponse(HttpConstants.unauthorizedMessage)).encode();
        end(resp, HttpConstants.unauthorized_code, errorResponse);
    }

    private void end(HttpServerResponse response, int statusCode) {
        this.end(response, statusCode, null);
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
}
