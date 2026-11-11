package ru.seventech.service.requests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "ru.seventech.service.requests", // Скан вашего текущего сервиса
        "ru.seventech.basetemplate"       // Скан утилит безопасности, включая BaseSecurityHelper
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
