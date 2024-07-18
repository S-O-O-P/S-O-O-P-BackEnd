package com.soop.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI().components(new Components()).info(swaggerInfo());
  }

  private Info swaggerInfo(){
    return new Info().title("LinkBee API").description("LinkBee 링크비 프로젝트의 클라이언트(Client) API 명세서입니다.").version("1.0.0");
  }
}
