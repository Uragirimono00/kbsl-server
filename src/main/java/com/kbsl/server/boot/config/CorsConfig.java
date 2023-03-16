package com.kbsl.server.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
    /**
     * CORS(Cross-Origin Resource Sharing) : 쉽게 말해 도메인이 다른 서버로 리소스 요청을 보내면 CORS 정책을 위반하기 때문에 에러 발생!
     * 해결 방법 ?
     * CorsConfiguration 객체를 생성하여, 원하는 요청에 대해서 허용을 해주면 됩니다.
     * HTTP OPTION으로 먼저 예비 요청을 보내고, 서버에서 요청을 허용한다는 응답을 받으면 그때 GET or POST 로 원하는 리소스 요청을 보내는 방법이다.
     * @return
     */
    @Bean
    public CorsConfigurationSource corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        // config.addAllowedOrigin("*");
        corsConfiguration.addAllowedOriginPattern("*"); // addAllowedOriginPattern("*") 대신 사용
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        source.registerCorsConfiguration("/api/**", corsConfiguration);    // /api/** 로 들어오는 모든 요청들은 config를 따르도록 등록!
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
