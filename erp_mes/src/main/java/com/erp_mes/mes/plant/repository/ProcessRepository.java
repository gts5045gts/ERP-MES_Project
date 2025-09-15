package com.erp_mes.mes.plant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp_mes.erp.personnel.entity.Personnel;

@Repository
public interface ProcessRepository extends JpaRepository<Personnel, String>{

}
