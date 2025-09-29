package com.erp_mes.mes.purchase.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.pm.dto.WorkOrderShortageDTO;
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
		log.info("DTO WorkOrderId: {}", purchaseDTO.getWorkOrderId());
		log.info("작업지시발주 확인 전 로그");
		// 작업지시 발주인 경우, work_order_shortage의 status를 '발주완료'로 변경
		if (purchaseDTO.getWorkOrderId() != null && !purchaseDTO.getWorkOrderId().isEmpty()) {
			log.info("작업지시발주 확인 if문 내부 로그");
			
			purchaseMapper.updateWorkOrderShortageStatus(purchaseDTO.getWorkOrderId(), "발주완료");
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
	public PurchaseDTO getPurchaseById(String purchaseId) {
		
		return purchaseMapper.getPurchaseById(purchaseId);
	}
	
	// 발주 수정
	@Transactional
    public void updatePurchase(PurchaseDTO purchaseDTO) {
		
        // purchase 테이블 업데이트
		purchaseMapper.updatePurchase(purchaseDTO);
       
        // 기존 purchase_detail 삭제 (전체 삭제 후 재등록)
		purchaseMapper.deletePurchaseDetails(purchaseDTO.getPurchaseId());
        
        // 새로운 orders_detail 재등록
        List<PurchaseDetailDTO> materials = purchaseDTO.getMaterials();
        if (materials != null && !materials.isEmpty()) {
             int seq = 1;
             for (PurchaseDetailDTO material : materials) {
            	 material.setPurchaseId(purchaseDTO.getPurchaseId());
            	 material.setId(seq);
                 purchaseMapper.insertPurchaseDetail(material);
                 seq++;
             }
        }
    }
	
	// 발주 취소 처리
	@Transactional
	public void cancelPurchase(String purchaseId, String reason) {
	    String currentStatus = purchaseMapper.findPurchaseStatus(purchaseId);

	    if ("CANCELED".equals(currentStatus)) {
	        throw new IllegalArgumentException("이미 취소된 발주입니다.");
	    }

	    purchaseMapper.updatePurchaseStatus(purchaseId, "CANCELED");
	        
	    // 해당 수주에 속한 모든 수주 상세 목록(orders_detail)의 상태를 'CANCELED'로 업데이트
	    purchaseMapper.updatePurchaseDetailsStatus(purchaseId, "CANCELED");
	    
	    // 취소 사유
	    purchaseMapper.updateReason(purchaseId, reason);
	}
	
	// 자재 부족한 작업지시 목록 조회
	public List<WorkOrderShortageDTO> getWorkOrderShortages() {
		return purchaseMapper.getWorkOrderShortages();
	}

	// 특정 작업지시의 상세 자재 목록 조회
	public List<WorkOrderShortageDTO> getWorkOrderDetailsForPurchase(String workOrderId) {
		log.info("Fetching work order details for ID: {}", workOrderId);
	    List<WorkOrderShortageDTO> details = purchaseMapper.getWorkOrderDetailsForPurchase(workOrderId);

	    // 로그 추가
	    log.info("Found {} details for work order ID: {}", details.size(), workOrderId);
	    for (WorkOrderShortageDTO dto : details) {
	        log.info("Detail DTO: {}", dto.toString()); // dto.toString()으로 필드값 확인
	    }

	    return details;
	}

}
