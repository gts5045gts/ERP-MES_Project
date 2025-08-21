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


    // ㅇㅇ
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
        // ID로 Appr 엔티티를 찾고, 없으면 예외 발생
        Appr appr = apprRepository.findById(reqId)
                .orElseThrow(() -> new IllegalArgumentException("해당 결재 문서를 찾을 수 없습니다. id=" + reqId));
        
        // Entity를 ApprFullDTO로 변환하여 반환
        return ApprFullDTO.fromEntity(appr);
    }
    
    // 페이징처리@@@@@
    @Transactional(readOnly = true)
    public Page<ApprDTO> getApprovalList(Pageable pageable) {
        System.out.println(">>> ApprService 페이징 처리 - 페이지: " + pageable.getPageNumber() + ", 사이즈: " + pageable.getPageSize());
        
        // 네이티브 쿼리로 전체 데이터 조회
        List<Object[]> allResults = apprRepository.findApprovalListWithJoin();
        System.out.println(">>> 전체 데이터 건수: " + allResults.size());
        
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
        
        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allDtoList.size());
        
        List<ApprDTO> pagedList = allDtoList.subList(start, end);
        System.out.println(">>> 현재 페이지 데이터 건수: " + pagedList.size());
        
        return new PageImpl<>(pagedList, pageable, allDtoList.size());
    }
}