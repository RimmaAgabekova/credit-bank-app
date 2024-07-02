package ru.neoflex.deal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@Slf4j
public class DealApplication {

    public static void main(String[] args) {
        SpringApplication.run(DealApplication.class, args);
        log.info("Микросервис deal запущен");
    }
}
