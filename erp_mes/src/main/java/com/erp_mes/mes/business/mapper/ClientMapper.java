package com.erp_mes.mes.business.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.erp_mes.mes.business.dto.ClientDTO;
import com.erp_mes.mes.business.dto.OrderDTO;

@Mapper
public interface ClientMapper {
	
    List<ClientDTO> getAllClients();
	
//	List<ClientDTO> getClients(@Param("clientName") String clientName, @Param("clientType") String clientType);
    
	void insertClient(ClientDTO client);
	
	void updateClient(ClientDTO client);
	
	List<ClientDTO> getOrderClients(@Param("clientType") String clientType, @Param("clientStatus") String clientStatus);
}
