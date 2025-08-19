package com.bootstrap.study.approval.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    
    
 	@GetMapping("/approval_list")
 	public String approvalList(@RequestParam(value = "status", required = false, defaultValue = "all") String status, Model model) {
 		System.out.println("내결재관리창!!!");
        model.addAttribute("status", status);

 		return "approval/approval_list";	
 	}
 	
 	@GetMapping("/showDraftingForm") 
 	public String showMyForm() {
 	    System.out.println("단순 폼 보여주기 요청 성공!");
 	    return"approval/drafting_form"; 
 	}
 	
 	
 	
}
