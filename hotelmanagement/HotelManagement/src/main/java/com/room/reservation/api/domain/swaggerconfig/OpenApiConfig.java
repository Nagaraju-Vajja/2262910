package com.room.reservation.api.domain.swaggerconfig;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

  

  @Bean
  public OpenAPI myOpenAPI() {
//    Server devServer = new Server();
//    
//    devServer.setDescription("Server URL in Development environment");
//
//    Server prodServer = new Server();
//  
//    prodServer.setDescription("Server URL in Production environment");

    


    Info info = new Info()
        .title("Hotel Management API")
        .description("This API exposes endpoints to provide hotel services.");
       

    return new OpenAPI().info(info);
  }
}