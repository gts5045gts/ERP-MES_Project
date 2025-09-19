package com.erp_mes.mes.plant.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.erp_mes.mes.plant.dto.ProcessRouteDTO;

@Mapper
public interface ProcessRouteMapper {

	List<ProcessRouteDTO> findAll();


	void save(ProcessRouteDTO routeDTO);


	List<ProcessRouteDTO> findByProductIdAll(String productId);



}
