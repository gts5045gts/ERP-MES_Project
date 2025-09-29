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
import com.erp_mes.mes.pop.mapper.WorkResultMapper;
import com.erp_mes.mes.pop.repository.WorkResultRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkResultService {
	
	private final WorkResultRepository workResultRepository;
	private final WorkResultMapper workResultMapper;
	private final CommonDetailCodeRepository comDtRepository;
	
// ==============================================================================

	// bom 조회
	public List<WorkResultDTO> workOrderWithBom(String productId) {
	    return workResultMapper.workOrderWithBom(productId);
	}
	
	
	// 작업시작 클릭시 작업현황에 업데이트
	@Transactional
	public List<WorkResultDTO> startWork(Long workOrderId) {
		workResultMapper.updateWorkOrderStatus(workOrderId);

		  WorkResult result = workResultRepository.findByWorkOrderId(workOrderId)
		            .orElseGet(() -> {
		                WorkResult w = new WorkResult();
		                w.setWorkOrderId(workOrderId); // 단순히 work_order_id만 세팅
		                return w;
		            });

		    workResultRepository.save(result);

		return workResultMapper.updateWorkResult(workOrderId);
	}



	// 무한 스크롤
	public List<WorkResultDTO> getPagedWorkResults(int page, int size) {
        int offset = page * size;

        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("size", size);

        return workResultMapper.workResultWithPaged(params);
    }

	
	// 수량 업데이트
	@Transactional
	public int updateWorkResult(WorkResultDTO dto) {
		Optional<WorkResult> result = workResultRepository.findById(dto.getResultId());
        if (result.isPresent()) {
        	WorkResult entity = result.get();
            entity.setGoodQty(dto.getGoodQty());
            entity.setDefectQty(dto.getDefectQty());
            return 1;
        }
        return 0;
    }

	// 불량 사유
	public List<CommonDetailCodeDTO> getDefectReason() {
		return comDtRepository.findByComId_ComId("DEFECT")
				.stream()
                .map(code -> new CommonDetailCodeDTO(code.getComDtId(), code.getComDtNm()))
                .collect(Collectors.toList());
	}


	public List<WorkResultDTO> bomByWorkOrderId(Long workOrderId) {
		return workResultMapper.bomByWorkOrderId(workOrderId);
	}







}
