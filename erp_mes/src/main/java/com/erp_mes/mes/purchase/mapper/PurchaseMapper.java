package com.erp_mes.mes.purchase.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.erp_mes.mes.pm.dto.WorkOrderShortageDTO;
import com.erp_mes.mes.purchase.dto.PurchaseDTO;
import com.erp_mes.mes.purchase.dto.PurchaseDetailDTO;
import com.erp_mes.mes.stock.dto.MaterialDTO;

@Mapper
public interface PurchaseMapper {

	// 발주번호 부여를 위한 컬럼수 체크
	int countPurchase();

	// 발주(purchase) 테이블에 데이터 삽입
	void insertPurchase(PurchaseDTO purchaseDTO);

	// 발주 상세(purchase_detail) 테이블에 데이터 삽입
	void insertPurchaseDetail(PurchaseDetailDTO purchaseDetailDTO);
	// 자재 리스트
	List<MaterialDTO> getAllMaterial();
	
	// 발주 목록
	List<PurchaseDTO> getAllPurchase();

	// 발주 상세 목록
	List<PurchaseDetailDTO> getPurchaseDetailsByOrderId(@Param("purchaseId")String purchaseId);
	
	// 발주 수정 모달창 기존값 가져오기
	PurchaseDTO getPurchaseById(String purchaseId);
	

// 발주 수정 ====================
	void updatePurchase(PurchaseDTO purchaseDTO);  
    void deletePurchaseDetails(String purchaseId);
    // 한번의 수행에 여러 건의 PurchaseDetailDTO insert 
    void insertPurchaseDetails(List<PurchaseDetailDTO> materials);
// =========================================================

// 발주 상태 취소, update
	String findPurchaseStatus(String purchaseId);
	void updatePurchaseStatus(@Param("purchaseId") String purchaseId, @Param("purchaseStatus") String purchaseStatus);
	void updatePurchaseDetailsStatus(@Param("purchaseId") String purchaseId, @Param("purchaseStatus") String purchaseStatus);
	
// 작업지시발주 모달창 grid	
	List<WorkOrderShortageDTO> getWorkOrderShortages();
	// 특정 작업지시의 상세 자재 목록 조회 (발주 등록)
	List<WorkOrderShortageDTO> getWorkOrderDetailsForPurchase(@Param("workOrderId") String workOrderId);
	
	// 작업지시발주가 등록 시 work_order_shortage 테이블에서 해당 work_order_id 값을 가진 status를 '발주완료'로 update
	void updateWorkOrderShortageStatus(@Param("workOrderId") String workOrderId, @Param("status") String status);

	void updateReason(@Param("purchaseId") String purchaseId, @Param("reason") String reason);

}
