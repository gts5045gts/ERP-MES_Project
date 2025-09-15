package com.erp_mes.mes.plant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp_mes.erp.personnel.entity.Personnel;

public interface ProcessRepository extends JpaRepository<Personnel, String>{

}
