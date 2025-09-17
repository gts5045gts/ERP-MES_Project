package com.erp_mes.mes.plant.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.erp_mes.mes.plant.dto.ProcessDTO;

@Mapper
public interface ProcessMapper {

	public List<ProcessDTO> findAll();

}
