package com.codeforcommunity.auth.DTO;

import io.vertx.core.json.JsonObject;

public interface IDTO {

    default String toJson() {
        return JsonObject.mapFrom(this).encode();
    }

}
