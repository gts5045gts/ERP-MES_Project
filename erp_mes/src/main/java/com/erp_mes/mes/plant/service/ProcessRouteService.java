package com.erp_mes.mes.plant.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.erp.config.util.SessionUtil;
import com.erp_mes.mes.lot.trace.TrackLot;
import com.erp_mes.mes.plant.dto.ProcessRouteDTO;
import com.erp_mes.mes.plant.entity.Equip;
import com.erp_mes.mes.plant.entity.Process;
import com.erp_mes.mes.plant.mapper.ProcessRouteMapper;
import com.erp_mes.mes.plant.repository.EquipRepository;
import com.erp_mes.mes.plant.repository.ProcessRepository;
import com.erp_mes.mes.pm.dto.BomDTO;
import com.erp_mes.mes.pm.dto.ProductDTO;
import com.erp_mes.mes.pm.mapper.ProductBomMapper;

import jakarta.servlet.http.HttpSession;
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
					map.put("materialNm", dto.getMaterialNm());
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

//	@TrackLot(tableName = "processs_routing", pkColumnName = "route_id") 	삭제
	public void saveRoute(ProcessRouteDTO routeDTO) {
		
		
		List<ProcessRouteDTO> routeList = routeMapper.findByProductIdAll(routeDTO.getProductId());
		
		Long seq =(long)(routeList.size() + 1);
		routeDTO.setProSeq(seq);
		
		/* 라인 컬럼 삭제
		//Line_id 생성------------------------------
		String productId = routeDTO.getProductId();  

	    // 끝 3자리 추출
	    String lastThree = productId.substring(productId.length() - 3);

	    // 라인 아이디 생성
	    String lineId = "LINE" + lastThree;
	    
	    routeDTO.setLineId(lineId);
	    */
		

	    routeMapper.save(routeDTO);
	    
	    
	    
	    
	    log.info("------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + routeDTO.getRouteId());
//      *******로트 생성: pk value 를 넘겨주는 곳 모든 프로세스가 끝나고 입력하면됨**********
//		HttpSession session = SessionUtil.getSession();
//		session.setAttribute("targetIdValue", routeDTO.getRouteId()); //pk_id의 값 입력
	

	}



	public List<Map<String, Object>> findMaterialByProductId(String productId) {
		List<BomDTO> bomDTO = productBomMapper.getBomListRoute(productId);
		
		List<Map<String, Object>> bom = bomDTO.stream()
				.map(dto ->{
					Map<String, Object> map = new HashMap<>();
					map.put("bomId", dto.getBomId());
					map.put("materialId", dto.getMaterialId());
					map.put("materialNm", dto.getMaterialName());
					return map;
					})
				.collect(Collectors.toList());
		
		return bom;
	}
	
	
	
	
	
	
	
	



	
	
	
}
