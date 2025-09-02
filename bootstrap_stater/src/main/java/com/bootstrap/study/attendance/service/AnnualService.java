package com.bootstrap.study.attendance.service;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bootstrap.study.approval.constant.ApprHalfType;
import com.bootstrap.study.approval.constant.ApprStatus;
import com.bootstrap.study.approval.constant.ApprVacType;
import com.bootstrap.study.approval.entity.Appr;
import com.bootstrap.study.approval.entity.ApprDetail;
import com.bootstrap.study.approval.repository.ApprDetailRepository;
import com.bootstrap.study.approval.repository.ApprRepository;
import com.bootstrap.study.attendance.dto.AnnualDTO;
import com.bootstrap.study.attendance.entity.Annual;
import com.bootstrap.study.attendance.repository.AnnualRepository;
import com.bootstrap.study.commonCode.entity.CommonDetailCode;
import com.bootstrap.study.personnel.entity.Personnel;
import com.bootstrap.study.personnel.repository.PersonnelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class AnnualService {


	private final AnnualRepository annRepository;
	private final PersonnelRepository empRepository;
	private final ApprRepository apprRepository;
	private final ApprDetailRepository apprDtRepository;

// ====================================================================	
// 공통부분 메서드로 추출
	
	// 특정 사원의 특정 연도 연차 가져오기
	private Annual getOrCreateAnnual(String empId, String annYear) {
		
		Personnel emp = empRepository.findById(empId)
	            .orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));
		
		return annRepository.findByEmpIdAndAnnYear(empId, annYear)
				.orElseGet(() -> {
					int totalAnnual = getAnnByPosition(emp.getPosition());
					Annual newAnn = new Annual(empId, annYear, 0.0, totalAnnual);
					annRepository.save(newAnn);
					return newAnn;
		});
	}
	
	// int year 버전 (위 메서드로 위임)
	private Annual getOrCreateAnnual(String empId, int year) {
	    return getOrCreateAnnual(empId, String.valueOf(year));
	}
	
	// 사원 조회
	private Personnel getPersonnel(String empId) {
	    return empRepository.findById(empId)
	            .orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));
	}
	
	// 반차 종류 화면에 표시
	private String halfType(ApprHalfType halfType) {
	    return switch (halfType) {
	    	case STARTMORNING, ENDMORNING -> "오전반차";
	    	case STARTAFTERNOON, ENDAFTERNOON -> "오후반차";
	    };
	}
	

// =================================================================================
	
	
	// 내 연차 조회
	public AnnualDTO myAnnual(String empId, String annYear) {
		
		Personnel emp = getPersonnel(empId);
	    Annual ann = getOrCreateAnnual(empId, annYear);

		// 직급별 연차 자동 적용 (갱신)
	    ann.setAnnTotal((double)getAnnByPosition(emp.getPosition()));
	    
	    annRepository.save(ann);
	   
	    return new AnnualDTO(ann, emp);
		
	}

	// 직급별 연차 계산
	private int getAnnByPosition(CommonDetailCode position) {
		switch (position.getComDtId()) {
			case "POS001": return 15;
			case "POS002" : case "POS003" : case "POS004" : return 20;
			case "POS005" : case "POS006" : return 25;
			default: return 15;
		}
		
	}
	

	
	// 사원의 권한별 연차 조회 + 무한스크롤
	@Transactional
	public Page<AnnualDTO> getAllAnnByYearPaged(String empId, String annYear, Pageable pageable) {
	    Personnel loginUser = getPersonnel(empId);
	    String roleCode = loginUser.getLevel().getComDtId();
	    String deptCode = loginUser.getDepartment().getComDtId();

	    Page<Annual> annPage;
	    switch (roleCode) {
	        case "AUT001": // 상위 관리자
	            annPage = annRepository.findByAnnYear(annYear, pageable);
	            break;
	        case "AUT002": // 중간 관리자
	            annPage = annRepository.findByAnnYearAndDepNm(annYear, deptCode, pageable); 
	            break;
	        case "AUT003": // 일반 사원
	            annPage = annRepository.findByAnnYearAndEmpId(annYear, empId, pageable);
	            break;
	        default:
	        	throw new RuntimeException("알 수 없는 권한 코드: " + roleCode);
	    }

	    List<AnnualDTO> dtoList = annPage.stream().map(ann -> {
	        Personnel emp = getPersonnel(ann.getEmpId());
	        ann.setAnnRemain(ann.getAnnTotal() - ann.getAnnUse());
	        return new AnnualDTO(ann, emp);
	    }).collect(Collectors.toList());

	    // 초기 연차 없는 사원 처리
	    List<String> existingEmps = dtoList.stream().map(AnnualDTO::getEmpId).toList();
	    List<Personnel> updateEmps = empRepository.findAll().stream()
	            .filter(emp -> !existingEmps.contains(emp.getEmpId()))
	            .toList();

	    for(Personnel emp : updateEmps) {
	        // 중간관리자는 부서 필터링
	        if ("AUT002".equals(roleCode) && !deptCode.equals(emp.getDepartment().getComDtId())) continue;
	        // 일반 사원는 자기 자신만
	        if ("AUT003".equals(roleCode) && !empId.equals(emp.getEmpId())) continue;

	        int totalAnn = getAnnByPosition(emp.getPosition());
	        Annual zeroAnn = new Annual(emp.getEmpId(), annYear, 0.0, totalAnn);
	        annRepository.save(zeroAnn);
	        AnnualDTO dto = new AnnualDTO(zeroAnn, emp);
	        dtoList.add(dto);
	    }

	    return new PageImpl<>(dtoList, pageable, dtoList.size());
	}



	// 검색창
	public List<AnnualDTO> searchAnn(String keyword) {
		
		List<Object[]> results = annRepository.searchAnn(keyword); // annual과 pesonnel 같이 담아야해서.

	    return results.stream()
	    		.map(obj -> new AnnualDTO((Annual) obj[0], (Personnel) obj[1])).toList();
	}



	// 사원등록시 연차 초기 생성
	public void annListUpdate(String empId) {
		Personnel emp = getPersonnel(empId);
		
        int totalAnnual = getAnnByPosition(emp.getPosition());
        Annual ann = new Annual(emp.getEmpId(), String.valueOf(java.time.LocalDate.now().getYear()), 0.0, totalAnnual);
        annRepository.save(ann);
    }
	
	
	// 기안서 승인 시 연차 변경
	@Transactional
	public void AnnUpdate() {
		
		List<Appr> allApprovals = apprRepository.findAll();

		for (Appr appr : allApprovals) {
			if (appr.getStatus() != ApprStatus.FINISHED || !"VACATION".equalsIgnoreCase(appr.getReqType())) continue; 

			Long reqId = appr.getReqId(); // 결재 문서 ID

			Annual ann = getOrCreateAnnual(appr.getEmpId(), appr.getRequestAt().getYear());

			List<ApprDetail> details = apprDtRepository.findByApprReqId(reqId);

			// 연차 일수 계산
			double totalDays = details.stream()
	                .mapToDouble(d -> {
	                	if (d.getVacType() == ApprVacType.LEAVE) return ChronoUnit.DAYS.between(d.getStartDate(), d.getEndDate()) + 1;
	                    else if (d.getVacType() == ApprVacType.HALF_LEAVE) return 0.5;
	                    else return 0;
	                }).sum();
			
			if (ann.getAnnUse() >= totalDays) continue;
			

			// 연차 차감
			ann.setAnnUse(ann.getAnnUse() + totalDays);
			ann.setAnnRemain(ann.getAnnTotal() - ann.getAnnUse());
			annRepository.save(ann);
	    }
	}

	
	// 오늘 연차자 조회
	@Transactional(readOnly = true)
	public List<AnnualDTO> getTodayAnn() {
		LocalDate today = LocalDate.now(); // 오늘 날짜 가져오기

		List<Appr> approvals = apprRepository.findAll().stream()
				.filter(a -> a.getStatus() == ApprStatus.FINISHED && "VACATION".equalsIgnoreCase(a.getReqType()))
				.toList();

		return approvals.stream().map(appr -> {
			List<ApprDetail> details = apprDtRepository.findByApprReqId(appr.getReqId());

			// 오늘 해당되는 결재 상세
			ApprDetail todayDetail = details.stream()
					.filter(d -> !today.isBefore(d.getStartDate()) && !today.isAfter(d.getEndDate())).findFirst()
					.orElse(null);

			if (todayDetail == null)
				return null;

			// 연차/반차 구분 텍스트
			StringBuilder text = new StringBuilder(); // 여러문자열을 연결해서 하나의 문자열로 만들기 위함
			for (ApprDetail d : details) {
				if (!today.isBefore(d.getStartDate()) && !today.isAfter(d.getEndDate())) {
					if (d.getVacType() == ApprVacType.LEAVE) {
						text.append("연차");
					} else if (d.getVacType() == ApprVacType.HALF_LEAVE) {
						text.append(halfType(d.getHalfType())).append(" ");
					}
				}
			}

			Annual ann = getOrCreateAnnual(appr.getEmpId(), appr.getRequestAt().getYear());
			ann.setAnnUse(text.toString().contains("연차") ? 1.0 : 0.5); // 임시 연차 사용량

			Personnel emp = getPersonnel(appr.getEmpId());

			AnnualDTO dto = new AnnualDTO(ann, emp);
			dto.setAnnType(text.toString().trim()); // 화면에 표시할 텍스트
			return dto;
		})
		.filter(dto -> dto != null)
		.collect(Collectors.toList());
	}
	

}
