package com.erp_mes.mes.lot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.erp_mes.mes.lot.entity.LotMaster;

@Repository
public interface LotRepository extends JpaRepository<LotMaster, String> {

	//마지막 lot_id 조회
	@Query(value = """
			SELECT lot_id FROM lot_master
			WHERE lot_id LIKE :prefix || :datePart ||
			CASE WHEN :machineId IS NOT NULL THEN '-' || :machineId || '-%' ELSE '-%' END
			ORDER BY lot_id DESC FETCH FIRST 1 ROWS ONLY
			""", nativeQuery = true)
	String findByLastLotId(@Param("prefix") String prefix, @Param("datePart") String datePart,
			@Param("machineId") String machineId);

	//work_order_id 조회
	@Query(value = """
			SELECT 
				work_order_id
			FROM 
				work_result
			WHERE
				lot_id = :lotId
			""", nativeQuery = true)
	Long findPopByworkOrderId(@Param("lotId") String popLotId);

	List<LotMaster> findByWorkOrderId(Long workOrderId);

}
