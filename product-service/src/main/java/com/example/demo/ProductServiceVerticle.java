package com.example.demo;

import com.example.handler.ProductHandler;
import com.example.infrastructure.PostgreSql;
import com.example.model.Product;
import com.example.model.mapper.ProductMapper;
import com.example.repository.ProductRepository;
import com.example.response.ResponseEntity;
import com.example.route.Route;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgPool;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductServiceVerticle extends AbstractVerticle {
  private ProductHandler productHandler;
  private PgPool pgPool;
  private ProductMapper productMapper;
  private ProductRepository productRepository;

  @Override
  public void start(Promise<Void> startPromise) {
    pgPool = PostgreSql.pgPool(vertx);
    productMapper = new ProductMapper();
    productRepository = new ProductRepository(pgPool, productMapper);
    productHandler = new ProductHandler(productRepository);

    if(false){ // true: monolith, false: microservice
      Router router = Route.getRoutes(vertx, productHandler);

      vertx.createHttpServer()
        .requestHandler(router)
        .listen(8888)
        .onSuccess(httpServer -> {
          startPromise.complete();
          System.out.println("HTTP server started on port " + httpServer.actualPort());
        })
        .onFailure(throwable -> {
          startPromise.fail(throwable);
          System.err.println("Failed to start HTTP server:" + throwable.getMessage());
        });
    }

    EventBus eventBus = vertx.eventBus();

    eventBus.consumer("api-requests", message -> {
      String dataString = message.body().toString();
      JsonObject dataObject = new JsonObject(dataString);

      String path = dataObject.getString("path");
      String method = dataObject.getString("method");
      String body = dataObject.getString("body");
      JsonObject bodyObject = new JsonObject();
      if(body != null){
        bodyObject = new JsonObject(body);
      }

      System.out.println("Path from API Gateway: " + path + " (" + method + ")");
      handleRoutes(message, path, method, bodyObject);
    });
  }

  @SneakyThrows
  public void handleRoutes(Message<Object> message, String path, String method, JsonObject body){
    if(path.equals("/product/listing") && method.equals("GET")){
      productRepository.findAll()
        .onSuccess(products -> {
          message.reply(Json.encode(ResponseEntity.okEntity(products)));
        })
        .onFailure(throwable -> {
          message.reply(Json.encode(ResponseEntity.okEntity(1, throwable.getMessage(), null)));
        });
    }

    if(path.equals("/product/create") && method.equals("POST")){
      Product product = productMapper.toModel(body);
      productRepository.insert(product)
          .onSuccess(prod -> {
            message.reply(Json.encode(ResponseEntity.okEntity(prod)));
          })
        .onFailure(throwable -> {
            message.reply(Json.encode(ResponseEntity.okEntity(1, throwable.getMessage(), null)));
        });
    }

    if(path.equals("/product/update") && method.equals("POST")){
      Product product = productMapper.toModel(body);
      productRepository.update(product)
        .onSuccess(prod -> {
        message.reply(Json.encode(ResponseEntity.okEntity(prod)));
      })
        .onFailure(throwable -> {
        message.reply(Json.encode(ResponseEntity.okEntity(1, throwable.getMessage(), null)));
      });
    }

    if(path.equals("/product/delete") && method.equals("POST")){
      int id = body.getInteger("id");
      productRepository.delete(id).onSuccess(integer -> {
        message.reply(Json.encode(ResponseEntity.okEntity("OK")));
      }).onFailure(throwable -> {
        message.reply(Json.encode(ResponseEntity.okEntity(1, throwable.getMessage(), null)));
      });
    }
  }

  @Override
  public void stop() throws Exception {
    super.stop();
    pgPool.close();
  }
}
