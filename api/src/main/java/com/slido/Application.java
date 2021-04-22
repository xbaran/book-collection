package com.slido;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@ComponentScan(value = {
        "com.slido"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
