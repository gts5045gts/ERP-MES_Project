package com.erp_mes.mes.lot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.erp_mes.mes.lot.entity.LotMaster;

@Repository
public interface LotRepository extends JpaRepository<LotMaster, String>{

	@Query(value = """
			SELECT lot_id FROM lot_master
			WHERE lot_id LIKE :prefix || :datePart || 
			CASE WHEN :machineId IS NOT NULL THEN '-' || :machineId || '-%' ELSE '-%' END
			ORDER BY lot_id DESC FETCH FIRST 1 ROWS ONLY
			""", nativeQuery = true)
	String findByLastLotId(@Param("prefix") String prefix, @Param("datePart") String datePart, @Param("machineId") String machineId);

}
