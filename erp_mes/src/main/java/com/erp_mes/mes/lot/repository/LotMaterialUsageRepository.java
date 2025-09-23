package com.erp_mes.mes.lot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.erp_mes.mes.lot.entity.LotMaterialUsage;

@Repository
public interface LotMaterialUsageRepository extends JpaRepository<LotMaterialUsage, Object> {
	
    //input 테이블 lot_id 조회
    @Query(value = "select lot_id from input where in_id = :inId"
    		, nativeQuery = true)
	String findByLotId(@Param("inId") String inId);

}
