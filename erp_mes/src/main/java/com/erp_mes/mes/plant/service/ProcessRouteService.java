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
import com.erp_mes.mes.plant.dto.ProcessRouteDTO;
import com.erp_mes.mes.plant.entity.Equip;
import com.erp_mes.mes.plant.entity.Process;
import com.erp_mes.mes.plant.mapper.ProcessMapper;
import com.erp_mes.mes.plant.mapper.ProcessRouteMapper;
import com.erp_mes.mes.plant.repository.EquipRepository;
import com.erp_mes.mes.plant.repository.ProcessRepository;
import com.erp_mes.mes.pm.dto.ProductDTO;
import com.erp_mes.mes.pm.mapper.ProductBomMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
@Log4j2
public class ProcessRouteService {
	
	final private ProcessRouteMapper routeMapper;
	
	final private CommonDetailCodeRepository codeRepository;
	final private ProcessRepository proRepository;
	final private EquipRepository equipRepository;
	final private ProductBomMapper productBomMapper;
	
	
	
	public List<Map<String, Object>> findAll() {
		
		List<ProcessRouteDTO> listRoute = routeMapper.findAll();
		
		
		List<Map<String, Object>> routeList = listRoute.stream()
				.map(dto ->{
					Map<String, Object> map = new HashMap<>();
					map.put("routeId", dto.getRouteId());
					map.put("note", dto.getNote());
					map.put("productNm", dto.getProductNm());
					map.put("equipNm", dto.getEquipNm());
					map.put("proNm", dto.getProNm());
					return map;
					})
				.collect(Collectors.toList());
				
		return routeList;
	}



	public List<ProductDTO> productList() {
		List<ProductDTO> productList = productBomMapper.getProductList();
		log.info("제품 정보 전체조회"  + productList);
		
		return productList;
	}



	public List<Process> proList() {
		List<Process> proList = proRepository.findAll();
		log.info("공정 정보 전체조회"  + proList);
		return proList;
	}



	public List<Equip> equipList() {
		List<Equip> equipList = equipRepository.findAll();
		log.info("설비 정보 전체조회"  + equipList);
		
		return equipList;
	}



	public void saveRoute(ProcessRouteDTO routeDTO) {
		List<ProcessRouteDTO> routeList = routeMapper.findByProductIdAll(routeDTO.getProductId());
		
		Long seq =(long)(routeList.size() + 1);
		routeDTO.setProSeq(seq);
		
		routeMapper.save(routeDTO);
		
		
	}
	
	
	
	
	
	
	
	



	
	
	
}
