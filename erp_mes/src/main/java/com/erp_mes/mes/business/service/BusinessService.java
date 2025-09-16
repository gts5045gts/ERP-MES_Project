package com.erp_mes.mes.business.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.business.dto.OrderDTO;
import com.erp_mes.mes.business.mapper.BusinessMapper;
import com.erp_mes.mes.pm.dto.ProductDTO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class BusinessService {
	private BusinessMapper businessMapper;

	public BusinessService(BusinessMapper businessMapper) {
		this.businessMapper = businessMapper;
	}

	// 수주 전체 목록
	public List<OrderDTO> getAllOrder() {
		return businessMapper.getAllOrder();
	}

	@Transactional
	public void saveOrder(OrderDTO orderDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentEmpId = authentication.getName(); // 혹은 사용자 정보를 담은 객체에서 가져오기

		// 새로운 수주 번호 생성
		String newOrderId = generateOrderId();

		// DTO에 생성된 번호 설정
		orderDto.setOrderId(newOrderId);
		orderDto.setEmpId(currentEmpId);

		// DB insert
		businessMapper.insertOrder(orderDto);

	}

	// 수주 번호 생성
	private String generateOrderId() {
		// 오늘 날짜 문자열 (YYYYMMDD)
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		// 오늘 날짜의 가장 큰 수주 번호 조회
		String maxOrderId = businessMapper.selectMaxOrderIdForToday(today);

		int newSequenceNumber = 1;

		if (maxOrderId != null) {
			// "ORD-YYYYMMDD-0001" 형식에서 마지막 숫자만 파싱
			String sequenceStr = maxOrderId.substring(maxOrderId.length() - 4);
			newSequenceNumber = Integer.parseInt(sequenceStr) + 1;
		}

		// 새로운 시퀀스 번호를 4자리로 포맷팅 (0001, 0002 등)
		String formattedSequence = String.format("%04d", newSequenceNumber);

		// 최종 수주 번호 완성
		return "ORD-" + today + "-" + formattedSequence;
	}

	// 수주 등록 모달에 보여줄 품목 리스트
	public List<ProductDTO> getAllProduct() {
		
		return businessMapper.getAllProduct();
	}

	public List<OrderDetailDTO> getOrderDetailsByOrderId(String orderId) {
		
		return businessMapper.getOrderDetailsByOrderId(orderId);
	}
}
