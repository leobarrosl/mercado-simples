package com.leobarrosl.mercado_simples.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Mercado Simples API")
                        .description("API para gerenciamento de um mercado simples")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Leonardo Leite")
                                .email("leonardoleite99@gmail.com")
                                .url("https://github.com/leobarrosl"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}