package com.bootstrap.study.personnel.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bootstrap.study.personnel.dto.PersonnelImgDTO;

@Mapper
public interface PersonnelImgMapper {

	PersonnelImgDTO findByempId(String empId);

}
