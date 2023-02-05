package com.example;

import com.example.demo.ApiGatewayVerticle;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.io.FileNotFoundException;

public class Main {
  public static void main(String[] args) throws FileNotFoundException {
    Config hazelcastConfig = new XmlConfigBuilder("api-gateway/config/hazelcast-config.xml").build();

    ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);
    VertxOptions options = new VertxOptions().setClusterManager(mgr);
    Vertx.clusteredVertx(options, res -> {
      if(res.succeeded()){
        Vertx vertx = res.result();
        vertx.deployVerticle(new ApiGatewayVerticle());
      }
    });
  }
}
