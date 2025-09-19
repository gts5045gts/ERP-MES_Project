package com.erp_mes.mes.pop.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.erp_mes.mes.pop.dto.WorkResultDTO;

@Mapper
public interface WorkResultMapper {
	
	// 작업지시조회를 위한 조인
	List<WorkResultDTO> workerkWithOrder(@Param("empId") String empId);
	
	// bom조회를 위한 조인
	List<WorkResultDTO> workOrderWithBom(@Param("workOrderId") Long workOrderId);
	
	// 작업현황을 위한 조인
	List<WorkResultDTO> workResultWithBom(@Param("workOrderIds") List<Long> workOrderIds);
	
	// 작업진행중 업데이트
	int updateWorkOrderStatus(@Param("list") List<Long> workOrderIds);
	
	// 무한스크롤
	List<WorkResultDTO> workResultWithPaged(Map<String, Object> params);
	
	// 작업완료 상태 업데이트(단일)
	int updateWorkStatusFinish(@Param("workOrderId") Long workOrderId);
	

}
