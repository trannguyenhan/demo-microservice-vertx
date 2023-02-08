package com.example.utils;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.Properties;

public class PostgreSql {
  public static final String HOST_CONFIG = "datasource.host";
  public static final String PORT_CONFIG = "datasource.port";
  public static final String DATABASE_CONFIG = "datasource.database";
  public static final String USERNAME_CONFIG = "datasource.username";
  public static final String PASSWORD_CONFIG = "datasource.password";

  public static PgPool pgPool(Vertx vertx) {
    final Properties properties = ConfigUtil.getInstance().getProperties();

    // Set the default schema
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(Integer.parseInt(properties.getProperty(PORT_CONFIG)))
      .setHost(properties.getProperty(HOST_CONFIG))
      .setDatabase(properties.getProperty(DATABASE_CONFIG))
      .setUser(properties.getProperty(USERNAME_CONFIG))
      .setPassword(properties.getProperty(PASSWORD_CONFIG));

    // Pool Options
    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

    // Create the pool from the data object
    return PgPool.pool(vertx, connectOptions, poolOptions);
  }
}
