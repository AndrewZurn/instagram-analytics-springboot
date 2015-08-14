package com.andrewzurn.instagram.analyzer.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Created by andrew on 8/10/15.
 */
@Configuration
@EnableCassandraRepositories("com.andrewzurn.instagram.analyzer.repository")
public class CassandraConfiguration extends AbstractCassandraConfiguration {

  private static final String KEYSPACE = "instagram_prototype_raw";
  private static final String CASSANDRA_IP = "localhost";
  private static final int CASSANDRA_PORT = 9042;

  @Bean
  @Override
  public CassandraClusterFactoryBean cluster() {
    CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
    cluster.setContactPoints(CASSANDRA_IP);
    cluster.setPort(CASSANDRA_PORT);
    return cluster;
  }

  @Override
  protected String getKeyspaceName() {
    return KEYSPACE;
  }

  @Bean
  @Override
  public CassandraMappingContext cassandraMapping() throws  ClassNotFoundException {
    return new BasicCassandraMappingContext();
  }

}
