package com.bootstrap.study.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bootstrap.study.attendance.entity.Annual;

@Repository
public interface AnnualRepository extends JpaRepository<Annual, Long> {

}
