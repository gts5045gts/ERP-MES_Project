package com.bootstrap.study.approval.controller;

import lombok.extern.log4j.Log4j2;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootstrap.study.approval.dto.ApprDTO;
import com.bootstrap.study.approval.dto.ApprFullDTO;
import com.bootstrap.study.approval.service.ApprService;
import com.bootstrap.study.approval.dto.ApprDetailDTO;

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
    
    
    // 결재목록
    @GetMapping("/approval_list")
    public String approvalList(
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            Model model) {
        
        System.out.println(">>> Controller - 상태 필터: " + status + ", 페이지: " + page);
        
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createAt"));
        
        // 상태에 따라 다른 데이터 조회
        Page<ApprDTO> approvalPage = apprService.getApprovalList(pageable, status);
        
        model.addAttribute("approvalList", approvalPage.getContent());
        model.addAttribute("currentPage", approvalPage.getNumber());
        model.addAttribute("totalPages", approvalPage.getTotalPages());
        model.addAttribute("totalElements", approvalPage.getTotalElements());
        model.addAttribute("hasPrevious", approvalPage.hasPrevious());
        model.addAttribute("hasNext", approvalPage.hasNext());
        model.addAttribute("currentStatus", status); // 현재 선택된 상태 추가
        
        return "approval/approval_list";    
    }
 	
 	// 결재하기 버튼 누를시
    // JavaScript의 fetch 요청을 처리하고, JSON 데이터를 반환합니다.
    @GetMapping("/api/detail/{reqId}")
    @ResponseBody
    public ResponseEntity<ApprFullDTO> getApprovalDetail(@PathVariable("reqId") Long reqId) {
        System.out.println(">>>>>> Controller 진입: 결재 상세 조회 API 호출 - reqId: " + reqId);
        
        try {
            ApprFullDTO detailDTO = apprService.getApprovalDetail(reqId);
            System.out.println(">>>>>> Controller: 데이터 조회 성공 - " + detailDTO);
            return ResponseEntity.ok(detailDTO); 
        } catch (Exception e) {
            System.err.println(">>>>>> Controller 에러: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
 	
 	@GetMapping("/showDraftingForm") 
 	public String showMyForm() {
 	    System.out.println("단순 폼 보여주기 요청 성공!");
 	    return"approval/drafting_form"; 
 	}
 	
 	//0821
 	// 승인버튼 누를시 승인처리되게 하기 (결재목록에서 대기 -> 승인으로 바뀜, 데이터도 반영)
 	// ApprController.java - comments 포함한 승인/반려 API

 	@PostMapping("/api/approve/{reqId}")
 	@ResponseBody
 	public ResponseEntity<String> approveRequest(@PathVariable("reqId") Long reqId,
 	                                            @RequestBody(required = false) Map<String, String> requestBody) {
 	    System.out.println(">>>>>> 승인 처리 API 호출 - reqId: " + reqId);
 	    
 	    try {
 	        // comments 추출 (없으면 빈 문자열)
 	        String comments = "";
 	        if (requestBody != null && requestBody.containsKey("comments")) {
 	            comments = requestBody.get("comments");
 	        }
 	        
 	        System.out.println(">>>>>> 승인 사유: " + comments);
 	        
 	        // 승인 처리 서비스 호출 (comments 포함)
 	        apprService.approveRequestWithComments(reqId, comments);
 	        
 	        System.out.println(">>>>>> 승인 처리 완료 - reqId: " + reqId);
 	        return ResponseEntity.ok("승인 처리가 완료되었습니다.");
 	        
 	    } catch (Exception e) {
 	        System.err.println(">>>>>> 승인 처리 실패 - reqId: " + reqId + ", 오류: " + e.getMessage());
 	        return ResponseEntity.status(500).body("승인 처리 중 오류가 발생했습니다.");
 	    }
 	}

 	@PostMapping("/api/reject/{reqId}")
 	@ResponseBody 
 	public ResponseEntity<String> rejectRequest(@PathVariable("reqId") Long reqId,
 	                                           @RequestBody(required = false) Map<String, String> requestBody) {
 	    System.out.println(">>>>>> 반려 처리 API 호출 - reqId: " + reqId);
 	    
 	    try {
 	        // comments 추출 (없으면 빈 문자열)
 	        String comments = "";
 	        if (requestBody != null && requestBody.containsKey("comments")) {
 	            comments = requestBody.get("comments");
 	        }
 	        
 	        System.out.println(">>>>>> 반려 사유: " + comments);
 	        
 	        // 반려 처리 서비스 호출 (comments 포함)
 	        apprService.rejectRequestWithComments(reqId, comments);
 	        
 	        System.out.println(">>>>>> 반려 처리 완료 - reqId: " + reqId);
 	        return ResponseEntity.ok("반려 처리가 완료되었습니다.");
 	        
 	    } catch (Exception e) {
 	        System.err.println(">>>>>> 반려 처리 실패 - reqId: " + reqId + ", 오류: " + e.getMessage());
 	        return ResponseEntity.status(500).body("반려 처리 중 오류가 발생했습니다.");
 	    }
 	}
 	
}
