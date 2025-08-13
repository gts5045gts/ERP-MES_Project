package com.bootstrap.study.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
	
	@GetMapping("/")
    public String main(){

        return "main";
    }
	
	@GetMapping("/index")
    public String login(){

        return "index";
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
