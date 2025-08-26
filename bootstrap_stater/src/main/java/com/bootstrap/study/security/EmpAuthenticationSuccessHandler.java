package com.bootstrap.study.security;

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
//		 아이디 기억하기 체크박스 체크 시 쿠키에 아이디(email) 저장
		String rememberId = request.getParameter("remember-id"); // 체크박스 파라미터값 가져오기
		Cookie cookie = new Cookie("remember-id", URLEncoder.encode(authentication.getName(), "UTF-8"));
		cookie.setPath("/"); // 애플리케이션 내에서 모든 경로 상에서 쿠키사용이 가능하도록 설정.
		
		if(rememberId != null && rememberId.equals("on")) {
			cookie.setMaxAge(60 * 60* 24 * 7); // 쿠키 유효기간 설정(7일)
		} else { // 아이디기억하기 체크박스 체크 해제시
			cookie.setMaxAge(0); // 쿠키 유효기간 설정(7일)
		}
		
		response.addCookie(cookie); // 응답 객체에 쿠키추가
		
		response.sendRedirect("/index"); // 메인페이지로 리다이렉트
	}

}

