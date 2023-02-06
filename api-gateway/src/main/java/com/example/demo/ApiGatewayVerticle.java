package com.example.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiGatewayVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start();

    vertx.createHttpServer().requestHandler(this::handleRequest).listen(8080, result -> {
      if(result.succeeded()){
        System.out.println("API Gateway started on port 8080!");
        startPromise.complete();
      } else {
        System.err.println("Fail to start API Gateway!");
        startPromise.fail(result.cause());
      }
    });

  }

  private void handleRequest(HttpServerRequest request) {
    JsonObject requestBody = new JsonObject()
      .put("method", request.method().name())
      .put("path", request.path());

    request.bodyHandler(buffer -> {
      if(buffer.length() != 0){
        requestBody.put("body", Json.encode(buffer.toJsonObject()));
      }

      EventBus eventBus = vertx.eventBus();
      request.response()
        .putHeader("accept", "application/json")
        .putHeader("content-type", "application/json");

      eventBus.request("api-requests", requestBody, messageAsyncResult -> {
        if(messageAsyncResult.succeeded()){
          request.response()
            .end(messageAsyncResult.result().body().toString());
        } else {
          request.response().setStatusCode(500).end(new JsonObject().put("server", "error").encode());
        }
      });
    });
  }
}
