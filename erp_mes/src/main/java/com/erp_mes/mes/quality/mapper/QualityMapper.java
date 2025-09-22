package com.erp_mes.mes.quality.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.mes.pm.dto.WorkOrderDTO;
import com.erp_mes.mes.quality.dto.InspectionDTO;
import com.erp_mes.mes.quality.dto.InspectionFMDTO;
import com.erp_mes.mes.quality.dto.InspectionItemDTO;
import com.erp_mes.mes.quality.dto.InspectionResultDTO;
import com.erp_mes.mes.quality.dto.InspectionTargetDTO;

@Mapper
public interface QualityMapper {

 // Inspection_ITEM 테이블 관련 메서드
 void insertItem(InspectionItemDTO inspectionItemDTO);
 List<InspectionItemDTO> findAllItems();
 void deleteItems(@Param("itemIds") List<Long> itemIds);

 // 공통코드
 List<CommonDetailCodeDTO> findUnits();
 
 // 기준정보 수정
 int updateInspectionFm(InspectionFMDTO inspectionFMDTO);
 int updateInspectionItem(InspectionItemDTO inspectionItemDTO);
 
 // 검사결과 등록 (DB에 저장되는 데이터 형식)
 int insertInspection(InspectionDTO inspectionDTO);
 int insertInspectionResult(InspectionResultDTO resultDTO);
 
 
 // 검사 이력 조회
 List<InspectionResultDTO> getInspectionResultList();
 
 // 검사 대기 목록 조회 (InspectionTargetDTO 사용)
 List<InspectionTargetDTO> getIncomingInspectionTargets();
 List<InspectionTargetDTO> getProcessInspectionTargets();
 List<InspectionTargetDTO> getPackagingInspectionTargets();
 
 // 검사 완료 후 상태 업데이트
 int updateWorkOrderStatus(String workOrderId);
 int updateInputStatus(String inputId);
 
 // 검사 항목 및 허용 공차 조회 
 List<InspectionItemDTO> findInspectionItemsByMaterialId(String materialId);
 List<InspectionItemDTO> findInspectionItemsByProcessId(Long processId);
 List<InspectionItemDTO> findInspectionItemsByProductId(String productId);
 void updateInputStatusByInId(@Param("inId") Long inId, @Param("newStatus") String newStatus);
 Integer findInCountByInId(Long inId);
 
}