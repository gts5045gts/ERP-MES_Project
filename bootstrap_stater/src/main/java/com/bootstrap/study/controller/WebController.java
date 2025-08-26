package com.bootstrap.study.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
	@RequestMapping("/")
	public String login(@CookieValue(value = "remember-id", required = false) String rememberId, Model model) {
		// 쿠키값 Model 객체에 추가
				model.addAttribute("rememberId", rememberId);
		return "login";
	}

	@RequestMapping("main")
	public String main() {

		return "main";
	}

	@RequestMapping("register")
	public String regist() {

		return "register";
	}

	@RequestMapping("blank")
	public String blank() {

		return "blank";
	}

}