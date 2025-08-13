package com.bootstrap.study.personnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bootstrap.study.personnel.entity.Personnel;

public interface PersonnelRepository extends JpaRepository<Personnel,Long> {

}
