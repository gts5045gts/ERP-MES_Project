package com.erp_mes.mes.pop.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.erp_mes.mes.pm.dto.WorkOrderDTO;
import com.erp_mes.mes.pop.dto.WorkResultDTO;

@Mapper
public interface WorkResultMapper {
	
	// 작업지시조회를 위한 조인
	List<WorkResultDTO> workerkWithOrder(@Param("empId") String empId);
	
	// bom조회를 위한 조인
	List<WorkResultDTO> workOrderWithBom(@Param("workOrderId") Long workOrderId);
	
	// 작업현황을 위한 조인
	List<WorkResultDTO> workResultWithBom(@Param("workOrderIds") List<Long> workOrderIds);
	
	// 검사 대기 목록
	List<WorkOrderDTO> getInspectionTargets();
	
}
