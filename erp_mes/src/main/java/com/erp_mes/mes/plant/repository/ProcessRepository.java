package com.erp_mes.mes.plant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp_mes.mes.plant.entity.Process;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long>{

}
