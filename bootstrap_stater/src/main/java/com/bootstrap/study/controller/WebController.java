package com.bootstrap.study.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
	@RequestMapping("/")
    public String main(){

        return "index";
    }
	
	@RequestMapping("login")
    public String login(){

        return "login";
    }
	
	@RequestMapping("register")
    public String regist(){

        return "register";
    }
	
	@RequestMapping("blank")
    public String blank(){

        return "blank";
    }

}
