package com.bootstrap.study.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); 
	}
	
	@Bean
	public WebSecurityCustomizer ignoreStaticResources() { // 메서드 이름 무관
		return (web) -> web.ignoring() // web 객체에 대한 보안 필터 무시
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 일반적인 정적 리소트 경로 모두 지정(css, js, images, error 등)
	}
	
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    	return httpSecurity
        .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index","/bootstrap/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/")
                .loginProcessingUrl("/login")
                .usernameParameter("empId")
                .passwordParameter("empPasswd")
                .defaultSuccessUrl("/index", true)
                .successHandler(new EmpAuthenticationSuccessHandler())
                .permitAll()
            )
            // 로그아웃 처리
            .logout(logout -> logout
                .logoutSuccessUrl("/?logout")
                .permitAll()
            )
            // 자동 로그인처리
            .rememberMe(rememberMeCustomizer -> rememberMeCustomizer
					.rememberMeParameter("remember-me") // 자동 로그인 수행하기 위한 체크박스 파라미터명 지정
					.tokenValiditySeconds(60 * 60 * 24) // 자동 로그인 토큰 유효기간 설정(기본값 14일 -> 1일 변경)
					)
    		.build();
    }
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//            .userDetailsService(userDetailsService())
//            .passwordEncoder(passwordEncoder())
//            .and()
//            .build();
//    }
//
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//       UserDetails user = User.builder()
//              .username("1234")
//              .password(new BCryptPasswordEncoder().encode("1234"))  
//              .roles("USER")
//              .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }

}