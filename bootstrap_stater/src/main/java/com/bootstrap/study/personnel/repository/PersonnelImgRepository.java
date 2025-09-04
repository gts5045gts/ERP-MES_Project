package com.bootstrap.study.personnel.repository;

import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bootstrap.study.personnel.entity.PersonnelImg;

public interface PersonnelImgRepository extends JpaRepository<PersonnelImg, String> {



	Optional<PersonnelImg> findByPersonnel_EmpId(@Param("empId")String empId);

}
