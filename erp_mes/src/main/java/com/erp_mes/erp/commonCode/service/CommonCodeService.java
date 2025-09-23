package com.erp_mes.erp.commonCode.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.erp.commonCode.dto.CommonCodeDTO;
import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.erp.commonCode.entity.CommonCode;
import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.repository.CommonCodeRepository;
import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonCodeService {
	
	private final CommonCodeRepository comRepository;
	private final CommonDetailCodeRepository comDetailRepository;
	

// =======================================================================================

// ========================================= 조회 ==============================================
	
	// 부서 ID(comDtId)로 상세 코드(부서)를 조회하는 메서드
	// 지금 부서명으로 안나오고 해당 등록된 코드로 나와서 부서id로 해당 부서이름 불러오는 코드 추가함
	// ex) 인사팀 으로 나와야하는데 DEP001로 나옴.
	public CommonDetailCode getCommonDetailCode(String comDtId) {
	    return comDetailRepository.findById(comDtId).orElse(null);
	}

	// 코드 조회 
	public List<CommonCode> findAllCodes() {
		return comRepository.findAll();
	}
	
	
	// 상세코드 조회
	public List<CommonDetailCode> findByComId(String comId) {
		CommonCode commonCode = comRepository.findById(comId)
		        .orElseThrow(() -> new IllegalArgumentException("해당 공통코드가 존재하지 않습니다: " + comId));

		return comDetailRepository.findByComIdOrderByComDtOrderAsc(commonCode);
	}

// ========================================= 등록 ===========================================
	
	// 공통코드 등록
	public CommonCode registCode(@Valid CommonCodeDTO codeDTO) {
		CommonCode comCode = codeDTO.toEntity();
		
		return comRepository.save(comCode);
		
	}

	// 상세코드 등록
	public CommonDetailCode registDtCode(@Valid CommonDetailCodeDTO codeDtDTO) {
		
		CommonCode parentId = comRepository.findById(codeDtDTO.getComId())
									.orElseThrow(() -> new RuntimeException("부모 코드 없음"));
		
		CommonDetailCode comDtCode = codeDtDTO.toEntity();
		
		comDtCode.setComId(parentId);
		
		return comDetailRepository.save(comDtCode);
		
	}




// ========================================= 삭제 ===========================================

	// 공통 코드 삭제
	@Transactional
	public String comDelete(String comId) {
		CommonCode commonCode = comRepository.findById(comId)
		            .orElseThrow(() -> new IllegalArgumentException("해당 공통코드가 존재하지 않습니다: " + comId));
		
		if (!commonCode.getDetailCodes().isEmpty()) {
		    return "하위 상세코드가 존재하여 삭제할 수 없습니다.";
		}

		comRepository.delete(commonCode);
		return "삭제가 완료되었습니다."; // 추가
	}

	
	
	// 상세 코드 삭제
	@Transactional
	public String deleteDtCode(String comDtId) {
		CommonDetailCode dtCode = comDetailRepository.findById(comDtId)
	                .orElseThrow(() -> new IllegalArgumentException("해당 상세코드가 존재하지 않습니다: " + comDtId));
		
		comDetailRepository.delete(dtCode);
		return "삭제가 완료되었습니다.";
	}
	


	
	
// ================================================ 수정 ===========================================
	
	
	// 공통 코드 수정
	@Transactional
	public void updateCode(String comId, String comNm, String useYn) {
		CommonCode code = comRepository.findById(comId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상세코드가 존재하지 않습니다: " + comId));
		
		code.setComNm(comNm);
		code.setUseYn(useYn);
		
	}
	
	
	// 상세코드 수정
	@Transactional
	public void updateDtCode(String comDtId, Integer comDtOrder, String comDtNm, String useYn) {
	    // 상세공통코드 조회
		CommonDetailCode dtCode = comDetailRepository.findById(comDtId)
		        .orElseThrow(() -> new IllegalArgumentException("해당 상세코드가 존재하지 않습니다: " + comDtId));
		
		// 값 수정
		dtCode.setComDtNm(comDtNm);
		dtCode.setComDtOrder(comDtOrder);
		dtCode.setUseYn(useYn);
		
        
    }

// ================================================ 검색 ===========================================
	
	// 공통코드 검색
	public List<CommonCodeDTO> searchCode(String keyword) {
		List<CommonCode> results = comRepository.searchCode(keyword);
        
		return results.stream()
                .map(CommonCodeDTO::fromEntity)
                .toList();
	}

	// 상세코드 검색
	public List<CommonDetailCodeDTO> searchDtCode(String parentId, String keyword) {
		 List<CommonDetailCode> results;

		    if (keyword != null && !keyword.isEmpty()) {
		        // 검색어가 있을 때: parentId + keyword 필터
		        results = comDetailRepository.searchByParentAndKeyword(parentId, keyword);
		    } else {
		        // 검색어가 없을 때: parentId 기준만
		        results = comDetailRepository.findByComId_ComId(parentId);
		    }

		    return results.stream()
		                  .map(CommonDetailCodeDTO::fromEntity)
		                  .toList();
	}






 

	
	




	
}
