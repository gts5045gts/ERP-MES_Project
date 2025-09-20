package com.erp_mes.mes.pop.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
// ==============================================================================
	
	// 작업시작 체크박스 선택시 작업현황에 업데이트
	@Transactional
	public void workResultList(List<Long> workOrderIds) {
		List<WorkResult> results = workOrderIds.stream()
			.map(id -> {
			    WorkResult wr = new WorkResult();
			    wr.setWorkOrderId(id); // workOrderId만 세팅
			    return wr;
			})
			.collect(Collectors.toList());

			workResultRepository.saveAll(results);
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



}
