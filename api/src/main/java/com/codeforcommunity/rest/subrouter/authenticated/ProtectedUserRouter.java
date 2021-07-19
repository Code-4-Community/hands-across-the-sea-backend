package com.codeforcommunity.rest.subrouter.authenticated;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.authenticated.IProtectedUserProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.user.ChangeEmailRequest;
import com.codeforcommunity.dto.user.ChangePasswordRequest;
import com.codeforcommunity.dto.user.UserDataRequest;
import com.codeforcommunity.dto.user.UserDataResponse;
import com.codeforcommunity.dto.user.UserListResponse;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.Optional;

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
    registerUpdateUserData(router);
    registerGetAllUsers(router);
    registerDisableAccount(router);
    registerEnableAccount(router);
    registerGetDisabledUsers(router);
    return router;
  }

  private void registerUpdateUserData(Router router) {
    Route updateUserRoute = router.put("/:user_id");
    updateUserRoute.handler(this::handleUpdateUserData);
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

  private void registerGetAllUsers(Router router) {
    Route getAllUsersRoute = router.get("/");
    getAllUsersRoute.handler(this::handleGetAllUsers);
  }

  private void registerDisableAccount(Router router) {
    Route disableUserAccountRoute = router.post("/disable/:user_id");
    disableUserAccountRoute.handler(this::handleDisableUser);
  }

  private void registerEnableAccount(Router router) {
    Route enableUserAccountRoute = router.post("/enable/:user_id");
    enableUserAccountRoute.handler(this::handleEnableUser);
  }

  private void registerGetDisabledUsers(Router router) {
    Route getDisabledUsersRoute = router.get("/disabled");
    getDisabledUsersRoute.handler(this::handleGetDisabledUsers);
  }

  private void handleDisableUser(RoutingContext ctx) {
    JWTData jwtData = ctx.get("jwt_data");
    int userId = RestFunctions.getPathParamAsInt(ctx, "user_id");
    processor.disableUserAccount(jwtData, userId);
    end(ctx.response(), 200);
  }

  private void handleEnableUser(RoutingContext ctx) {
    JWTData jwtData = ctx.get("jwt_data");
    int userId = RestFunctions.getPathParamAsInt(ctx, "user_id");
    processor.enableUserAccount(jwtData, userId);
    end(ctx.response(), 200);
  }

  private void handleGetAllUsers(RoutingContext ctx) {
    JWTData jwtData = ctx.get("jwt_data");
    Optional<String> countryName =
        RestFunctions.getOptionalQueryParam(ctx, "country", (str -> str));

    UserListResponse users;

    if (!countryName.isPresent()) {
      users = processor.getAllUsers(jwtData, null);
    } else {
      Country country = RestFunctions.getCountryFromString(countryName.get());
      users = processor.getAllUsers(jwtData, country);
    }
    end(ctx.response(), 200, JsonObject.mapFrom(users).toString());
  }

  private void handleGetDisabledUsers(RoutingContext ctx) {
    JWTData jwtData = ctx.get("jwt_data");
    Optional<String> countryName =
        RestFunctions.getOptionalQueryParam(ctx, "country", (str -> str));

    UserListResponse users;
    if (!countryName.isPresent()) {
      users = processor.getDisabledUsers(jwtData);
    } else {
      Country country = RestFunctions.getCountryFromString(countryName.get());
      users = processor.getDisabledUsers(jwtData, country);
    }
    end(ctx.response(), 200, JsonObject.mapFrom(users).toString());
  }

  private void handleUpdateUserData(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int userId = RestFunctions.getPathParamAsInt(ctx, "user_id");
    UserDataRequest request = RestFunctions.getJsonBodyAsClass(ctx, UserDataRequest.class);
    processor.updateUserData(userData, userId, request);
    end(ctx.response(), 200);
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
