package com.kbsl.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication()
@EnableJpaAuditing
public class KbslServerApplication {

    public static void main(String[] args)
    {
        SpringApplication springApplication = new SpringApplication(KbslServerApplication.class);

        springApplication.setLogStartupInfo(false);
        springApplication.run(args);
    }

}
