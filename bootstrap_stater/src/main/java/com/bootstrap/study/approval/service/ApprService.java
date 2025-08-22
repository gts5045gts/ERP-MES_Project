package com.bootstrap.study.approval.service;

import com.bootstrap.study.approval.dto.ApprDTO;
import com.bootstrap.study.approval.dto.ApprFullDTO;
import com.bootstrap.study.approval.entity.Appr;
import com.bootstrap.study.approval.repository.ApprRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2; // Log4j2 임포트 추가!

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2 // 로그를 사용하기 위한 어노테이션 추가!
public class ApprService {

    private final ApprRepository apprRepository;
    // TODO: 직원 정보 조회를 위해 EmployeeRepository 등이 필요합니다.
    // private final EmployeeRepository employeeRepository;


    // 0820
	@Transactional(readOnly = true)
	public List<ApprDTO> getApprovalList() {
	// 네이티브 쿼리 실행
	List<Object[]> results = apprRepository.findApprovalListWithJoin();
	log.info(">>>>>> DB에서 조회된 결재 라인 건수: " + results.size() + "건");
	
	// Object[] 결과를 ApprDTO로 변환
	List<ApprDTO> apprDtoList = results.stream()
	        .map(result -> {
	            ApprDTO dto = new ApprDTO();
	            dto.setStepNo((Integer) result[0]); // step_no
	            dto.setTitle((String) result[1]); // title
	            dto.setDrafterName((String) result[2]); // emp_name
	            dto.setCreateAt(((java.sql.Timestamp) result[3]).toLocalDateTime()); // create_at
	            if (result[4] != null) {
	                dto.setDecDate(((java.sql.Timestamp) result[4]).toLocalDateTime()); // dec_date
	            }
	            dto.setDecision((String) result[5]); // decision
	            dto.setReqId(((Number) result[6]).longValue()); // req_id
	            dto.setReqType((String) result[7]); // req_type
	            dto.setEmpId((String) result[8]); // emp_id
	            dto.setCurrentStep((Integer) result[9]); // current_step
	            
	            // 임시 데이터
	            dto.setDepartment("인사부"); 
	            dto.setCurrentApprover("김이사");
	            
	            return dto;
	        })
	        .collect(Collectors.toList());
	
	log.info(">>>>>> DTO 변환 후 최종 반환 건수: " + apprDtoList.size() + "건");
	    return apprDtoList;
	}
    
    @Transactional(readOnly = true)
    public ApprFullDTO getApprovalDetail(Long reqId) {
        System.out.println(">>>>>> ApprService: 결재 상세 조회 - reqId: " + reqId);
        
        // 기존 방식 대신 네이티브 쿼리 결과에서 해당 reqId 찾기
        List<Object[]> results = apprRepository.findApprovalListWithJoin();
        
        // reqId가 일치하는 데이터 찾기
        for (Object[] result : results) {
            Long currentReqId = ((Number) result[6]).longValue(); // req_id는 result[6]
            
            if (currentReqId.equals(reqId)) {
                // 찾았으면 해당 데이터로 ApprFullDTO 생성
                ApprFullDTO dto = new ApprFullDTO();
                
                dto.setReqId(currentReqId);                                          // req_id
                dto.setTitle((String) result[1]);                                   // title  
                dto.setDrafterName((String) result[2]);                             // emp_name (실제 기안자명!)
                dto.setCreateAt(((java.sql.Timestamp) result[3]).toLocalDateTime()); // create_at
                dto.setReqType((String) result[7]);                                 // req_type
                dto.setEmpId((String) result[8]);                                   // emp_id
                
                // 부서명은 결재목록에서 가져오는 임시 데이터 사용
                dto.setDepartment("인사부"); 
                
                // content는 별도로 조회 (네이티브 쿼리에 없으므로)
                Appr appr = apprRepository.findById(reqId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 결재 문서를 찾을 수 없습니다. id=" + reqId));
                dto.setContent(appr.getContent());
                
                System.out.println(">>>>>> 실제 기안자명으로 설정: " + dto.getDrafterName());
                return dto;
            }
        }
        
        // 만약 찾지 못했다면 예외 발생
        throw new IllegalArgumentException("해당 결재 문서를 찾을 수 없습니다. id=" + reqId);
    }
    
    // 페이징처리@@@@@
    @Transactional(readOnly = true)
    public Page<ApprDTO> getApprovalList(Pageable pageable, String status) {
        System.out.println(">>> ApprService - 상태 필터: " + status);
        
        // 네이티브 쿼리로 전체 데이터 조회
        List<Object[]> allResults = apprRepository.findApprovalListWithJoin();
        
        // DTO로 변환
        List<ApprDTO> allDtoList = allResults.stream()
                .map(result -> {
                    ApprDTO dto = new ApprDTO();
                    dto.setStepNo((Integer) result[0]); 
                    dto.setTitle((String) result[1]); 
                    dto.setDrafterName((String) result[2]); 
                    dto.setCreateAt(((java.sql.Timestamp) result[3]).toLocalDateTime()); 
                    if (result[4] != null) {
                        dto.setDecDate(((java.sql.Timestamp) result[4]).toLocalDateTime()); 
                    }
                    dto.setDecision((String) result[5]); 
                    dto.setReqId(((Number) result[6]).longValue()); 
                    dto.setReqType((String) result[7]); 
                    dto.setEmpId((String) result[8]); 
                    dto.setCurrentStep((Integer) result[9]); 
                    dto.setDepartment("인사부"); 
                    dto.setCurrentApprover("김이사");
                    return dto;
                })
                .collect(Collectors.toList());
        
        // 상태별 필터링
        List<ApprDTO> filteredList;
        if ("pending".equals(status)) {
            filteredList = allDtoList.stream()
                    .filter(dto -> "대기".equals(dto.getStatusLabel()))
                    .collect(Collectors.toList());
        } else if ("completed".equals(status)) {
            filteredList = allDtoList.stream()
                    .filter(dto -> "승인".equals(dto.getStatusLabel()) || "반려".equals(dto.getStatusLabel()))
                    .collect(Collectors.toList());
        } else {
            filteredList = allDtoList; // all인 경우 전체
        }
        
        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredList.size());
        
        List<ApprDTO> pagedList = filteredList.subList(start, end);
        
        return new PageImpl<>(pagedList, pageable, filteredList.size());
    }
    
    //0821
   	// 승인버튼 누를시 승인처리되게 하기 (결재목록에서 대기 -> 승인으로 바뀜, 데이터도 반영)
    // ApprService.java - comments 포함한 승인/반려 메서드

    @Transactional
    public void approveRequestWithComments(Long reqId, String comments) {
        System.out.println(">>>>>> ApprService: 승인 처리 시작 - reqId: " + reqId + ", comments: " + comments);
        
        // comments가 null이면 빈 문자열로 처리
        String safeComments = (comments != null && !comments.trim().isEmpty()) ? comments.trim() : "";
        
        int updatedRows = apprRepository.updateApprovalLineDecision(reqId, "ACCEPT", safeComments);
        
        System.out.println(">>>>>> 업데이트된 행 수: " + updatedRows);
        
        if (updatedRows == 0) {
            throw new RuntimeException("승인 처리할 결재라인을 찾을 수 없습니다.");
        }
        
        System.out.println(">>>>>> 승인 처리 완료 - reqId: " + reqId);
    }

    @Transactional
    public void rejectRequestWithComments(Long reqId, String comments) {
        System.out.println(">>>>>> ApprService: 반려 처리 시작 - reqId: " + reqId + ", comments: " + comments);
        
        // comments가 null이면 빈 문자열로 처리
        String safeComments = (comments != null && !comments.trim().isEmpty()) ? comments.trim() : "";
        
        int updatedRows = apprRepository.updateApprovalLineDecision(reqId, "DENY", safeComments);
        
        System.out.println(">>>>>> 업데이트된 행 수: " + updatedRows);
        
        if (updatedRows == 0) {
            throw new RuntimeException("반려 처리할 결재라인을 찾을 수 없습니다.");
        }
        
        System.out.println(">>>>>> 반려 처리 완료 - reqId: " + reqId);
    }
}