package com.bootstrap.study.attendance.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bootstrap.study.attendance.dto.CommuteDTO;

@Mapper
public interface CommuteMapper {

	List<CommuteDTO> selectAllCommute();
}
