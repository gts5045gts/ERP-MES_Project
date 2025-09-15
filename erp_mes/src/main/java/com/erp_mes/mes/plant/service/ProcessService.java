package com.erp_mes.mes.plant.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.mes.plant.dto.ProcessDTO;
import com.erp_mes.mes.plant.entity.Process;
import com.erp_mes.mes.plant.mapper.ProcessMapper;
import com.erp_mes.mes.plant.repository.ProcessRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
@Log4j2
public class ProcessService {
	
	final private CommonDetailCodeRepository codeRepository;
	final private ProcessMapper proMapper;
	final private ProcessRepository proRepository;
	
	
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


	public List<CommonDetailCodeDTO> findAllByPro() {
		List<CommonDetailCode> listCom = codeRepository.findAll();
		
		List<CommonDetailCodeDTO> comList = listCom.stream()
				.filter( result -> "PRO".equals(result.getComId().getComId()))
				.map(CommonDetailCodeDTO :: fromEntity)
				.collect(Collectors.toList());
		
		return comList;
	}


	public void savePro(ProcessDTO proDTO) {
		log.info("proService에 진입 savePro------------------------------");
		
		proDTO.setProId("test8");
		Process pro = new Process();
		pro = pro.fromDTO(proDTO, codeRepository);

		proRepository.save(pro);
		
		
		
	}
	
	
	
	
	



	
	
	
}
