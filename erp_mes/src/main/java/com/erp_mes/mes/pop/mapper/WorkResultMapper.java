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
	List<WorkResultDTO> workOrderWithBom(@Param("productId") String productId);
    
    // 작업지시서의 작업상태 업데이트
	int updateWorkOrderStatus(@Param("workOrderId") Long workOrderId);
	
	// 작업현황 업데이트
	List<WorkResultDTO> updateWorkResult(@Param("workOrderId") Long workOrderId);
    
	// 무한스크롤
	List<WorkResultDTO> workResultWithPaged(Map<String, Object> params);
	
	// 작업완료 상태 업데이트(단일)
	int updateWorkStatusFinish(@Param("workOrderId") Long workOrderId);

	// 불량 실적에 로트아이디 넣기
	String findLotIdByWorkOrderId(@Param("workOrderId") Long workOrderId);

	List<WorkResultDTO> bomByWorkOrderId(@Param("workOrderId") Long workOrderId);




	
	
	




}
