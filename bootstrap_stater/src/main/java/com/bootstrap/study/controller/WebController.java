package com.bootstrap.study.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
	
	@RequestMapping("/")
	public String login() {

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