package com.codeforcommunity.rest.subrouter;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.dto.auth.ForgotPasswordRequest;
import com.codeforcommunity.dto.auth.LoginRequest;
import com.codeforcommunity.dto.auth.NewUserRequest;
import com.codeforcommunity.dto.auth.RefreshSessionRequest;
import com.codeforcommunity.dto.auth.RefreshSessionResponse;
import com.codeforcommunity.dto.auth.ResetPasswordRequest;
import com.codeforcommunity.dto.auth.SessionResponse;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

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
    registerVerifySecretKey(router);
    registerRequestForgotPassword(router);
    registerResetPassword(router);

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
    Route logoutUserRoute = router.delete("/login");
    logoutUserRoute.handler(this::handleDeleteLogoutUser);
  }

  private void registerRequestForgotPassword(Router router) {
    Route forgotPasswordRequestRoute = router.post("/forgot_password/request");
    forgotPasswordRequestRoute.handler(this::handleForgotPasswordRequest);
  }

  private void registerResetPassword(Router router) {
    Route resetPasswordRoute = router.post("/forgot_password/reset");
    resetPasswordRoute.handler(this::handleResetPassword);
  }

  /** This route is for validating a secret key that has been sent to a user's email. */
  private void registerVerifySecretKey(Router router) {
    Route verifySecretKeyRoute = router.get("/verify/:secret_key");
    verifySecretKeyRoute.handler(this::handleVerifySecretKey);
  }

  private void handlePostUserLoginRoute(RoutingContext ctx) {
    LoginRequest userRequest = RestFunctions.getJsonBodyAsClass(ctx, LoginRequest.class);

    SessionResponse response = authProcessor.login(userRequest);

    end(ctx.response(), 200, JsonObject.mapFrom(response).encode());
  }

  private void handlePostRefreshUser(RoutingContext ctx) {
    String refreshToken = RestFunctions.getRequestHeader(ctx.request(), "X-Refresh-Token");
    RefreshSessionRequest request = new RefreshSessionRequest(refreshToken);

    RefreshSessionResponse response = authProcessor.refreshSession(request);

    end(ctx.response(), 201, JsonObject.mapFrom(response).toString());
  }

  private void handleDeleteLogoutUser(RoutingContext ctx) {
    String refreshToken = RestFunctions.getRequestHeader(ctx.request(), "X-Refresh-Token");
    authProcessor.logout(refreshToken);
    end(ctx.response(), 204);
  }

  private void handlePostNewUser(RoutingContext ctx) {
    NewUserRequest request = RestFunctions.getJsonBodyAsClass(ctx, NewUserRequest.class);
    SessionResponse response = authProcessor.signUp(request);

    end(ctx.response(), 201, JsonObject.mapFrom(response).toString());
  }

  private void handleForgotPasswordRequest(RoutingContext ctx) {
    ForgotPasswordRequest request =
        RestFunctions.getJsonBodyAsClass(ctx, ForgotPasswordRequest.class);

    authProcessor.requestPasswordReset(request);

    end(ctx.response(), 200);
  }

  private void handleResetPassword(RoutingContext ctx) {
    ResetPasswordRequest request =
        RestFunctions.getJsonBodyAsClass(ctx, ResetPasswordRequest.class);

    authProcessor.resetPassword(request);

    end(ctx.response(), 200);
  }

  private void handleVerifySecretKey(RoutingContext ctx) {
    String secret = ctx.pathParam("secret_key");
    authProcessor.verifyEmail(secret);
    end(ctx.response(), 200);
  }
}
