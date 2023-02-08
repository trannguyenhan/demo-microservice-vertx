package com.example.infrastructure;

import com.example.model.constant.DbConstant;
import com.example.utils.ConfigUtil;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.Properties;

public class PostgreSql {
  public static PgPool pgPool(Vertx vertx) {
    final Properties properties = ConfigUtil.getInstance().getProperties();

    // Set the default schema
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(Integer.parseInt(properties.getProperty(DbConstant.PORT_CONFIG)))
      .setHost(properties.getProperty(DbConstant.HOST_CONFIG))
      .setDatabase(properties.getProperty(DbConstant.DATABASE_CONFIG))
      .setUser(properties.getProperty(DbConstant.USERNAME_CONFIG))
      .setPassword(properties.getProperty(DbConstant.PASSWORD_CONFIG));

    // Pool Options
    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

    // Create the pool from the data object
    return PgPool.pool(vertx, connectOptions, poolOptions);
  }
}
