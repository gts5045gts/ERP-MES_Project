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

import com.bootstrap.study.approval.constant.ApprStatus;
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
	
	// 내 연차 조회
	public AnnualDTO myAnnual(String empId, String annYear) {
		
		Personnel emp = empRepository.findById(empId)
	            .orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));
		
		
		
		// 연차 데이터 조회 혹은 신규 생성
		Annual ann = annRepository.findByEmpIdAndAnnYear(empId, annYear)
				.orElseGet(() -> {
                    int totalAnnual = getAnnByPosition(emp.getPosition());
                    Annual newAnn = new Annual(empId, annYear, 0.0, totalAnnual);
                    annRepository.save(newAnn);
                    return newAnn;
                });

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
	
	
	// 모든사원의 연차 조회 + 무한스크롤
	@Transactional
	public Page<AnnualDTO> getAllAnnByYearPaged(String annYear, Pageable pageable) {
		Page<Annual> annPage = annRepository.findByAnnYear(annYear, pageable); 

		// 2. Annual -> AnnualDTO 변환
		List<AnnualDTO> dtoList = annPage.stream().map(ann -> {
			Personnel emp = empRepository.findById(ann.getEmpId())
					.orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));
			ann.setAnnRemain(ann.getAnnTotal() - ann.getAnnUse());
			return new AnnualDTO(ann, emp); // DTO 생성자에서 annPeriod, annExpire 계산됨
		}).collect(Collectors.toList());

		List<String> existingEmpIds = dtoList.stream().map(AnnualDTO::getEmpId).toList();

		List<Personnel> missingEmps = empRepository.findAll().stream()
				.filter(emp -> !existingEmpIds.contains(emp.getEmpId())).toList();

		for (Personnel emp : missingEmps) {
			int totalAnnual = getAnnByPosition(emp.getPosition());
			Annual tempAnn = new Annual(emp.getEmpId(), annYear , 0.0, totalAnnual);
			annRepository.save(tempAnn);
			AnnualDTO dto = new AnnualDTO(tempAnn, emp);
			dtoList.add(dto);
		}
		// 3. Page<AnnualDTO>로 변환
		return new PageImpl<>(dtoList, pageable, dtoList.size());
	}


	// 검색창
	public List<AnnualDTO> searchAnn(String keyword) {
		
		List<Object[]> results = annRepository.searchAnn(keyword);

	    return results.stream()
	                  .map(obj -> new AnnualDTO((Annual) obj[0], (Personnel) obj[1]))
	                  .toList();
	}


	// 내 연차 사용률(도넛차트)
	public AnnualDTO findById(Long id) {
		Annual ann = annRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("해당 사원의 연차가 없습니다.")); 
		
		Personnel emp = empRepository.findById(ann.getEmpId())
	            .orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));
		
		return new AnnualDTO(ann, emp);
	}
	

	// 사원등록시 연차 초기 생성
	public void annListUpdate(String empId) {
		Personnel emp = empRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));
		
        int totalAnnual = getAnnByPosition(emp.getPosition());
        Annual ann = new Annual(emp.getEmpId(), String.valueOf(java.time.LocalDate.now().getYear()), 0.0, totalAnnual);
        annRepository.save(ann);
    }
	
	
	// 기안서 승인 시 연차 변경
	@Transactional
	public void AnnUpdate() {
		// FINISHED + VACATION 결재 조회
		List<Appr> allApprovals = apprRepository.findAll();

		for (Appr appr : allApprovals) {
			if (appr.getStatus() != ApprStatus.FINISHED || !"VACATION".equalsIgnoreCase(appr.getReqType())) continue; 

			Long reqId = appr.getReqId();

			Annual ann = annRepository
					.findByEmpIdAndAnnYear(appr.getEmpId(), String.valueOf(appr.getRequestAt().getYear()))
					.orElseGet(() -> {
						Personnel emp = empRepository.findById(appr.getEmpId())
								.orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));
						int totalAnnual = getAnnByPosition(emp.getPosition());
						Annual newAnn = new Annual(emp.getEmpId(), String.valueOf(appr.getRequestAt().getYear()), 0,
								totalAnnual);
						annRepository.save(newAnn);
						return newAnn;
					});

			Personnel emp = empRepository.findById(ann.getEmpId())
					.orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));

			List<ApprDetail> details = apprDtRepository.findByApprReqId(reqId);

			// 연차 일수 계산
			int totalDays = details.stream()
					.mapToInt(d -> (int) ChronoUnit.DAYS.between(d.getStartDate(), d.getEndDate()) + 1).sum();
			
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
		LocalDate today = LocalDate.now();

		List<Appr> approvals = apprRepository.findAll().stream()
				.filter(a -> a.getStatus() == ApprStatus.FINISHED && "VACATION".equalsIgnoreCase(a.getReqType()))
				.toList();

		List<AnnualDTO> todaysAnn = approvals.stream().filter(appr -> {
			List<ApprDetail> details = apprDtRepository.findByApprReqId(appr.getReqId());
			return details.stream().anyMatch(d -> !today.isBefore(d.getStartDate()) && !today.isAfter(d.getEndDate()));
		}).map(appr -> {
			Annual ann = annRepository
					.findByEmpIdAndAnnYear(appr.getEmpId(), String.valueOf(appr.getRequestAt().getYear()))
					.orElseGet(() -> {
						Personnel emp = empRepository.findById(appr.getEmpId())
								.orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));
						int totalAnnual = getAnnByPosition(emp.getPosition());
						Annual newAnn = new Annual(emp.getEmpId(), String.valueOf(appr.getRequestAt().getYear()), 0,
								totalAnnual);
						annRepository.save(newAnn);
						return newAnn;
					});

			Personnel emp = empRepository.findById(appr.getEmpId())
					.orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));

			return new AnnualDTO(ann, emp);
		}).collect(Collectors.toList());

		return todaysAnn;
	}
	

	
}
