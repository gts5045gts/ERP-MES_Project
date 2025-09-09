package com.erp_mes.erp.approval.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.erp.approval.constant.ApprDecision;
import com.erp_mes.erp.approval.constant.ApprReqType;
import com.erp_mes.erp.approval.constant.ApprStatus;
import com.erp_mes.erp.approval.dto.ApprDTO;
import com.erp_mes.erp.approval.dto.ApprDetailDTO;
import com.erp_mes.erp.approval.dto.ApprFullDTO;
import com.erp_mes.erp.approval.dto.ApprLineDTO;
import com.erp_mes.erp.approval.entity.Appr;
import com.erp_mes.erp.approval.entity.ApprDetail;
import com.erp_mes.erp.approval.entity.ApprLine;
import com.erp_mes.erp.approval.repository.ApprLineRepository;
import com.erp_mes.erp.approval.repository.ApprRepository;
import com.erp_mes.erp.attendance.entity.Annual;
import com.erp_mes.erp.personnel.dto.PersonnelDTO;
import com.erp_mes.erp.personnel.service.PersonnelTransferService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class ApprService {

	private final ApprRepository apprRepository;
	private final ApprLineService apprLineService;
	private final ApprLineRepository apprLineRepository;
	private final PersonnelTransferService personnelTransferService;
	
	// 상수 정의
	private static final String DEFAULT_DEPARTMENT = "인사부";
	private static final String DEFAULT_APPROVER = "김이사";
	private static final String PENDING_STATUS = "대기";
	private static final String APPROVED_STATUS = "승인";
	private static final String REJECTED_STATUS = "반려";
    
    // 결재 목록 조회 (페이징, 상태별 + 사용자별 필터링 지원)
    public Page<ApprDTO> getApprovalList(Pageable pageable, String status, String userId) {
	   log.info("결재 목록 조회 - 상태 필터: {}, 사용자 ID: {}", status, userId);
	   
	   if (userId == null) {
	       throw new IllegalArgumentException("로그인 정보가 필요합니다.");
	   }
	   
	   List<Object[]> allResults;
	   
	   if ("my".equals(status)) {	// 내가 기안한 문서만 조회
	       allResults = apprRepository.findMyDraftedApprovalList(userId);
	   } else {						// 내가 결재해야 할 문서만 조회 (내가 기안한 문서 제외)
	       allResults = apprLineRepository.findToApproveList(userId);
	   }
	   
	   List<ApprDTO> allDtoList = convertToApprDTOList(allResults);
	   
	   // 상태별 필터링
	   List<ApprDTO> filteredList;
	   if ("my".equals(status)) {
	       filteredList = allDtoList;  // 이미 내 기안 문서만 있으니 추가 필터링 불필요
	   } else {
	       filteredList = filterByStatus(allDtoList, status);
	   }
	   
	   return createPagedResult(filteredList, pageable);
	}

	// 결재 상세 정보 조회
	@Transactional(readOnly = true)
	public ApprFullDTO getApprovalDetail(Long reqId) {
	    log.info("결재 상세 조회 - reqId: {}", reqId);
	    
	    // 기본 결재 정보 조회
	    List<Object[]> results = apprRepository.findApprovalByReqId(reqId);
	    
	    if (!results.isEmpty()) {
	        Object[] result = results.get(0);
	        ApprFullDTO dto = convertToApprFullDTO(result);
	        
	        // content는 별도 조회
	        Appr appr = findApprovalById(reqId);
	        dto.setContent(appr.getContent());
	        
	        // 결재선 정보 조회 및 설정
	        List<Object[]> lineResults = apprLineRepository.findApprovalLinesByReqId(reqId);
	        List<ApprFullDTO.ApprLineInfo> approvalLines = lineResults.stream()
	            .map(this::convertToApprLineInfo)
	            .collect(Collectors.toList());
	        dto.setApprovalLines(approvalLines);
	        
	        return dto;
	    }
	    
	    throw new IllegalArgumentException("해당 결재 문서를 찾을 수 없습니다. id=" + reqId);
	}
    // 결재 상세 정보 조회
	private ApprFullDTO.ApprLineInfo convertToApprLineInfo(Object[] result) {
		ApprFullDTO.ApprLineInfo lineInfo = new ApprFullDTO.ApprLineInfo();
		lineInfo.setStepNo(((Number) result[0]).intValue());
		lineInfo.setApprId((String) result[1]);
		lineInfo.setApprName((String) result[2]);
		lineInfo.setDecision((String) result[3]);
		
		if (result[4] != null && result[4] instanceof java.sql.Timestamp) {
		    lineInfo.setDecDate(((java.sql.Timestamp) result[4]).toLocalDateTime());
		}
		
		lineInfo.setComments((String) result[5]);
		return lineInfo;
	}
    
	@Transactional  
	public void approveRequestWithComments(Long reqId, String comments, String loginId) {
		// 결재선에 ACCEPT 처리
		apprLineRepository.updateMyApprovalLine(reqId, loginId, "ACCEPT", comments);
		
		// 남은 결재자 수 확인
		int remainingCount = apprLineRepository.countRemainingApprovals(reqId);
		
		if (remainingCount == 0) {
		    // 모든 결재 완료 -> FINISHED
		    apprRepository.updateApprovalStatus(reqId, "FINISHED");
		} else {
		    // 아직 결재자 남음 -> PROCESSING
		    apprRepository.updateApprovalStatus(reqId, "PROCESSING");
		}
	}

    @Transactional
    public void rejectRequestWithComments(Long reqId, String comments, String loginId) {
        // 결재선에 DENY 처리
        int updatedRows = apprLineRepository.updateMyApprovalLine(reqId, loginId, "DENY", comments);
        
        // 문서 상태를 CANCELED로 변경 (한 명이라도 반려하면 즉시 취소)
        apprRepository.updateApprovalStatus(reqId, "CANCELED");
    }
    
    // ==================== Private 헬퍼 메서드 ====================
     
    //Object[] 배열을 ApprDTO 리스트로 변환
    private List<ApprDTO> convertToApprDTOList(List<Object[]> results) {
        return results.stream()
                .map(this::convertToApprDTO)
                .collect(Collectors.toList());
    }
    
    // DB 조회 결과(Object[])를 ApprDTO 객체로 매핑
    private ApprDTO convertToApprDTO(Object[] result) {
		ApprDTO dto = new ApprDTO();
		
		log.info("=== 쿼리 결과 raw 데이터 ===");
		for(int i = 0; i < result.length; i++) {
		    log.info("result[{}]: {}", i, result[i]);
		}
		
		// 부서, 직급 설정 전 로그
		log.info("부서명 raw: {}", result[3]);
		log.info("직급명 raw: {}", result[4]);
		dto.setStepNo(((Number) result[0]).intValue());
		dto.setTitle((String) result[1]);
		dto.setDrafterName((String) result[2]);
		dto.setDepartment(result[3] != null ? (String) result[3] : "-");  
		dto.setPosition(result[4] != null ? (String) result[4] : "-");    
		
		// REQUEST_AT 처리 - result[5]
		Object requestAtObj = result[5];
		if (requestAtObj == null) {
		    dto.setCreateAt(java.time.LocalDateTime.now());
		} else if (requestAtObj instanceof java.sql.Date) {
		    java.sql.Date sqlDate = (java.sql.Date) requestAtObj;
		    dto.setCreateAt(sqlDate.toLocalDate().atStartOfDay());
		} else if (requestAtObj instanceof java.sql.Timestamp) {
		    dto.setCreateAt(((java.sql.Timestamp) requestAtObj).toLocalDateTime());
		} else {
		    log.warn("알 수 없는 날짜 타입: {}, 현재 시간으로 대체", requestAtObj.getClass().getName());
		    dto.setCreateAt(java.time.LocalDateTime.now());
		}
		
		// DEC_DATE 처리
		if (result[6] != null) {
		    Object decDateObj = result[6];  
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
        
        dto.setDecision((String) result[7]);
        dto.setReqId(((Number) result[8]).longValue());
        dto.setReqType((String) result[9]);
        dto.setEmpId((String) result[10]);
        dto.setCurrentApprover("-");
        
        // 기안신청, 진행중, 완료 표시(ApprStatus)
        String decision = (String) result[7];
        if ("DENY".equals(decision)) {
            dto.setStatus(ApprStatus.FINISHED);
        } else if ("ACCEPT".equals(decision)) {
            // 모든 결재가 끝났는지는 별도 확인 필요
            dto.setStatus(ApprStatus.PROCESSING); 
        } else {
            dto.setStatus(ApprStatus.REQUESTED);
        }
        
        // STATUS 필드 추가
        if (result.length > 11 && result[11] != null) {
            String statusStr = (String) result[11];
            dto.setStatus(ApprStatus.valueOf(statusStr));
        }
        
        // 반려 여부 확인 
        if (result.length > 12 && result[12] != null) {
            String hasRejection = (String) result[12];
            dto.setHasRejection("DENY".equals(hasRejection));
        }
        
        return dto;
    }
    
    // 올린 결재 취소 처리
	@Transactional
	public void cancelApproval(Long reqId, String loginId) {
	    log.info("결재 취소 시도 - reqId: {}, loginId: {}", reqId, loginId);
	    
	    // 결재 문서 조회
	    Appr appr = apprRepository.findById(reqId)
	        .orElseThrow(() -> new IllegalArgumentException("결재를 찾을 수 없습니다."));
	    
	    log.info("문서 작성자: {}, 요청자: {}", appr.getEmpId(), loginId);
	    
	    // empId 비교 시 trim() 추가
	    if (!appr.getEmpId().trim().equals(loginId.trim())) {
	        log.error("권한 없음 - 문서작성자: [{}], 요청자: [{}]", appr.getEmpId(), loginId);
	        throw new SecurityException("본인이 작성한 결재만 취소할 수 있습니다.");
	    }
	    
	    // 대기 상태인지 확인
	    if (appr.getStatus() != ApprStatus.REQUESTED) {
	        log.error("상태 불일치 - 현재상태: {}", appr.getStatus());
	        throw new IllegalArgumentException("대기 상태의 결재만 취소할 수 있습니다.");
	    }
	    
	    // DELETE 처리
	    apprRepository.deleteById(reqId);
	    log.info("결재 삭제 완료 - reqId: {}", reqId);
	}
	
    // 결재대기 알림
    public int getMyPendingCount(String loginId) {
        // 내가 올린 대기 상태 결재 건수
        return apprRepository.countMyPendingApprovals(loginId);
    }
    
    // 결재대기 알림
    public int getToApproveCount(String loginId) {
        // 내가 결재해야 할 대기 건수
        return apprLineRepository.countMyPendingApprovals(loginId);
    }
    
    // 결재대기 알림
    public String getMyApprovalStatusSummary(String loginId) {
        // 내 결재의 각 상태별 건수를 문자열로 반환 (해시값처럼 사용)
        int requested = apprRepository.countMyApprovalsByStatus(loginId, "REQUESTED");
        int processing = apprRepository.countMyApprovalsByStatus(loginId, "PROCESSING");
        int finished = apprRepository.countMyApprovalsByStatus(loginId, "FINISHED");
        int canceled = apprRepository.countMyApprovalsByStatus(loginId, "CANCELED");
        
        return String.format("%d-%d-%d-%d", requested, processing, finished, canceled);
    }
    
    // Object[] 배열을 ApprFullDTO로 변환 - ORACLE TIMESTAMPTZ 타입 처리 추가
    private ApprFullDTO convertToApprFullDTO(Object[] result) {
        ApprFullDTO dto = new ApprFullDTO();
        dto.setReqId(((Number) result[8]).longValue());  // 인덱스 8로 수정
        dto.setTitle((String) result[1]);
        dto.setDrafterName((String) result[2]);
        dto.setDepartment(result[3] != null ? (String) result[3] : "-");  // 실제 부서명
        
        // REQUEST_AT 처리 - result[5]로 수정
        Object requestAtObj = result[5];
        if (requestAtObj != null) {
            if (requestAtObj instanceof java.sql.Date) {
                dto.setCreateAt(((java.sql.Date) requestAtObj).toLocalDate().atStartOfDay());
            } else if (requestAtObj instanceof java.sql.Timestamp) {
                dto.setCreateAt(((java.sql.Timestamp) requestAtObj).toLocalDateTime());
            }
        }
        
        dto.setReqType((String) result[9]);
        dto.setEmpId((String) result[10]);
        
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
	// 0901 삭제
	@Transactional
	public void deleteSelected(List<Long> ids, String loginId) {
	    List<String> errors = new ArrayList<>();
	    int successCount = 0;
	    
	    for (Long id : ids) {
	        try {
	            Appr appr = apprRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("결재를 찾을 수 없습니다"));
	            
	            // 권한 확인: 1) 기안자이거나 2) 결재자인 경우
	            boolean isAuthor = appr.getEmpId().equals(loginId);
	            boolean isApprover = isUserApprover(id, loginId);
	            
	            if (!isAuthor && !isApprover) {
	                errors.add("ID " + id + ": 삭제 권한이 없습니다");
	                continue;
	            }
	            
	            // 기안자의 경우 진행중인 결재는 삭제 불가 (취소 후 삭제)
	            if (isAuthor && (appr.getStatus() == ApprStatus.REQUESTED || 
	                           appr.getStatus() == ApprStatus.PROCESSING)) {
	                errors.add("ID " + id + ": 진행중인 결재는 취소 후 삭제하세요");
	                continue;
	            }
	            
	            apprRepository.deleteById(id);
	            successCount++;
	            
	        } catch (Exception e) {
	            errors.add("ID " + id + ": 삭제 실패 - " + e.getMessage());
	        }
	    }
	    
	    // 결과 메시지
	    String resultMessage = successCount + "건 삭제 완료";
	    if (!errors.isEmpty()) {
	        resultMessage += "\n실패한 항목:\n" + String.join("\n", errors);
	        throw new RuntimeException(resultMessage);
	    }
	}
	
	// 0901 삭제, 사용자가 해당 결재의 결재자인지 확인
	private boolean isUserApprover(Long reqId, String loginId) {
	    // approval_line 테이블에서 해당 사용자가 결재자인지 확인
	    try {
	        List<Object[]> approvers = apprLineRepository.findApprovalLinesByReqId(reqId);
	        return approvers.stream()
	                .anyMatch(line -> loginId.equals(line[1])); // appr_id 컬럼
	    } catch (Exception e) {
	        return false;
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
		    
		    if (empId.equals(loginEmpId)) {
				line.setDecDate(LocalDateTime.now());
				line.setDecision(ApprDecision.ACCEPT);
			}
		    line.setApprId(empId);
		    line.setStepNo(index++);
		    appr.addLine(line);  // Appr이 직접 관리
		}
		
		// 인사발령은 ApprDetail에 관여하지 않기 때문에 null 체크를 통해 이 부분을 건너뛰도록 if문 추가
		if (apprDTO.getApprDetailDTOList() != null) {
			for (ApprDetailDTO dto : apprDTO.getApprDetailDTOList()) {
				ApprDetail detail = new ApprDetail();
				detail.setVacType(dto.getVacType());
				detail.setStartDate(dto.getStartDate());
				detail.setEndDate(dto.getEndDate());
				detail.setHalfType(dto.getHalfType());
				appr.addDetail(detail);  // 연관관계 메서드
			}
		}
		
		apprRepository.save(appr);
		
		return appr.getReqId();
	}

    //연차 정보 조회
	public Annual getAnnualInfo(String empId) {
		Annual annual = apprRepository.findByAnnual(empId, LocalDate.now().getYear())
				.orElseThrow(() -> new EntityNotFoundException(empId + " : 연차정보조회실패!"));
		
		return annual;
	}
	
	@Transactional
	public void processApproval(Long reqId, String apprId, ApprDecision decision, String comments) {
		ApprLine apprLine = apprLineRepository.findByApprReqIdAndApprId(reqId, apprId)
				.orElseThrow(() -> new IllegalArgumentException("결재 라인 정보를 찾을 수 없습니다."));
		
		// 현재 결재 단계 처리
		apprLine.setDecision(decision);
		apprLine.setDecDate(LocalDateTime.now());
		apprLine.setComments(comments);
		apprLineRepository.save(apprLine);

		Appr appr = apprLine.getAppr();
		int currentStep = apprLine.getStepNo();
		int totalStep = appr.getTotStep();

		if (decision == ApprDecision.ACCEPT) {
			if (currentStep == totalStep) {
				// 최종 승인
				appr.setStatus(ApprStatus.FINISHED);
				apprRepository.save(appr);

				// 인사 발령 문서인 경우
				if ("TRANSFER".equals(appr.getReqType())) {
					personnelTransferService.processPersonnelTransfer(reqId);
				}
			}
		} else if (decision == ApprDecision.DENY) {
			appr.setStatus(ApprStatus.CANCELED);
			appr.setHasRejection(true);
			apprRepository.save(appr);
		}
	}
	
}