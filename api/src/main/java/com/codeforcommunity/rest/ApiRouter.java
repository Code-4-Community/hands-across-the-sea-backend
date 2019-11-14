package com.codeforcommunity.rest;

import com.codeforcommunity.api.INotesProcessor;
import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.dto.MemberReturn;
import com.codeforcommunity.dto.*;

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

import javax.xml.bind.annotation.XmlType;
import java.util.*;

public class ApiRouter {
    private final INotesProcessor notesProcessor;
    private final IAuthProcessor authProcessor;

    public ApiRouter(INotesProcessor notesProcessor, IAuthProcessor authProcessor) {
        this.notesProcessor = notesProcessor;
        this.authProcessor = authProcessor;
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
        registerNewUser(router);
        registerLogoutUser(router);

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
        Route loginUserRoute = router.route(HttpMethod.POST, "/api/v1/user/getNewUserSession"); //todo add these paths to constants
        loginUserRoute.handler(this::handlePostUserLoginRoute);
    }

    private void registerRefreshUser(Router router) {
        Route refreshUserRoute = router.route(HttpMethod.POST, "/api/v1/user/getNewUserSession/getNewAccessToken");
        refreshUserRoute.handler(this::handlePostRefreshUser);
    }

    private void registerNewUser(Router router) {
        Route newUserRoute = router.route(HttpMethod.POST, "/api/v1/user/signup");
        newUserRoute.handler(this::handlePostNewUser);
    }

    private void registerLogoutUser(Router router) {
        Route logoutUserRoute = router.route(HttpMethod.DELETE, "/api/v1/user/login");
        logoutUserRoute.handler(this::handleDeleteLogoutUser);
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
                notes = Collections.singletonList(notesProcessor.getANote(noteId));
            } else {
                notes = notesProcessor.getAllNotes();
            }
            NotesResponse response = new NotesResponse(HttpConstants.okMessage, notes);
            end(ctx.response(), HttpConstants.ok_code, JsonObject.mapFrom(response).encode());
        } catch (Exception e) {
            endServerError(ctx.response(), e);
        }
    }

    private void handlePostNoteRoute(RoutingContext ctx) {

        if (authorized(ctx.request())) {
            endUnauthorized(ctx.response());
            return;
        }

        NotesRequest requestBody;

        try {
            requestBody = ctx.getBodyAsJson().mapTo(NotesRequest.class);
            assert requestBody != null;
        } catch (Exception e) {
            endClientError(ctx.response());
            return;
        }

        try {
            List<FullNote> notes = notesProcessor.createNotes(requestBody.getNotes());
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
            FullNote updatedNote = notesProcessor.updateNote(noteId, requestBody.getNote());
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
            notesProcessor.deleteNote(noteId);
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
            List<MemberReturn> members = notesProcessor.getAllMembers();
            String memberJson = JsonObject.mapFrom(members).encode();
            end(ctx.response(), HttpConstants.ok_code, memberJson);
        } catch (Exception e) {
            endServerError(ctx.response(), e);
        }
    }

    private boolean authorized(HttpServerRequest req) {

        String accessToken;

        try {
            accessToken = req.getHeader("access_token");
            return authProcessor.authenticateUser(accessToken);
        } catch (Exception e) {
            return false;
        }
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
                .putHeader(HttpConstants.contentType, HttpConstants.applicationJson)
                .putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("Access-Control-Allow-Methods", "DELETE, POST, GET, OPTIONS")
                .putHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
        if (jsonBody == null || jsonBody.equals("")) {
            finalResponse.end();
        } else {
            finalResponse.end(jsonBody);
        }
    }

    private void handlePostUserLoginRoute(RoutingContext ctx) {

        try {
            String reqBody = ctx.getBodyAsString();

            String[] tokens = authProcessor.getNewUserSession(reqBody);

            String jsonReturn = JsonObject.mapFrom(new HashMap<String, String>() {{
                put("access_token", tokens[0]); //todo make sure these map correctly
                put("refresh_token", tokens[1]);
            }}).encode();

            end(ctx.response(), HttpConstants.ok_code, jsonReturn);
        } catch (Exception e) {
            endUnauthorized(ctx.response());
        }
    }

    private void handlePostRefreshUser(RoutingContext ctx) { //todo update contract to handle client errors as well as un authed errors as well as server errros

        try {
            Map<String, String> bodyMap = new ObjectMapper().readValue(ctx.getBodyAsString(), HashMap.class);
            String body = bodyMap.get("refresh_token");

            end(ctx.response(), HttpConstants.created_code, JsonObject.mapFrom(new HashMap<String, String>() {{ //todo refactor names in auth class
                put("access_token", authProcessor.getNewAccessToken(body)); //todo make sure these map correctly
            }}).encode()); //todo refactor to no use exceptions as flow control

        } catch (Exception e) {
            endUnauthorized(ctx.response());
        }
    }

    private void handleDeleteLogoutUser(RoutingContext ctx) {

        String body;
        try {
            Map<String, String> bodyMap = new ObjectMapper().readValue(ctx.getBodyAsString(), HashMap.class);
            body = bodyMap.get("refresh_token");
        } catch (Exception e) {
            endClientError(ctx.response());
            return;
        }

        if (authProcessor.invalidateUserSession(body)) {
            end(ctx.response(), 204);
        } else {
            endUnauthorized(ctx.response());
        }
    }

    private void handlePostNewUser(RoutingContext ctx) {

        String username;
        String email;
        String password;
        String firstName;
        String lastName;
        Map<String, String> bodyMap;
        try {
            bodyMap = new ObjectMapper().readValue(ctx.getBodyAsString(), HashMap.class);
            username = bodyMap.get("username");
            email = bodyMap.get("email");
            password = bodyMap.get("password");
            firstName = bodyMap.get("first_name");
            lastName = bodyMap.get("last_name");
            assert username != null && email != null && password != null && firstName != null && lastName != null;
        } catch (Exception e) {
            endClientError(ctx.response());
            return;
        }

        if(authProcessor.newUser(username, email, password, firstName, lastName)) {
            end(ctx.response(), HttpConstants.created_code);
        } else {
            endUnauthorized(ctx.response()); //todo change status codes to match the contract
        }
    }
}
