package com.kbsl.server.boot.config;

import com.kbsl.server.auth.fliter.AuthJwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthJwtFilter authJwtFilter;
    private final CorsConfig corsConfig;

    private static final String[] PERMIT_URL_ARRAY = {
        /* swagger v2 */
        "/v2/api-docs",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**",
        /* swagger v3 */
        "/v3/api-docs/**",
        "/swagger-ui/**"
    };

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
            .oauth2Login()
            .and()
            // CORS
            .cors().configurationSource(corsConfig.corsFilter())
            .and()
            // CSRF 해제 - JWT 사용하기때문
            .csrf().disable()
            // JWT 사용을 위한 Stateless Policy 설정
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .formLogin().disable()
            .httpBasic().disable()
            .authorizeHttpRequests((authz) -> authz
                .antMatchers(PERMIT_URL_ARRAY).permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/**/auth/**").permitAll()
                .antMatchers("/**/adm/**").hasAnyRole("ADMIN")
                .antMatchers("/**/league/**").permitAll()
                .antMatchers("/**/score/**").permitAll()
                .antMatchers("/**/user/**").permitAll()
                .antMatchers("/**/song/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(authJwtFilter, UsernamePasswordAuthenticationFilter.class)
            );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().antMatchers("/resources/**"));
    }
}
