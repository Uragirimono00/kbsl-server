package com.kbsl.kbslserver.boot.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KbslServerApplication {

    public static void main(String[] args)
    {
        SpringApplication springApplication = new SpringApplication(KbslServerApplication.class);

        springApplication.setLogStartupInfo(false);
        springApplication.run(args);
    }

}
