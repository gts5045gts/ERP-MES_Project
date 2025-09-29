package com.erp_mes.mes.lot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.lot.dto.LotDetailDTO;
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
	
	@Query(value = """
			select 
				o.lot_id as lotId,
				o.out_count as outCount,
				
				m.material_name as materialNm,
				m.material_type as materialType,
				m.material_id as materialId
				  
			from 
				output o
			left join 
				material m
			on 
				o.material_id = m.material_id
			where
				o.work_order_id = :workOrderId
			""", nativeQuery = true)
	List<LotDetailDTO> findByMaterialInfo(@Param("workOrderId") String workOrderId);

	@Query(value = """
			select 
			    e.equip_nm as equipNm,
			    e.equip_id as equipId,
			    e.note as note
			from 
				process_routing p
			left join
			    equipment e
		    on 
			   e.equip_id = p.equip_id
			where 
				p.product_id = :productId
			order by e.equip_id
			""", nativeQuery = true)
	List<LotDetailDTO> findByEquipmenInfo(@Param("productId") String productId);

	List<LotMaster> findByTargetIdValue(String targetIdValue);

}
