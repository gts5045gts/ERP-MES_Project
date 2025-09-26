package com.erp_mes.mes.pop.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.erp.commonCode.dto.CommonCodeDTO;
import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.erp.commonCode.repository.CommonCodeRepository;
import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.mes.plant.dto.ProcessDTO;
import com.erp_mes.mes.pm.dto.BomDTO;
import com.erp_mes.mes.pop.dto.DefectDTO;
import com.erp_mes.mes.pop.dto.WorkResultDTO;
import com.erp_mes.mes.pop.entity.WorkResult;
import com.erp_mes.mes.pop.mapper.DefectMapper;
import com.erp_mes.mes.pop.mapper.WorkResultMapper;
import com.erp_mes.mes.pop.repository.WorkResultRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefectService {
	
	private final DefectMapper defectMapper;
	private final WorkResultMapper workResultMapper;


// ==============================================================================

	// 불량 테이블 저장
	public Long saveDefect(DefectDTO defectDTO) {
		String lotId = workResultMapper.findLotIdByWorkOrderId(defectDTO.getWorkOrderId());
		defectDTO.setLotId(lotId);

	    defectMapper.saveDefect(defectDTO); // insert 실행
	    return defectDTO.getDefectItemId(); 
	}
	
	// 불량 수량 업데이트
	@Transactional
	public int updateDefect(DefectDTO defectDTO) {
		return defectMapper.updateDefect(defectDTO);
	}


}
