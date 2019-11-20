package com.codeforcommunity.rest;

import com.codeforcommunity.api.INotesProcessor;
import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.dto.MemberReturn;
import com.codeforcommunity.dto.*;

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

    //protected resource
    private void registerGetNoteRoute(Router router) {
        Route getNoteRoute = router.route(HttpMethod.GET, HttpConstants.noteRoute);
        getNoteRoute.handler(this::handleAuthorizeUser).handler(this::handleGetNoteRoute);
    }

    //protected resource
    private void registerPostNoteRoute(Router router) {
        Route postNoteRoute = router.route(HttpMethod.POST, HttpConstants.noteRoute);
        postNoteRoute.handler(this::handleAuthorizeUser).handler(this::handlePostNoteRoute);
    }

    //protected resource
    private void registerPutNoteRoute(Router router) {
        Route putNoteRoute = router.route(HttpMethod.PUT, HttpConstants.noteRoute + "/:" + HttpConstants.noteIdParam);
        putNoteRoute.handler(this::handleAuthorizeUser).handler(this::handlePutNoteRoute);
    }

    //protected resource
    private void registerDeleteNoteRoute(Router router) {
        Route deleteNoteRoute = router.route(HttpMethod.DELETE, HttpConstants.noteRoute + "/:" + HttpConstants.noteIdParam);
        deleteNoteRoute.handler(this::handleAuthorizeUser).handler(this::handleDeleteNoteRoute);
    }

    private void registerLoginUser(Router router) {
        Route loginUserRoute = router.route(HttpMethod.POST, "/api/v1/user/getNewUserSession");
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

    /**
     * A handler to be called as the first handler for any request for a protected resource. If given user is
     * authorization this router will call the next router in which the desired response is handled. If user fails
     * authorization this handler will end the handler with an unauthorized response to the user.
     *
     * @param ctx routing context to handle.
     */
    private void handleAuthorizeUser(RoutingContext ctx) {
        if (authorized(ctx.request())) {
            ctx.next();
        } else {
            endUnauthorized(ctx.response());
        }
    }

    private void handleGetNoteRoute(RoutingContext ctx) {

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

        try {
            List<MemberReturn> members = notesProcessor.getAllMembers();
            String memberJson = JsonObject.mapFrom(members).encode();
            end(ctx.response(), HttpConstants.ok_code, memberJson);
        } catch (Exception e) {
            endServerError(ctx.response(), e);
        }
    }

    private void handlePostUserLoginRoute(RoutingContext ctx) {

        try {
            JsonObject reqBody = ctx.getBodyAsJson();

            IsUserRequest userRequest = new IsUserRequest() {{
                setPassword(reqBody.getString("password"));
                setUsername(reqBody.getString("username"));
            }};

            if (!authProcessor.isUser(userRequest)) {
                endUnauthorized(ctx.response());
                return;
            }
            SessionResponse response = authProcessor.getSession(new NewSessionRequest() {{
                setUsername(reqBody.getString("username"));
            }});

            end(ctx.response(), HttpConstants.ok_code, response.toJson());
        } catch (Exception e) {
            endUnauthorized(ctx.response());
        }
    }

    private void handlePostRefreshUser(RoutingContext ctx) {

        try {

            String refreshToken = ctx.getBodyAsJson().getString("refresh_token");

            RefreshSessionRequest request = new RefreshSessionRequest() {{
                setRefreshToken(refreshToken);
            }};

            RefreshSessionResponse response = authProcessor.refreshSession(request);

            end(ctx.response(), HttpConstants.created_code, response.toJson());

        } catch (Exception e) {
            endUnauthorized(ctx.response());
        }
    }

    private void handleDeleteLogoutUser(RoutingContext ctx) {

        try {
            String refreshToken = ctx.getBodyAsJson().getString("refreshToken");
            authProcessor.endSession(refreshToken);
        } catch (Exception e) {
            endClientError(ctx.response());
        }

    }

    private void handlePostNewUser(RoutingContext ctx) {

        try {

            JsonObject body = ctx.getBodyAsJson();

            NewUserRequest userRequest = new NewUserRequest() {{
                setEmail(body.getString("email"));
                setUsername(body.getString("username"));
                setPassword(body.getString("password"));
                setFirstName(body.getString("first_name"));
                setLastName(body.getString("last_name"));
            }};

            authProcessor.newUser(userRequest);

        } catch (Exception e) {
            endClientError(ctx.response());
        }
    }

    private boolean authorized(HttpServerRequest req) {

        String accessToken;

        try {
            accessToken = req.getHeader("access_token");
            return authProcessor.isAuthorized(accessToken);
        } catch (Exception e) {
            return false;
        }
    }

    private void endClientError(HttpServerResponse resp) {
        String errorResponse = new ClientErrorResponse(HttpConstants.clientErrorMessage).toJson();
        end(resp, HttpConstants.client_error_code, errorResponse);
    }

    private void endUnauthorized(HttpServerResponse resp) {
        String errorResponse = new ClientErrorResponse(HttpConstants.unauthorizedMessage).toJson();
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
}
