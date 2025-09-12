package com.erp_mes.mes.plant.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.mes.plant.dto.ProcessDTO;
import com.erp_mes.mes.plant.mapper.ProcessMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
@Log4j2
public class ProcessService {
	
	final private CommonDetailCodeRepository codeRepository;
	final private ProcessMapper proMapper;
	
	
	public List<Map<String, String>> findAll() {
		List<ProcessDTO> proList = proMapper.findAll();
		
		List<Map<String, String>> position = proList.stream()
			.map(dto -> Map.of("proId", dto.getProId(),
								"proNm", dto.getProNm(),
								"typeNm",dto.getTypeNm(),
								"note",dto.getNote()
					))
			.toList();
			
		
		
		
		return position;
	}
	
	
	
	
	



	
	
	
}
