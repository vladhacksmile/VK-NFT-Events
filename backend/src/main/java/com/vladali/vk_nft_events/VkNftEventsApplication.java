package com.vladali.vk_nft_events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class VkNftEventsApplication {

    public static void main(String[] args) {
        SpringApplication.run(VkNftEventsApplication.class, args);
    }

}
