package com.erp_mes.erp.config.security;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

// 스프링시큐리티 로그인 성공 시 작업을 처리하는 핸들러 정의(AuthenticationSuccessHandler 인터페이스 구현체로 정의)
// 별도로 스프링 빈으로 등록할 필요 없음
@Log4j2
public class EmpAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	// 로그인 성공 시 별도의 추가 작업(ex. 아이디 기억하기, 읽지 않은 메세지 확인 작업 등)을 onAuthenticationSuccess() 메서드 오버라이딩
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		response.sendRedirect("/main"); // 메인페이지로 리다이렉트
	}

}

