package com.example.repository;

import com.example.exception.ApiException;
import com.example.model.Product;
import com.example.model.mapper.ProductMapper;
import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.Tuple;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ProductRepository {
  private final PgPool client;
  private final ProductMapper productMapper;

  public ProductRepository(PgPool client,
                           ProductMapper productMapper) {
    this.client = client;
    this.productMapper = productMapper;
  }

  public Future<List<Product>> findAll(){
    return client.query("select * from products order by created_at desc")
      .execute()
      .map(rs ->
        StreamSupport.stream(rs.spliterator(), false)
          .map(productMapper::toModel)
          .collect(Collectors.toList())
      );
  }

  public Future<Product> findById(Integer id){
    Objects.requireNonNull(id, "id not null");

    return client.preparedQuery("select * from products where id=$1").execute(Tuple.of(id))
      .map(RowSet::iterator)
      .map(iterator -> {
          if (iterator.hasNext()) {
            return productMapper.toModel(iterator.next());
          }
          throw new ApiException("Product not found!");
        }
      );
  }

  public Future<Product> insert(Product product){
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt = LocalDateTime.now();
//    product.setUpdatedAt(LocalDateTime.now());
//    product.setCreatedAt(LocalDateTime.now());

    return client.preparedQuery("insert into products(name, description, price, created_at, updated_at) values ($1, $2, $3, $4, $5)")
      .execute(Tuple.of(product.getName(), product.getDescription(), product.getPrice(), createdAt, updatedAt))
      .map(rs -> rs.iterator().hasNext()? productMapper.toModel(rs.iterator().next()) : product);
  }

  public Future<Product> update(Product product){
    LocalDateTime updatedAt = LocalDateTime.now();
//    product.setUpdatedAt(LocalDateTime.now());

    return client.preparedQuery("update products set name=$1, description=$2, price=$3, updated_at=$4 where id=$5")
      .execute(Tuple.of(product.getName(), product.getDescription(), product.getPrice(), updatedAt, product.getId()))
      .map(rs -> rs.iterator().hasNext()? productMapper.toModel(rs.iterator().next()) : product);
  }

  public Future<Integer> delete(Integer id){
    Objects.requireNonNull(id, "id not null");
    return client.preparedQuery("delete from products where id=$1").execute(Tuple.of(id))
      .map(SqlResult::rowCount);
  }
}
