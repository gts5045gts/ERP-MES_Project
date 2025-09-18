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
import com.erp_mes.mes.plant.mapper.ProcessRouteMapper;
import com.erp_mes.mes.plant.repository.EquipRepository;
import com.erp_mes.mes.plant.repository.ProcessRepository;

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
	
	
	
	
	
	
	
	



	
	
	
}
