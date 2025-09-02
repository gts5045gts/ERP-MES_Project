package com.bootstrap.study.approval.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootstrap.study.approval.constant.ApprDecision;
import com.bootstrap.study.approval.constant.ApprReqType;
import com.bootstrap.study.approval.dto.ApprDTO;
import com.bootstrap.study.approval.dto.ApprFullDTO;
import com.bootstrap.study.approval.service.ApprService;
import com.bootstrap.study.attendance.entity.Annual;
import com.bootstrap.study.personnel.dto.PersonnelDTO;
import com.bootstrap.study.personnel.dto.PersonnelLoginDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

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

    @GetMapping("/new/{reqTypeVal}")
    public String draftingForm(@PathVariable("reqTypeVal") String reqTypeVal, Model model, Authentication authentication){
    	
    	//사원정보
    	PersonnelLoginDTO principal = (PersonnelLoginDTO) authentication.getPrincipal();
    	String loginEmpId = principal.getEmpId();
    	//연차 정보
    	Annual annual = apprService.getAnnualInfo(loginEmpId);
    	double remain = annual.getAnnTotal() - annual.getAnnUse();
    	//문서정보
    	ApprReqType reqType = ApprReqType.fromName(reqTypeVal);
    	String title = reqType.getLabel();
    	    	
    	model.addAttribute("apprDTO", new ApprDTO());
    	model.addAttribute("selectedRole", reqTypeVal); // 기본 선택값
    	model.addAttribute("loginEmpId", loginEmpId);
    	model.addAttribute("principal", principal);
    	model.addAttribute("remain", remain);
    	model.addAttribute("title", title);
    	        
        return "approval/drafting_form";
    }
    
    @GetMapping("/showDraftingForm") 
    public String showMyForm() {
        log.info("단순 폼 보여주기 요청");
        return "approval/drafting_form"; 
    }
    
    
    // 0827 결재 목록 조회 (페이징, 상태별 필터링 지원)
    @GetMapping("/approval_list")
    public String approvalList(
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            Model model, 
            Authentication authentication) {
        
        log.info("결재 목록 조회 - 상태: {}, 페이지: {}", status, page);
        
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createAt"));
        
        // 항상 로그인 사용자 ID 전달 (내 결재만 보기)
        String currentUserId = authentication.getName();
        log.info("로그인 사용자: {}", currentUserId);
        
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
 	
    // 0827 승인 처리 API
    @PostMapping("/api/approve/{reqId}")
    @ResponseBody
    public ResponseEntity<String> approveRequest(@PathVariable("reqId") Long reqId,
                                                 @RequestBody(required = false) Map<String, String> requestBody,Authentication authentication) { 
                                                  
        return processApproval(reqId, requestBody, "APPROVE", authentication);  
    }

    
    // 0827 반려 처리 API
    @PostMapping("/api/reject/{reqId}")
    @ResponseBody 
    public ResponseEntity<String> rejectRequest(@PathVariable("reqId") Long reqId,
                                                @RequestBody(required = false) Map<String, String> requestBody, Authentication authentication) { 
        return processApproval(reqId, requestBody, "REJECT", authentication);  
    }
    
    // ==================== Private Helper Methods ====================
    
    // 0827 승인/반려 공통 처리 메서드
    private ResponseEntity<String> processApproval(Long reqId, Map<String, String> requestBody, String action, Authentication authentication) {  // authentication 추가
        log.info("{} 처리 API 호출 - reqId: {}", action, reqId);
        
        try {
            String comments = extractComments(requestBody);
            String loginId = authentication.getName();  // 로그인 ID 가져오기
            log.info("{} 사유: {}, 로그인ID: {}", action, comments, loginId);
            
            if ("APPROVE".equals(action)) {
            	apprService.processApproval(reqId, loginId, ApprDecision.ACCEPT, comments);
//                apprService.approveRequestWithComments(reqId, comments, loginId);  // loginId 추가
                log.info("승인 처리 완료 - reqId: {}", reqId);
                return ResponseEntity.ok("승인 처리가 완료되었습니다.");
            } else {
            	apprService.processApproval(reqId, loginId, ApprDecision.DENY, comments);
//                apprService.rejectRequestWithComments(reqId, comments, loginId);  // loginId 추가
                log.info("반려 처리 완료 - reqId: {}", reqId);
                return ResponseEntity.ok("반려 처리가 완료되었습니다.");
            }
            
        } catch (Exception e) {
            log.error("{} 처리 실패 - reqId: {}, 오류: {}", action, reqId, e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(action.equals("APPROVE") ? "승인 처리 중 오류가 발생했습니다." : "반려 처리 중 오류가 발생했습니다.");
        }
    }
    
    // 0828 - 결재 취소 처리 API
    @PostMapping("/api/cancel/{reqId}")
    @ResponseBody
    public ResponseEntity<String> cancelRequest(@PathVariable("reqId") Long reqId, 
                                               Authentication authentication) {
        try {
            String loginId = authentication.getName();
            
            // Service를 통해 취소 처리
            apprService.cancelApproval(reqId, loginId);
            
            return ResponseEntity.ok("결재가 취소되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            log.error("결재 취소 실패 - reqId: {}, 오류: {}", reqId, e.getMessage());
            return ResponseEntity.status(500).body("결재 취소 중 오류가 발생했습니다.");
        }
    }
    // 0828 알림창
    @GetMapping("/api/counts")
    @ResponseBody
    public Map<String, Object> getApprovalCounts(Authentication authentication) {
        String loginId = authentication.getName();
        Map<String, Object> result = new HashMap<>();
        
        // 내결재목록 대기 건수
        result.put("myPending", apprService.getMyPendingCount(loginId));
        
        // 결재대기 건수
        result.put("toApprove", apprService.getToApproveCount(loginId));
        
        // 내결재목록 전체 상태 정보 (상태 변화 감지용)
        result.put("myApprovalStatus", apprService.getMyApprovalStatusSummary(loginId));
        
        return result;
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
    
    //결재자 검색
    @GetMapping("/empSearch")
    @ResponseBody
    public List<PersonnelDTO> searchUser(@RequestParam("name") String name, Authentication authentication) {
    	
    	String loginEmpId = authentication.getName();
    	
        return apprService.getApprEmployee(name, loginEmpId);
    }
 	
    // 0826 - 기존 하드코딩된 "2025082501" 대신 실제 로그인 사용자 정보 사용
    @PostMapping("/save")
    @ResponseBody
    public String registAppr(@ModelAttribute("apprDTO") @Valid ApprDTO apprDTO, 
                            @RequestParam("empIds") String[] empIds, 
                            BindingResult bindingResult, 
                            Model model,
                            Authentication authentication) throws IOException {  // Authentication 추가

        if (bindingResult.hasErrors()) {
            String reqType = apprDTO.getReqType();
            return "approval/new/" + reqType;
        }

        if (apprDTO.getApprDetailDTOList() == null) {
            apprDTO.setApprDetailDTOList(new ArrayList<>());
        }

        // 로그인한 사용자 ID 가져와서 Service에 전달
        String loginEmpId = authentication.getName();
        Long apprId = apprService.registAppr(apprDTO, empIds, loginEmpId);  // 3개 파라미터

        //결재 리스트로 이동되게 변경해야함.
        return "<script>" +
                "alert('신청 완료되었습니다.');" +
                "window.close();" +
                "</script>";
    }
}
