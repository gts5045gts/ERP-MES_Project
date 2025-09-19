package com.erp_mes.mes.purchase.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.erp_mes.mes.business.dto.OrderDetailDTO;
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
//	PurchaseDTO getPurchaseById(String purchaseId);

}
