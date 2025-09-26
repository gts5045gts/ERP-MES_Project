package com.erp_mes.mes.pop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp_mes.mes.pop.dto.WorkResultDTO;
import com.erp_mes.mes.pop.entity.WorkResult;

@Repository
public interface WorkResultRepository extends JpaRepository<WorkResult, Long> {

	
	// 작업시작 시 생산실적 테이블 업데이트
	Optional<WorkResult> findByWorkOrderId(Long workOrderId);
}
