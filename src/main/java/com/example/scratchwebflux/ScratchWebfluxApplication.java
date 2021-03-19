package com.example.scratchwebflux;

import io.r2dbc.spi.ConnectionFactory;
import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@Configuration
@EnableR2dbcRepositories
@SpringBootApplication
public class ScratchWebfluxApplication {

  public static void main(String[] args) {
    SpringApplication.run(ScratchWebfluxApplication.class, args);
  }


  @Bean
  public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

    val initializer = new ConnectionFactoryInitializer();
    initializer.setConnectionFactory(connectionFactory);
    initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
    return initializer;
  }
}
