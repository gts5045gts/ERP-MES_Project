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
	@RequestMapping("404")
	public String a404(){
		
		return "/example/404";
	}
	@RequestMapping("buttons")
	public String buttons(){
		
		return "/example/buttons";
	}
	@RequestMapping("cards")
	public String cards(){
		
		return "/example/cards";
	}
	@RequestMapping("charts")
	public String charts(){
		
		return "/example/charts";
	}
	@RequestMapping("index2")
	public String index2(){
		
		return "/example/index2";
	}
	@RequestMapping("tables")
	public String tables(){
		
		return "/example/tables";
	}
	@RequestMapping("utill_animation")
	public String utill_animation(){
		
		return "/example/utill_animation";
	}
	@RequestMapping("utill_border")
	public String utill_border(){
		
		return "/example/utill_border";
	}
	@RequestMapping("utill_color")
	public String utill_color(){
		
		return "/example/utill_color";
	}
	@RequestMapping("utill_other")
	public String utill_other(){
		
		return "/example/utill_other";
	}

}
