package com.bootstrap.study.personnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bootstrap.study.personnel.entity.PersonnelTransfer;

@Repository
public interface PersonnelTransferRepository extends JpaRepository<PersonnelTransfer, Long>{

}
