package com.bootstrap.study.approval.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bootstrap.study.approval.dto.apprDTO;

@Controller
@RequestMapping("/approval")
@Log4j2
public class apprController {

    @GetMapping("/doc_list")
    public String getDocList(){
        return "/approval/appr_doc_list";
    }

    @GetMapping("/new")
    public String draftingForm(Model model){
    	model.addAttribute("apprDTO", new apprDTO());
    	    	
        return "approval/drafting_form";
    }

}
