package com.example;

import com.example.demo.ProductServiceVerticle;
import com.hazelcast.config.Config;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.ConfigUtil;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class Main {
  public static void main(String[] args) {
    Config hazelcastConfig = ConfigUtil.loadConfig();

    hazelcastConfig.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1").setEnabled(true);
    hazelcastConfig.getNetworkConfig().setPort(5702).setPortCount(100).setPortAutoIncrement(true);
    hazelcastConfig.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
    hazelcastConfig.setClusterName("demo");

    ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);
    VertxOptions options = new VertxOptions().setClusterManager(mgr);
    Vertx.clusteredVertx(options, res -> {
      if(res.succeeded()){
        Vertx vertx = res.result();
        vertx.deployVerticle(new ProductServiceVerticle());
      }
    });
  }
}
