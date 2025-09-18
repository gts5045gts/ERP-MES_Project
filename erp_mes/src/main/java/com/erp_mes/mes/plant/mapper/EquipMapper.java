package com.erp_mes.mes.plant.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.erp_mes.mes.plant.dto.EquipDTO;

@Mapper
public interface EquipMapper {

	List<EquipDTO> findAll();
	
	

}
