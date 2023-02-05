package com.example.route;

import com.example.handler.ProductHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class Route {
  public static Router getRoutes(
    Vertx vertx,
    ProductHandler productService
  ) {
    Router router = Router.router(vertx);

    // add header for all response
    router.route().handler(routingContext -> {
      HttpServerResponse response = routingContext.response();
      response.putHeader("Content-Type", "application/json");
      response.putHeader("Accept", "application/json");
      routingContext.next();
    });

    // register BodyHandler globally.
    router.get("/product/listing").produces("application/json").handler(productService::getAll);
    router.post("/product/create").consumes("application/json").handler(BodyHandler.create()).handler(productService::store);
    router.get("/product/:id").produces("application/json").handler(productService::get);
    router.post("/product/update/:id").consumes("application/json").handler(BodyHandler.create()).handler(productService::update);
    router.post("/posts/delete/:id").handler(productService::delete);

    return router;
  }
}
