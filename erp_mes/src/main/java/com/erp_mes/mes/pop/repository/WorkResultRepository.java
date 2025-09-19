package com.erp_mes.mes.pop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp_mes.mes.pop.entity.WorkResult;

@Repository
public interface WorkResultRepository extends JpaRepository<WorkResult, Long> {
	
}
