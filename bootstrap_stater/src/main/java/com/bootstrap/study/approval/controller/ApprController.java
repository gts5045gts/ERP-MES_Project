package com.bootstrap.study.approval.controller;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootstrap.study.approval.constant.ApprReqType;
import com.bootstrap.study.approval.dto.ApprDTO;
import com.bootstrap.study.approval.dto.ApprFullDTO;
import com.bootstrap.study.approval.service.ApprService;
import com.bootstrap.study.personnel.dto.PersonnelDTO;

import jakarta.validation.Valid;

import com.bootstrap.study.approval.dto.ApprDetailDTO;
import com.bootstrap.study.approval.dto.ApprEmpDTO;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/new/{reqTypeVal}")
    public String draftingForm(@PathVariable("reqTypeVal") ApprReqType reqTypeVal, Model model){
    	
    	model.addAttribute("apprDTO", new ApprDTO());
    	model.addAttribute("apprDetailDTO", new ApprDetailDTO());
    	model.addAttribute("selectedRole", reqTypeVal); // 기본 선택값
    	        
        return "approval/drafting_form";
    }

    // 결재목록
    @GetMapping("/approval_list")
    public String approvalList(
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            Model model) {
        
        System.out.println(">>> Controller 시작 - 페이지: " + page);
        
        // 페이징 설정 (1페이지당 5개)
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createAt"));
        
        Page<ApprDTO> approvalPage = apprService.getApprovalList(pageable);
        System.out.println(">>> 조회된 페이지 정보 - 현재페이지: " + approvalPage.getNumber() + 
                          ", 전체페이지: " + approvalPage.getTotalPages() + 
                          ", 전체데이터: " + approvalPage.getTotalElements());
        
        model.addAttribute("approvalList", approvalPage.getContent());
        model.addAttribute("currentPage", approvalPage.getNumber());
        model.addAttribute("totalPages", approvalPage.getTotalPages());
        model.addAttribute("totalElements", approvalPage.getTotalElements());
        model.addAttribute("hasPrevious", approvalPage.hasPrevious());
        model.addAttribute("hasNext", approvalPage.hasNext());
        
        return "approval/approval_list";    
    }
 	
 	// 결재하기 버튼 누를시
    // JavaScript의 fetch 요청을 처리하고, JSON 데이터를 반환합니다.
 	@GetMapping("/api/detail/{reqId}")
    @ResponseBody // 이 어노테이션이 있어야 HTML이 아닌 데이터(JSON)를 반환합니다.
    public ResponseEntity<ApprFullDTO> getApprovalDetail(@PathVariable("reqId") Long reqId) {
 		System.out.println(">>>>>> Controller: 결재 상세 조회 API 호출 - reqId: " + reqId);
        ApprFullDTO detailDTO = apprService.getApprovalDetail(reqId);
        return ResponseEntity.ok(detailDTO); // 성공 응답(200 OK)과 함께 데이터를 보냄
    }
 	
 	@GetMapping("/showDraftingForm") 
 	public String showMyForm() {
 	    System.out.println("단순 폼 보여주기 요청 성공!");
 	    return"approval/drafting_form"; 
 	}
    
    //결재자 검색
    @GetMapping("/empSearch")
    @ResponseBody
    public List<ApprEmpDTO> searchUser(@RequestParam("name") String name) {
        return apprService.getApprEmployee(name);
    }

    @PostMapping("/save")
    @ResponseBody
    public String registAppr(@ModelAttribute("apprDTO") @Valid ApprDTO apprDTO, @RequestParam("empIds") String[] empIds, BindingResult bindingResult, Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            ApprReqType reqType = apprDTO.getReqType();
            return "approval/new/" + reqType;
        }

    	Long apprId = apprService.registAppr(apprDTO, empIds);

        //결재 리스트로 이동되게 변경해야함.
        return "<script>" +
                "alert('신청 완료되었습니다.');" +
                "parent.location.reload();"+
                "window.close();" +
                "</script>";
    }    
}
