package com.bootstrap.study.commonCode.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bootstrap.study.commonCode.dto.CommonCodeDTO;
import com.bootstrap.study.commonCode.dto.CommonDetailCodeDTO;
import com.bootstrap.study.commonCode.entity.CommonCode;
import com.bootstrap.study.commonCode.entity.CommonDetailCode;
import com.bootstrap.study.commonCode.mapper.CommonCodeMapper;
import com.bootstrap.study.commonCode.repository.CommonCodeRepository;
import com.bootstrap.study.commonCode.repository.CommonDetailCodeRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonCodeService {
	
	private final CommonCodeMapper comMapper;
	private final CommonCodeRepository comRepository;
	private final CommonDetailCodeRepository comDetailRepository;
	

// =======================================================================================



	// 코드 조회 
	public List<CommonCode> findAllCodes() {
		return comRepository.findAll();
	}
	
	
	// 상세코드 조회
	public List<CommonDetailCode> findByComId(String comId) {
		return comDetailRepository.findByComId_ComId(comId);
	}


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




	
	
}
