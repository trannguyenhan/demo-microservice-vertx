package com.example.infrastructure;

import io.vertx.core.Vertx;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

public class RabbitMq {
  public static RabbitMQClient createClientWithManualParams(Vertx vertx) {
    RabbitMQOptions config = new RabbitMQOptions();
    // Each parameter is optional
    // The default parameter with be used if the parameter is not set
    config.setUser("trannguyenhan");
    config.setPassword("123456");
    config.setHost("localhost");
    config.setPort(5672);
    config.setVirtualHost("vhost1");
    config.setConnectionTimeout(6000);
    config.setRequestedHeartbeat(60);
    config.setHandshakeTimeout(6000);
    config.setRequestedChannelMax(5);
    config.setNetworkRecoveryInterval(500);
    config.setAutomaticRecoveryEnabled(true);

    return RabbitMQClient.create(vertx, config);
  }

}
