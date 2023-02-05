package com.example.handler;

import com.example.model.Product;
import com.example.repository.ProductRepository;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
public class ProductHandler {
  private final ProductRepository productRepository;

  public ProductHandler(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public void getAll(RoutingContext rc){
    this.productRepository.findAll()
      .onSuccess(data -> rc.response().end(Json.encode(data)))
      .onFailure(throwable -> {
        throwable.printStackTrace();
        rc.fail(throwable);
      });
  }

  public void getAll(List<Product> result){

    try {
      this.productRepository.findAll().toCompletionStage().toCompletableFuture().get();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public void get(RoutingContext rc){
    int id = rc.get("id");
    this.productRepository.findById(id)
      .onSuccess(product -> rc.response().end(Json.encode(product)))
      .onFailure(throwable -> {
        throwable.printStackTrace();
        rc.fail(throwable);
      });
  }

  public void store(RoutingContext rc){
    JsonObject data = rc.getBodyAsJson();
    Product product = new Product();
    product.setName(data.getString("name"))
      .setDescription(data.getString("description"))
      .setPrice(data.getFloat("price"));

    this.productRepository.insert(product)
      .onSuccess(item -> rc.response().end(Json.encode(item)))
      .onFailure(throwable -> {
        throwable.printStackTrace();
        rc.fail(throwable);
      });
  }

  public void store(Product product){
    this.productRepository.insert(product)
      .onSuccess(itm -> {
        System.out.println("Success insert product to table");
      })
      .onFailure(throwable -> {
        throwable.printStackTrace();
        System.err.println(throwable.getMessage());
      });
  }

  public void update(RoutingContext rc){
    Map<String, String> params = rc.pathParams();
    Integer id = Integer.valueOf(params.get("id"));

    JsonObject data = rc.getBodyAsJson();
    Product product = new Product();
    product.setId(id);
    product.setName(data.getString("name"))
      .setDescription(data.getString("description"))
      .setPrice(data.getFloat("price"));

    this.productRepository.update(product)
      .onSuccess(item -> rc.response().end(Json.encode(item)))
      .onFailure(throwable -> {
        throwable.printStackTrace();
        rc.fail(throwable);
      });
  }

  public void update(Product product){
    this.productRepository.update(product)
      .onSuccess(item -> {
        System.out.println("Success update product to table");
      })
      .onFailure(throwable -> {
        throwable.printStackTrace();
        System.err.println(throwable.getMessage());
      });
  }

  public void delete(RoutingContext rc){
    Map<String, String> params = rc.pathParams();
    Integer id = Integer.valueOf(params.get("id"));

    this.productRepository.delete(id)
      .onSuccess(item -> rc.response().end(Json.encode(item)))
      .onFailure(throwable -> {
        throwable.printStackTrace();
        rc.fail(throwable);
      });
  }

  public void delete(Integer id){
    this.productRepository.delete(id)
      .onSuccess(item -> {
        System.out.println("Success update product to table");
      })
      .onFailure(throwable -> {
        throwable.printStackTrace();
        System.err.println(throwable.getMessage());
      });
  }
}
