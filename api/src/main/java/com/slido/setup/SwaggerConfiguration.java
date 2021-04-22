package com.slido.setup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@Profile("swagger")
@EnableSwagger2
public class SwaggerConfiguration {

    @Value( "${swagger.base.path}" )
    private String basePath = "localhost:8080";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .host(basePath)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.slido.book.api"))
                .paths(PathSelectors.any())

                .build();
    }
}
