package com.portfolio.fastbreak.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Fastbreak API 문서 (MVP)")
                .description("농구 티켓 예매 시스템(MVP)을 위한 프론트엔드 연동용 REST API 명세서입니다.")
                .version("1.0.0");
    }
}
