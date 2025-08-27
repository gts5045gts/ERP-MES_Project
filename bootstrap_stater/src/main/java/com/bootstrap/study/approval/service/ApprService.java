package com.bootstrap.study.approval.service;

import com.bootstrap.study.approval.constant.ApprReqType;
import com.bootstrap.study.approval.dto.ApprDTO;
import com.bootstrap.study.approval.dto.ApprDetailDTO;
import com.bootstrap.study.approval.dto.ApprFullDTO;
import com.bootstrap.study.approval.dto.ApprLineDTO;
import com.bootstrap.study.approval.entity.Appr;
import com.bootstrap.study.approval.entity.ApprDetail;
import com.bootstrap.study.approval.entity.ApprLine;
import com.bootstrap.study.approval.repository.ApprRepository;
import com.bootstrap.study.personnel.dto.PersonnelDTO;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class ApprService {

	private final ApprRepository apprRepository;
	private final ApprLineService apprLineService;
	
    // 상수 정의
    private static final String DEFAULT_DEPARTMENT = "인사부";
    private static final String DEFAULT_APPROVER = "김이사";
    private static final String PENDING_STATUS = "대기";
    private static final String APPROVED_STATUS = "승인";
    private static final String REJECTED_STATUS = "반려";
    
    
    // 결재 목록 조회 (페이징 없음)
    @Transactional(readOnly = true)
    public List<ApprDTO> getApprovalList() {
        List<Object[]> results = apprRepository.findApprovalListWithJoin();
        log.info("DB에서 조회된 결재 라인 건수: {}건", results.size());
        
        List<ApprDTO> apprDtoList = convertToApprDTOList(results);
        
        log.info("DTO 변환 후 최종 반환 건수: {}건", apprDtoList.size());
        return apprDtoList;
    }
    
    
    // 결재 목록 조회 (페이징, 상태별 필터링 지원) - 기존 메소드
    @Transactional(readOnly = true)
    public Page<ApprDTO> getApprovalList(Pageable pageable, String status) {
        return getApprovalList(pageable, status, null); // userId를 null로 전달
    }
    
    // 결재 목록 조회 (페이징, 상태별 + 사용자별 필터링 지원)
    @Transactional(readOnly = true)
    public Page<ApprDTO> getApprovalList(Pageable pageable, String status, String userId) {
        log.info("결재 목록 조회 - 상태 필터: {}, 사용자 ID: {}", status, userId);
        
        // 전체 데이터 조회 및 DTO 변환
        List<Object[]> allResults = apprRepository.findApprovalListWithJoin();
        List<ApprDTO> allDtoList = convertToApprDTOList(allResults);
        
        // 상태별 + 사용자별 필터링
        List<ApprDTO> filteredList = filterByStatusAndUser(allDtoList, status, userId);
        
        // 페이징 처리
        return createPagedResult(filteredList, pageable);
    }
    
    //결재 상세 정보 조회
    @Transactional(readOnly = true)
    public ApprFullDTO getApprovalDetail(Long reqId) {
        log.info("결재 상세 조회 - reqId: {}", reqId);
        
        List<Object[]> results = apprRepository.findApprovalListWithJoin();
        
        // reqId와 일치하는 데이터 찾기
        for (Object[] result : results) {
            Long currentReqId = ((Number) result[6]).longValue();
            
            if (currentReqId.equals(reqId)) {
                ApprFullDTO dto = convertToApprFullDTO(result);
                
                // content는 별도 조회
                Appr appr = findApprovalById(reqId);
                dto.setContent(appr.getContent());
                
                log.info("결재 상세 조회 완료 - 기안자: {}", dto.getDrafterName());
                return dto;
            }
        }
        
        throw new IllegalArgumentException("해당 결재 문서를 찾을 수 없습니다. id=" + reqId);
    }
    // 0826
    //승인 처리 (코멘트 포함)
    @Transactional
    public void approveRequestWithComments(Long reqId, String comments) {
        log.info("승인 처리 시작 - reqId: {}, comments: {}", reqId, comments);
        
        // 현재 단계 승인 처리
        processApprovalDecision(reqId, comments, "ACCEPT", "승인");
        
        // 남은 대기 결재가 있는지 확인
        int pendingCount = apprRepository.countPendingApprovals(reqId);
        
        if (pendingCount == 0) {
            // 모든 결재 완료 -> FINISHED로 변경
            log.info("모든 결재 완료 - 문서 상태를 FINISHED로 변경: reqId={}", reqId);
            apprRepository.updateApprovalStatus(reqId, "FINISHED");
        }
        
        log.info("승인 처리 완료 - reqId: {}", reqId);
    }
    
    //반려 처리 (코멘트 포함)
    @Transactional
    public void rejectRequestWithComments(Long reqId, String comments) {
        log.info("반려 처리 시작 - reqId: {}, comments: {}", reqId, comments);
        
        processApprovalDecision(reqId, comments, "DENY", "반려");
        
        // 반려되면 즉시 FINISHED로
        log.info("반려로 인한 결재 종료 - 문서 상태를 FINISHED로 변경: reqId={}", reqId);
        apprRepository.updateApprovalStatus(reqId, "FINISHED");
        
        log.info("반려 처리 완료 - reqId: {}", reqId);
    }
    
    // ==================== Private 헬퍼 메서드 ====================
     
    //Object[] 배열을 ApprDTO 리스트로 변환
    private List<ApprDTO> convertToApprDTOList(List<Object[]> results) {
        return results.stream()
                .map(this::convertToApprDTO)
                .collect(Collectors.toList());
    }
    
    
    // Object[] 배열을 ApprDTO로 변환 - ORACLE TIMESTAMPTZ 타입 처리 추가
    private ApprDTO convertToApprDTO(Object[] result) {
        ApprDTO dto = new ApprDTO();
        dto.setStepNo((Integer) result[0]);
        dto.setTitle((String) result[1]);
        dto.setDrafterName((String) result[2]);
        
        // REQUEST_AT 처리 - result[3]
        Object requestAtObj = result[3];
        if (requestAtObj == null) {
            dto.setCreateAt(java.time.LocalDateTime.now());
        } else if (requestAtObj instanceof java.sql.Date) {
            // DATE 타입을 LocalDateTime으로 변환
            java.sql.Date sqlDate = (java.sql.Date) requestAtObj;
            dto.setCreateAt(sqlDate.toLocalDate().atStartOfDay());
        } else if (requestAtObj instanceof java.sql.Timestamp) {
            dto.setCreateAt(((java.sql.Timestamp) requestAtObj).toLocalDateTime());
        } else {
            log.warn("알 수 없는 날짜 타입: {}, 현재 시간으로 대체", requestAtObj.getClass().getName());
            dto.setCreateAt(java.time.LocalDateTime.now());
        }
        
        // DEC_DATE 처리
        if (result[4] != null) {
            Object decDateObj = result[4];
            if (decDateObj instanceof oracle.sql.TIMESTAMPTZ) {
                try {
                    oracle.sql.TIMESTAMPTZ timestamptz = (oracle.sql.TIMESTAMPTZ) decDateObj;
                    dto.setDecDate(timestamptz.timestampValue().toLocalDateTime());
                } catch (Exception e) {
                    log.warn("DEC_DATE TIMESTAMPTZ 변환 실패: {}", e.getMessage());
                    dto.setDecDate(null);
                }
            } else if (decDateObj instanceof java.sql.Timestamp) {
                dto.setDecDate(((java.sql.Timestamp) decDateObj).toLocalDateTime());
            }
        }
        
        dto.setDecision((String) result[5]);
        dto.setReqId(((Number) result[6]).longValue());
//        dto.setReqType(ApprReqType.valueOf((String) result[7]));
        dto.setReqType((String) result[7]);
        dto.setEmpId((String) result[8]);
        
        // 임시 데이터 설정
        dto.setDepartment(DEFAULT_DEPARTMENT);
        dto.setCurrentApprover(DEFAULT_APPROVER);
        
        return dto;
    }
    
    
    // Object[] 배열을 ApprFullDTO로 변환 - ORACLE TIMESTAMPTZ 타입 처리 추가
    private ApprFullDTO convertToApprFullDTO(Object[] result) {
        ApprFullDTO dto = new ApprFullDTO();
        dto.setReqId(((Number) result[6]).longValue());
        dto.setTitle((String) result[1]);
        dto.setDrafterName((String) result[2]);
        
        // REQUEST_AT 처리 - result[3]
        Object requestAtObj = result[3];
        if (requestAtObj == null) {
            dto.setCreateAt(java.time.LocalDateTime.now());
        } else if (requestAtObj instanceof java.sql.Date) {
            // DATE 타입을 LocalDateTime으로 변환
            java.sql.Date sqlDate = (java.sql.Date) requestAtObj;
            dto.setCreateAt(sqlDate.toLocalDate().atStartOfDay());
        } else if (requestAtObj instanceof java.sql.Timestamp) {
            dto.setCreateAt(((java.sql.Timestamp) requestAtObj).toLocalDateTime());
        } else {
            log.warn("ApprFullDTO 알 수 없는 날짜 타입: {}, 현재 시간으로 대체", requestAtObj.getClass().getName());
            dto.setCreateAt(java.time.LocalDateTime.now());
        }
        
        dto.setReqType((String) result[7]);
        dto.setEmpId((String) result[8]);
        dto.setDepartment(DEFAULT_DEPARTMENT);
        
        return dto;
    }
    
    //상태별 필터링 (기존 메소드 - 호환성 유지)
    private List<ApprDTO> filterByStatus(List<ApprDTO> allDtoList, String status) {
        switch (status) {
            case "pending":
                return allDtoList.stream()
                        .filter(dto -> PENDING_STATUS.equals(dto.getStatusLabel()))
                        .collect(Collectors.toList());
            case "completed":
                return allDtoList.stream()
                        .filter(dto -> APPROVED_STATUS.equals(dto.getStatusLabel()) 
                                    || REJECTED_STATUS.equals(dto.getStatusLabel()))
                        .collect(Collectors.toList());
            default:
                return allDtoList; // "all" 또는 기타
        }
    }
    
    // 상태별 + 사용자별 필터링
    private List<ApprDTO> filterByStatusAndUser(List<ApprDTO> allDtoList, String status, String userId) {
        // 사용자별 필터링 먼저 적용 (내결재인 경우)
        List<ApprDTO> userFilteredList = allDtoList;
        if (userId != null) {
            userFilteredList = allDtoList.stream()
                    .filter(dto -> userId.equals(dto.getEmpId()))
                    .collect(Collectors.toList());
            log.info("내결재 필터링 적용 - 사용자: {}, 필터링 후 건수: {}", userId, userFilteredList.size());
        }
        
        // 상태별 필터링 적용
        switch (status) {
            case "pending":
                return userFilteredList.stream()
                        .filter(dto -> PENDING_STATUS.equals(dto.getStatusLabel()))
                        .collect(Collectors.toList());
            case "completed":
                return userFilteredList.stream()
                        .filter(dto -> APPROVED_STATUS.equals(dto.getStatusLabel()) 
                                    || REJECTED_STATUS.equals(dto.getStatusLabel()))
                        .collect(Collectors.toList());
            case "my":
                // 내결재인 경우는 이미 사용자 필터링이 적용되어 있으므로 전체 반환
                return userFilteredList;
            default: // "all"
                return userFilteredList;
        }
    }
    
    //페이징 결과 생성
    private Page<ApprDTO> createPagedResult(List<ApprDTO> filteredList, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredList.size());
        
        List<ApprDTO> pagedList = filteredList.subList(start, end);
        return new PageImpl<>(pagedList, pageable, filteredList.size());
    }
    
    //승인/반려 공통 처리 로직
    private void processApprovalDecision(Long reqId, String comments, String decision, String actionName) {
        String safeComments = sanitizeComments(comments);
        
        int updatedRows = apprRepository.updateApprovalLineDecision(reqId, decision, safeComments);
        
        log.info("업데이트된 행 수: {}", updatedRows);
        
        if (updatedRows == 0) {
            throw new RuntimeException(actionName + " 처리할 결재라인을 찾을 수 없습니다.");
        }
    }
    
    //코멘트 안전 처리 (null 및 공백 처리)
    private String sanitizeComments(String comments) {
        return (comments != null && !comments.trim().isEmpty()) ? comments.trim() : "";
    }
    
    //결재 문서 조회 (예외 처리 포함)
    private Appr findApprovalById(Long reqId) {
        return apprRepository.findById(reqId)
                .orElseThrow(() -> new IllegalArgumentException("해당 결재 문서를 찾을 수 없습니다. id=" + reqId));
    }

    //결재자 리스트 조회
    @Transactional(readOnly = true)
    public List<PersonnelDTO> getApprEmployee(String keyword, String currentEmpId) {
    	log.info("currentEmpId>>>>>>>>>>>>>>>>>>>>>>>>>"+currentEmpId);
        return apprRepository.findByNameContainingIgnoreCase(keyword, currentEmpId)
                .stream()
                .map(PersonnelDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Long registAppr(@Valid ApprDTO apprDTO, String[] empIds, String loginEmpId) throws IOException{
		
		Appr appr = apprDTO.toEntity();
		
		appr.setEmpId(loginEmpId);
		appr.setTotStep(empIds.length);
		
		int index = 1;
		for (String empId : empIds) {
		    ApprLine line = new ApprLine();
		    line.setApprId(empId);
		    line.setStepNo(index++);
		    appr.addLine(line);  // Appr이 직접 관리
		}
		
		for (ApprDetailDTO dto : apprDTO.getApprDetailDTOList()) {
		    ApprDetail detail = new ApprDetail();
		    detail.setVacType(dto.getVacType());
		    detail.setStartDate(dto.getStartDate());
		    detail.setEndDate(dto.getEndDate());
		    detail.setHalfType(dto.getHalfType());
		    appr.addDetail(detail);  // 연관관계 메서드
		}
		
		apprRepository.save(appr);
		
//		apprLineService.registApprLine(appr, empIds);
				
		return appr.getReqId();
	}
	
}