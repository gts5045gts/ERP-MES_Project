package com.bootstrap.study.personnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bootstrap.study.personnel.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
