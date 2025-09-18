package com.erp_mes.mes.business.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.business.dto.ClientDTO;
import com.erp_mes.mes.business.mapper.ClientMapper;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ClientService {
	private ClientMapper clientMapper;

	public ClientService(ClientMapper clientMapper) {
		this.clientMapper = clientMapper;
	}

	// 거래처 전체 목록
	public List<ClientDTO> getAllClients() {
		return clientMapper.getAllClients();
	}

//    // 거래처 필터링(거래처명, 거래처 유형)
//    public List<ClientDTO> getClients(String clientName, String clientType) {
//        return clientMapper.getClients(clientName, clientType);
//    }

	// 거래처 등록
	@Transactional
	public void saveClient(ClientDTO client) {
		clientMapper.insertClient(client);
	}

	// 거래처 수정
	@Transactional
	public void updateClient(ClientDTO client) {
		clientMapper.updateClient(client);
	}

	// 매출사 거래처 목록
	public List<ClientDTO> getOrderClients(String clientType, String clientStatus) {
		return clientMapper.getOrderClients(clientType, clientStatus);
	}
}
