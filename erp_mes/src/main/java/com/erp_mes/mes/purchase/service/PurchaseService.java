package com.erp_mes.mes.purchase.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.business.dto.OrderDetailDTO;
import com.erp_mes.mes.purchase.dto.PurchaseDTO;
import com.erp_mes.mes.purchase.dto.PurchaseDetailDTO;
import com.erp_mes.mes.purchase.mapper.PurchaseMapper;
import com.erp_mes.mes.stock.dto.MaterialDTO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PurchaseService {
	private final PurchaseMapper purchaseMapper;

	public PurchaseService(PurchaseMapper purchaseMapper) {
		this.purchaseMapper = purchaseMapper;
	}

	@Transactional
	public String createPurchase(PurchaseDTO purchaseDTO) {
		// 1) 발주번호 생성 (PUR-yyyyMMdd-XXXX)
		int count = purchaseMapper.countPurchase();
		int next = count + 1;
		String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String seqPart = String.format("%04d", next);
		String purchaseId = "PUR-" + datePart + "-" + seqPart;
		purchaseDTO.setPurchaseId(purchaseId);

		purchaseMapper.insertPurchase(purchaseDTO);

		List<PurchaseDetailDTO> materials = purchaseDTO.getMaterials();
		if (materials != null && !materials.isEmpty()) {
			int seq = 1;
			for (PurchaseDetailDTO material : materials) {
				material.setPurchaseId(purchaseDTO.getPurchaseId());
				material.setId(seq); // 서비스 계층에서 번호 할당
				purchaseMapper.insertPurchaseDetail(material);
				seq++;
			}
		}

		return purchaseDTO.getPurchaseId();
	}


	// 자재 목록
	public List<MaterialDTO> getAllMaterial() {
		
		return purchaseMapper.getAllMaterial();
	}
	// 발주 전체 목록
	public List<PurchaseDTO> getAllPurchase() {
		return purchaseMapper.getAllPurchase();
	}
		
	// 발주 상세 목록 리스트
	public List<PurchaseDetailDTO> getPurchaseDetailsByOrderId(String purchaseId) {
			
		return purchaseMapper.getPurchaseDetailsByOrderId(purchaseId);
	}

	// 발주 수정 모달창에서 기존 발주 데이터 조회
//	public PurchaseDTO getPurchaseById(String purchaseId) {
//		
//		return purchaseMapper.getPurchaseById(purchaseId);
//	}

}
