package com.codeforcommunity.rest.subrouter;

import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.dto.auth.LoginRequest;
import com.codeforcommunity.dto.auth.NewUserRequest;
import com.codeforcommunity.dto.auth.RefreshSessionRequest;
import com.codeforcommunity.dto.auth.RefreshSessionResponse;
import com.codeforcommunity.dto.SessionResponse;
import com.codeforcommunity.rest.HttpConstants;
import com.codeforcommunity.rest.IRouter;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import static com.codeforcommunity.rest.ApiRouter.end;
import static com.codeforcommunity.rest.ApiRouter.endClientError;
import static com.codeforcommunity.rest.ApiRouter.endUnauthorized;

public class AuthRouter implements IRouter {
  private final IAuthProcessor authProcessor;

  public AuthRouter(IAuthProcessor authProcessor) {
    this.authProcessor = authProcessor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    registerLoginUser(router);
    registerRefreshUser(router);
    registerNewUser(router);
    registerLogoutUser(router);

    return router;
  }


  private void registerLoginUser(Router router) {
    Route loginUserRoute = router.post("/login");
    loginUserRoute.handler(this::handlePostUserLoginRoute);
  }

  private void registerRefreshUser(Router router) {
    Route refreshUserRoute = router.post("/login/refresh");
    refreshUserRoute.handler(this::handlePostRefreshUser);
  }

  private void registerNewUser(Router router) {
    Route newUserRoute = router.post("/signup");
    newUserRoute.handler(this::handlePostNewUser);
  }

  private void registerLogoutUser(Router router) {
    Route logoutUserRoute = router.delete( "/login");
    logoutUserRoute.handler(this::handleDeleteLogoutUser);
  }

  private void verifySecretKey(Router router) {
    Route verifySecretKeyRoute = router.get("/verify/:secret_key");
    verifySecretKeyRoute.handler(this::handleVerifySecretKey);
  }


  private void handlePostUserLoginRoute(RoutingContext ctx) {
    try {
      LoginRequest userRequest = ctx.getBodyAsJson().mapTo(LoginRequest.class);

      SessionResponse response = authProcessor.login(userRequest);

      end(ctx.response(), HttpConstants.ok_code, JsonObject.mapFrom(response).encode());
    } catch (Exception e) {
      endUnauthorized(ctx.response());
    }
  }

  private void handlePostRefreshUser(RoutingContext ctx) {
    try {
      String refreshToken = ctx.request().getHeader("refresh_token");
      RefreshSessionRequest request = new RefreshSessionRequest(refreshToken);

      RefreshSessionResponse response = authProcessor.refreshSession(request);

      end(ctx.response(), HttpConstants.created_code, JsonObject.mapFrom(response).toString());

    } catch (Exception e) {
      endUnauthorized(ctx.response());
    }
  }

  private void handleDeleteLogoutUser(RoutingContext ctx) {
    try {
      String refreshToken = ctx.request().getHeader("refreshToken");
      authProcessor.logout(refreshToken);
      end(ctx.response(), 204);
    } catch (Exception e) {
      endClientError(ctx.response());
    }
  }

  private void handlePostNewUser(RoutingContext ctx) {
    try {
      NewUserRequest request = ctx.getBodyAsJson().mapTo(NewUserRequest.class);

      SessionResponse response = authProcessor.signUp(request);

      end(ctx.response(), 201, JsonObject.mapFrom(response).toString());
    } catch (Exception e) {
      endClientError(ctx.response());
    }
  }

  private void handleVerifySecretKey(RoutingContext ctx) {
    String secret = ctx.pathParam("secret_key");


  }
}
