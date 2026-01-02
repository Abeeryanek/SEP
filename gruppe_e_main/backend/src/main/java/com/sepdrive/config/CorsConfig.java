package com.sepdrive.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;

public class CorsConfig {

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                          // alle Endpunkte
                .allowedOrigins("http://localhost:4200")    // Frontend-URL
                .allowedMethods("*")                        // GET,POST,PUT,…
                .allowedHeaders("*")
                .allowCredentials(true);                    // JSESSIONID / Cookies
    }
}
