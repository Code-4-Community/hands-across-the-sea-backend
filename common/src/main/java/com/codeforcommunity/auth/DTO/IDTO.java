package com.codeforcommunity.auth.DTO;

import io.vertx.core.json.JsonObject;

public interface IDTO {

    default String toJson() {
        return JsonObject.mapFrom(this).encode(); //todo change this to use jackson so we can avoid vertx dependency
    }

}
