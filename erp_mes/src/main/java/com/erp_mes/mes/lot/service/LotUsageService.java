package com.erp_mes.mes.lot.service;

import org.springframework.stereotype.Service;

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

	public String getInputLotId(Object inId) {
		
		String lotId = usageRepository.findByLotId((String) inId);
		if (lotId == null) {
		       throw new IllegalArgumentException("lotId가 없습니다.");
		}
		
		return lotId;
	}
	
}