package com.codeforcommunity.rest;

import com.codeforcommunity.api.INotesProcessor;
import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.auth.JWTAuthorizer;

import com.codeforcommunity.rest.subrouter.AuthRouter;
import com.codeforcommunity.rest.subrouter.CommonRouter;
import com.codeforcommunity.rest.subrouter.NotesRouter;
import io.vertx.core.Vertx;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class ApiRouter implements IRouter {
    private final CommonRouter commonRouter;
    private final NotesRouter notesRouter;
    private final AuthRouter authRouter;

    public ApiRouter(INotesProcessor notesProcessor, IAuthProcessor authProcessor, JWTAuthorizer jwtAuthorizer) {
        this.commonRouter = new CommonRouter(jwtAuthorizer);
        this.notesRouter = new NotesRouter(notesProcessor);
        this.authRouter = new AuthRouter(authProcessor);
    }

    /**
     * Initialize a router and register all route handlers on it.
     */
    public Router initializeRouter(Vertx vertx) {
        Router router = commonRouter.initializeRouter(vertx);

        router.mountSubRouter("/notes", notesRouter.initializeRouter(vertx));
        router.mountSubRouter("/user", authRouter.initializeRouter(vertx));

        return router;
    }
    public static void end(HttpServerResponse response, int statusCode) {
        end(response, statusCode, null);
    }

    public static void end(HttpServerResponse response, int statusCode, String jsonBody) {
        response.setStatusCode(statusCode)
                .putHeader(HttpConstants.contentType, HttpConstants.applicationJson)
                .putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("Access-Control-Allow-Methods", "DELETE, POST, GET, OPTIONS")
                .putHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
        if (jsonBody == null || jsonBody.equals("")) {
            response.end();
        } else {
            response.end(jsonBody);
        }
    }
}
