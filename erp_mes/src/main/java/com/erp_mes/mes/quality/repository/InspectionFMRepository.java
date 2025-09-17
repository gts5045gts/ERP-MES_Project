package com.erp_mes.mes.quality.repository;

import com.erp_mes.mes.quality.entity.InspectionFM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectionFMRepository extends JpaRepository<InspectionFM, Long> {
	
}