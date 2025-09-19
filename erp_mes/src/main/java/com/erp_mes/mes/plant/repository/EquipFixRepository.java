package com.erp_mes.mes.plant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp_mes.mes.plant.entity.EquipFix;

@Repository
public interface EquipFixRepository extends JpaRepository<EquipFix, String>{

}
