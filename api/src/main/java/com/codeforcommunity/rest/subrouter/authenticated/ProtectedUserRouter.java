package com.codeforcommunity.rest.subrouter.authenticated;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.authenticated.IProtectedUserProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.user.ChangeEmailRequest;
import com.codeforcommunity.dto.user.ChangePasswordRequest;
import com.codeforcommunity.dto.user.GetAllUsersFromCountryRequest;
import com.codeforcommunity.dto.user.UserDataResponse;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.List;

public class ProtectedUserRouter implements IRouter {

  private final IProtectedUserProcessor processor;

  public ProtectedUserRouter(IProtectedUserProcessor processor) {
    this.processor = processor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    registerDeleteUser(router);
    registerChangePassword(router);
    registerGetUserData(router);
    registerChangeEmail(router);
    registerGetAllUsersFromASpecificCountry(router);
    registerGetAllUsers(router);

    return router;
  }

  private void registerDeleteUser(Router router) {
    Route deleteUserRoute = router.delete("/");
    deleteUserRoute.handler(this::handleDeleteUserRoute);
  }

  private void registerChangePassword(Router router) {
    Route changePasswordRoute = router.post("/change_password");
    changePasswordRoute.handler(this::handleChangePasswordRoute);
  }

  private void registerGetUserData(Router router) {
    Route getUserDataRoute = router.get("/data");
    getUserDataRoute.handler(this::handleGetUserDataRoute);
  }

  private void registerChangeEmail(Router router) {
    Route changePasswordRoute = router.post("/change_email");
    changePasswordRoute.handler(this::handleChangeEmailRoute);
  }

  private void registerGetAllUsersFromASpecificCountry(Router router) {
    Route getAllUsersFromCountryRoute = router.get("/country");
    getAllUsersFromCountryRoute.handler(this::handleGetAllUsersFromASpecificCountry);
  }

  private void registerGetAllUsers(Router router) {
    Route getAllUsersRoute = router.get("/");
    getAllUsersRoute.handler(this::handleGetAllUsers);
  }

  private void handleGetAllUsers(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");

    List<UserDataResponse> response = processor.getAllUsers(userData);

    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }

  private void handleGetAllUsersFromASpecificCountry(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");

    GetAllUsersFromCountryRequest request = RestFunctions.getJsonBodyAsClass(ctx, GetAllUsersFromCountryRequest.class);

    List<UserDataResponse> response = processor.getAllUsersFromCountry(userData, request);

    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }

  private void handleDeleteUserRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");

    processor.deleteUser(userData);

    end(ctx.response(), 200);
  }

  private void handleChangePasswordRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    ChangePasswordRequest changePasswordRequest =
        RestFunctions.getJsonBodyAsClass(ctx, ChangePasswordRequest.class);

    processor.changePassword(userData, changePasswordRequest);

    end(ctx.response(), 200);
  }

  private void handleGetUserDataRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");

    UserDataResponse response = processor.getUserData(userData);

    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }

  private void handleChangeEmailRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    ChangeEmailRequest changeEmailRequest =
        RestFunctions.getJsonBodyAsClass(ctx, ChangeEmailRequest.class);

    processor.changeEmail(userData, changeEmailRequest);

    end(ctx.response(), 200);
  }
}
