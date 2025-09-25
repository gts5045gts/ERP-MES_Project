package com.erp_mes.mes.plant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.mes.plant.dto.ProcessDTO;
import com.erp_mes.mes.plant.entity.Equip;
import com.erp_mes.mes.plant.entity.Process;
import com.erp_mes.mes.plant.mapper.ProcessMapper;
import com.erp_mes.mes.plant.repository.EquipRepository;
import com.erp_mes.mes.plant.repository.ProcessRepository;
import com.erp_mes.mes.quality.entity.InspectionFM;
import com.erp_mes.mes.quality.repository.InspectionFMRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
@Log4j2
public class ProcessService {
	
	final private ProcessMapper proMapper;
	
	final private CommonDetailCodeRepository codeRepository;
	final private ProcessRepository proRepository;
	final private EquipRepository equipRepository;
	final private InspectionFMRepository fmRepository;
	
	
	
	public List<Map<String, Object>> findAll() {
		List<ProcessDTO> proList = proMapper.findAll();
		
		List<Map<String, Object>> position = proList.stream()
			.map(dto -> {
					Map<String, Object> map = new HashMap<>();
					map.put("proId", dto.getProId());
					map.put("proNm", dto.getProNm());
					map.put("typeNm",dto.getTypeNm());
					map.put("note",dto.getNote());
					map.put("inspecNm",dto.getInspecNm());
					return map;
				})
		    .collect(Collectors.toList());
		
		
		
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
		Long inpecId = (long)4;
		InspectionFM inspec = fmRepository.findById(inpecId)
				.orElseThrow(() -> new IllegalArgumentException("해당하는 검사유형이 존재하지 않습니다!!--------------------------"));
		
		proDTO.setInspecId(inspec.getInspectionFMId());
		Process pro = new Process();
		pro = pro.fromDTO(proDTO, fmRepository);
		log.info("저장할 데이터 : " + proDTO.toString());

		
		proRepository.save(pro);
		log.info("저장완료!!");
		
	}


	public List<Equip> equipAll() {
		List<Equip> equipList = equipRepository.findAll(); 
		
		return equipList;
	}

	
	// 기준검사 등록에 사용 - 품질관리
	public List<ProcessDTO> getProcessList() {

		return proMapper.findAll();
	}
	
	
	
	
	



	
	
	
}
