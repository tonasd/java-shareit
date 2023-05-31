package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItServer {

    public static void main(String[] args) {
        SpringApplication.run(ShareItServer.class, args);
    }

   /* @PostConstruct   // if applied than not pass test in github due to timeZone difference
    public void init() {
        // Setting Spring Boot SetTimeZone
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
    }*/

}
