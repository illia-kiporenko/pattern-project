package me.kiporenko.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication(scanBasePackages = {
        "me.kiporenko.application",
        "me.kiporenko.system",
        "me.kiporenko.auth",
        "me.kiporenko.storage"})
@EnableJpaRepositories(basePackages = {
        "me.kiporenko.system.repository",
        "me.kiporenko.auth.repository",
        "me.kiporenko.storage.repository"
})
@EntityScan(basePackages = {
        "me.kiporenko.system.model",
        "me.kiporenko.auth.domain.model",
        "me.kiporenko.storage.model"
})
@EnableCaching
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class PatternProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatternProjectApplication.class, args);
    }

}