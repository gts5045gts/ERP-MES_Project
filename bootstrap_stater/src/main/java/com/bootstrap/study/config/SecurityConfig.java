package com.bootstrap.study.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/main","/index", "/bootstrap/**").permitAll() // 로그인 페이지 및 정적 리소스 허용
                .anyRequest().authenticated() // 그 외 요청은 인증 필요
            )
            .formLogin(form -> form
                .loginPage("/main") // 커스텀 로그인 페이지
                .loginProcessingUrl("/login") 
                .defaultSuccessUrl("/index", true) // 로그인 성공 시 이동할 페이지
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/main?logout") // 로그아웃 성공 시 이동할 페이지
                .permitAll()
            );

        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder() // 테스트용, 실제 서비스에서는 PasswordEncoder 사용 권장
//            .username("user")
//            .password("password")
//            .roles("USER")
//            .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }
}
