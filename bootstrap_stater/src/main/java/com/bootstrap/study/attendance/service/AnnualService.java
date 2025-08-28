package com.bootstrap.study.attendance.service;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bootstrap.study.attendance.dto.AnnualDTO;
import com.bootstrap.study.attendance.entity.Annual;
import com.bootstrap.study.attendance.repository.AnnualRepository;
import com.bootstrap.study.commonCode.entity.CommonDetailCode;
import com.bootstrap.study.personnel.entity.Personnel;
import com.bootstrap.study.personnel.repository.PersonnelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnualService {


	private final AnnualRepository annRepository;
	private final PersonnelRepository empRepository;
	

// ============================================	
	
	// 내 연차 조회
	public AnnualDTO myAnnual(String empId, String annYear) {
		
		Personnel emp = empRepository.findById(empId)
	            .orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));
		
		// 연차 데이터 조회 혹은 신규 생성
		Annual ann = annRepository.findByEmpIdAndAnnYear(empId, annYear)
				.orElseGet(() -> new Annual(empId, annYear, 0.0, (double)getAnnByPosition(emp.getPosition())));

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
	
	// 모든사원의 연차 조회
	public Page<AnnualDTO> getAllAnnByYearPaged(String annYear, Pageable pageable) {
		 Page<Annual> annPage = annRepository.findByAnnYear(annYear, pageable); // JpaRepository에서 Page 지원

		    // 2. Annual -> AnnualDTO 변환
		    List<AnnualDTO> dtoList = annPage.stream().map(ann -> {
		        Personnel emp = empRepository.findById(ann.getEmpId())
		                .orElseThrow(() -> new RuntimeException("해당 사원이 없습니다."));
		        return new AnnualDTO(ann, emp); // DTO 생성자에서 annPeriod, annExpire 계산됨
		    }).collect(Collectors.toList());

		    // 3. Page<AnnualDTO>로 변환
		    return new PageImpl<>(dtoList, pageable, annPage.getTotalElements());
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
	public void annListUpdate(Personnel emp) {
        int totalAnnual = getAnnByPosition(emp.getPosition());
        Annual ann = new Annual(emp.getEmpId(), String.valueOf(java.time.LocalDate.now().getYear()), 0.0, totalAnnual);
        annRepository.save(ann);
    }




	
	

	
}
