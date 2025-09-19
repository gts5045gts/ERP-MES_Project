package com.erp_mes.mes.plant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.mes.plant.dto.EquipDTO;
import com.erp_mes.mes.plant.dto.EquipFixDTO;
import com.erp_mes.mes.plant.dto.ProcessRouteDTO;
import com.erp_mes.mes.plant.entity.Equip;
import com.erp_mes.mes.plant.entity.EquipFix;
import com.erp_mes.mes.plant.mapper.EquipFixMapper;
import com.erp_mes.mes.plant.mapper.EquipMapper;
import com.erp_mes.mes.plant.mapper.ProcessMapper;
import com.erp_mes.mes.plant.repository.EquipFixRepository;
import com.erp_mes.mes.plant.repository.EquipRepository;
import com.erp_mes.mes.plant.repository.ProcessRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/equip")
@RequiredArgsConstructor
@Log4j2
public class EquipService {
	
	final private CommonDetailCodeRepository codeRepository;
	final private EquipRepository equipRepository;
	
	
	
	final private ProcessMapper proMapper;
	final private EquipMapper equipMapper;
	final private EquipFixMapper equipFixMapper;
	
	
	final private ProcessRepository proRepository;
	final private EquipFixRepository equipFixRepository;
	
	
	
	public List<Map<String, Object>> findAll() {
		List<EquipDTO> equipList = equipMapper.findAll();
		
		List<Map<String, Object>> equip = equipList.stream()
			.map(dto -> {
					Map<String, Object> map = new HashMap<>();
					map.put("equipId", dto.getEquipId());
					map.put("equipNm", dto.getEquipNm());
					map.put("typeNm", dto.getTypeNm());
					map.put("useYn", dto.getUseYn());
					map.put("note",dto.getNote());
					map.put("purchaseDt",dto.getPurchaseDt());
					map.put("installDt",dto.getInstallDt());
					return map;
				})
		    .collect(Collectors.toList());
		
		
		
		return equip;
	}



	public void saveEquip(EquipDTO equipDTO) {
		log.info("equipService에 진입 saveEquip------------------------------");
		List<Equip> equipList = equipRepository.findAll();
		
		Long count = (long) (equipList.size() + 1);
		String id = "EQ" + String.format("%03d", count);
		
		
		equipDTO.setEquipId(id);
		equipDTO.setUseYn("Y");
		
		
		
		Equip equip = new Equip();
		equip = equip.fromDTO(equipDTO, codeRepository);

		equipRepository.save(equip);
		log.info("저장완료!!");
		
		
	}


	//수리이력에 대한 로직
	public List<Map<String, Object>> findById(String equipId) {
		List<EquipFixDTO> fixList = equipFixMapper.findById(equipId);
		
		List<Map<String, Object>> fix = fixList.stream()
				.map(dto -> {
						Map<String, Object> map = new HashMap<>();
						map.put("startDt", dto.getStartDt());
						map.put("endDt",dto.getEndDt());
						map.put("note", dto.getNote());
						return map;
					})
			    .collect(Collectors.toList());
		
		
		return fix;
	}



	public void saveFix(EquipFixDTO fix) {
		EquipFix equipFix = new EquipFix();
		equipFix = equipFix.fromDTO(fix);
		
		
		equipFixRepository.save(equipFix);
	}



	
}
