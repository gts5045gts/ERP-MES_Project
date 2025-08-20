package com.bootstrap.study.approval.controller;

import lombok.extern.log4j.Log4j2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bootstrap.study.approval.dto.ApprDTO;
import com.bootstrap.study.approval.service.ApprService;

@Controller
@RequestMapping("/approval")
@Log4j2
public class ApprController {
	
	@Autowired
    private ApprService apprService; 
	
    @GetMapping("/doc_list")
    public String getDocList(){
        return "/approval/appr_doc_list";
    }

    @GetMapping("/new")
    public String draftingForm(Model model){
    	model.addAttribute("apprDTO", new ApprDTO());
    	    	
        return "approval/drafting_form";
    }
    
    
    @GetMapping("/approval_list")
    public String approvalList(@RequestParam(value = "status", required = false, defaultValue = "all") String status, Model model) {
        // ...
        // Service에 실제로 있는 메소드 이름인 'getApprovalList'로 바꿔주세요!
        List<ApprDTO> approvalList = apprService.getApprovalList(); 
        
        model.addAttribute("approvalList", approvalList);
        //...
        return "approval/approval_list";    
    }
 	
 	@GetMapping("/showDraftingForm") 
 	public String showMyForm() {
 	    System.out.println("단순 폼 보여주기 요청 성공!");
 	    return"approval/drafting_form"; 
 	}
 	
 	
 	
}
