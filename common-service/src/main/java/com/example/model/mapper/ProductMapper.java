package com.example.model.mapper;

import com.example.model.Product;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

public class ProductMapper {
  public Product toModel(Row row){
    if(row == null){
      return null;
    }

    Product product = new Product();
    product.setId(row.getInteger("id"));
    product.setName(row.getString("name"));
    product.setDescription(row.getString("description"));

    return product;
  }

  public Product toModel(JsonObject jsonObject){
    if(jsonObject == null){
      return null;
    }

    Product product = new Product();
    if(jsonObject.containsKey("id")){
      product.setId(jsonObject.getInteger("id"));
    }

    product.setName(jsonObject.getString("name"));
    product.setDescription(jsonObject.getString("description"));
    product.setPrice(jsonObject.getFloat("price"));

    return product;
  }
}
