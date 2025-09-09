package com.erp_mes.erp.personnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp_mes.erp.personnel.entity.PersonnelTransfer;

@Repository
public interface PersonnelTransferRepository extends JpaRepository<PersonnelTransfer, Long>{

}
