package com.tiktok.configration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类.
 *
 * @author lzc
 * @version 1.0
 */
@Configuration
@Data
public class Knife4jConfig {
    @Value("${knife4j.config.infoTitle}")
    private String infoTitle;
    @Value("${knife4j.config.infoDescription}")
    private String infoDescription;
    @Value("${knife4j.config.infoContactName}")
    private String infoContactName;
    @Value("${knife4j.config.infoContactEmail}")
    private String infoContactEmail;
    @Value("${knife4j.config.version}")
    private String version;
    @Value("${knife4j.config.licenseName}")
    private String licenseName;
    @Value("${knife4j.config.licenseUrl}")
    private String licenseUrl;

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title(infoTitle)
                        .description(infoDescription)
                        .contact(new Contact().name(infoContactName).email(infoContactEmail))
                        .version(version)
                        .license(new License().name(licenseName).url(licenseUrl)));
    }
}