package com.bootstrap.study.approval.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

@Controller
@RequestMapping("/approval")
@RequiredArgsConstructor
@Log4j2
public class ApprController {
	
    private final ApprService apprService;
	
    @GetMapping("/doc_list")
    public String getDocList(){
        return "/approval/appr_doc_list";
    }

    @GetMapping("/new")
    public String draftingForm(Model model){
    	model.addAttribute("apprDTO", new ApprDTO());
        return "approval/drafting_form";
    }
    
    @GetMapping("/showDraftingForm") 
    public String showMyForm() {
        log.info("단순 폼 보여주기 요청");
        return "approval/drafting_form"; 
    }
    
    
    //결재 목록 조회 (페이징, 상태별 필터링 지원)
    @GetMapping("/approval_list")
    public String approvalList(
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            Model model, 
            Authentication authentication) { // 로그인 사용자 정보 추가
        
        log.info("결재 목록 조회 - 상태: {}, 페이지: {}", status, page);
        
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createAt"));
        
        // 내결재인 경우 현재 로그인 사용자 ID 전달
        String currentUserId = null;
        if ("my".equals(status) && authentication != null) {
            currentUserId = authentication.getName(); // 로그인한 사용자 ID
            log.info("내결재 조회 - 사용자: {}", currentUserId);
        }
        
        Page<ApprDTO> approvalPage = apprService.getApprovalList(pageable, status, currentUserId);
        
        // 모델에 페이징 정보 추가
        addPaginationAttributes(model, approvalPage, status);
        
        return "approval/approval_list";    
    }
    
    //결재 상세 정보 조회 API
    @GetMapping("/api/detail/{reqId}")
    @ResponseBody
    public ResponseEntity<ApprFullDTO> getApprovalDetail(@PathVariable("reqId") Long reqId) {
        log.info("결재 상세 조회 API 호출 - reqId: {}", reqId);
        
        try {
            ApprFullDTO detailDTO = apprService.getApprovalDetail(reqId);
            log.info("결재 상세 조회 성공 - reqId: {}", reqId);
            return ResponseEntity.ok(detailDTO); 
        } catch (Exception e) {
            log.error("결재 상세 조회 실패 - reqId: {}, 오류: {}", reqId, e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }
 	
    //승인 처리 API
    @PostMapping("/api/approve/{reqId}")
    @ResponseBody
    public ResponseEntity<String> approveRequest(@PathVariable("reqId") Long reqId,
                                                @RequestBody(required = false) Map<String, String> requestBody) {
        return processApproval(reqId, requestBody, "APPROVE");
    }
    
     //반려 처리 API
    @PostMapping("/api/reject/{reqId}")
    @ResponseBody 
    public ResponseEntity<String> rejectRequest(@PathVariable("reqId") Long reqId,
                                              @RequestBody(required = false) Map<String, String> requestBody) {
        return processApproval(reqId, requestBody, "REJECT");
    }
    
    // ==================== Private Helper Methods ====================
    
    //승인/반려 공통 처리 메서드
    private ResponseEntity<String> processApproval(Long reqId, Map<String, String> requestBody, String action) {
        log.info("{} 처리 API 호출 - reqId: {}", action, reqId);
        
        try {
            String comments = extractComments(requestBody);
            log.info("{} 사유: {}", action, comments);
            
            if ("APPROVE".equals(action)) {
                apprService.approveRequestWithComments(reqId, comments);
                log.info("승인 처리 완료 - reqId: {}", reqId);
                return ResponseEntity.ok("승인 처리가 완료되었습니다.");
            } else {
                apprService.rejectRequestWithComments(reqId, comments);
                log.info("반려 처리 완료 - reqId: {}", reqId);
                return ResponseEntity.ok("반려 처리가 완료되었습니다.");
            }
            
        } catch (Exception e) {
            log.error("{} 처리 실패 - reqId: {}, 오류: {}", action, reqId, e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(action.equals("APPROVE") ? "승인 처리 중 오류가 발생했습니다." : "반려 처리 중 오류가 발생했습니다.");
        }
    }
    
    
    //요청 본문에서 comments 추출
    private String extractComments(Map<String, String> requestBody) {
        if (requestBody != null && requestBody.containsKey("comments")) {
            return requestBody.get("comments");
        }
        return "";
    }
    
    //모델에 페이징 관련 속성 추가
    private void addPaginationAttributes(Model model, Page<ApprDTO> approvalPage, String status) {
        model.addAttribute("approvalList", approvalPage.getContent());
        model.addAttribute("currentPage", approvalPage.getNumber());
        model.addAttribute("totalPages", approvalPage.getTotalPages());
        model.addAttribute("totalElements", approvalPage.getTotalElements());
        model.addAttribute("hasPrevious", approvalPage.hasPrevious());
        model.addAttribute("hasNext", approvalPage.hasNext());
        model.addAttribute("currentStatus", status);
    }
}