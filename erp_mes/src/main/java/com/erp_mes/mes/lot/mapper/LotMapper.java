package com.erp_mes.mes.lot.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.plant.dto.ProcessDTO;
import com.erp_mes.mes.pop.dto.WorkResultDTO;

@Mapper
public interface LotMapper {

	//로트 추적 페이지 전체 리스트
	List<LotDTO> lotListWithPaged(Map<String, Object> params);

	List<ProcessDTO> findByProcess(String productId);

	List<WorkResultDTO> findDetail(Long workOrderId);

}
