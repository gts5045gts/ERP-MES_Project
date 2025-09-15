package com.erp_mes.mes.plant.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
@Log4j2
public class ProcessService {
	
	final private CommonDetailCodeRepository codeRepository;
	
	
	
	public List<Map<String, String>> findAll() {
		List<CommonDetailCode> comList = codeRepository.findAll();
		
		List<Map<String, String>> position = comList.stream()
			.filter(result -> "PRO".equals(result.getComId().getComId()))
			.map(CommonDetailCodeDTO :: fromEntity)
			.map(dto -> Map.of("comDtId", dto.getComDtId(),
					"comDtNm", dto.getComDtNm()))
			.toList();
			
		
		
		
		return position;
	}
	
	
	
	
	



	
	
	
}