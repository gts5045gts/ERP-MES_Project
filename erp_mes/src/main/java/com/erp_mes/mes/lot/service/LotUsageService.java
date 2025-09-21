package com.erp_mes.mes.lot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.erp_mes.mes.lot.dto.MaterialUsageDTO;
import com.erp_mes.mes.lot.repository.LotMaterialUsageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class LotUsageService {

	private final LotMaterialUsageRepository usageRepository;

	public List<MaterialUsageDTO> getMaterialLotsForWorkOrder(Object routeId) {
		
		
		return null;
	}
	
}