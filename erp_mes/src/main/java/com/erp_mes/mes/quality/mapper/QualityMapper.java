package com.erp_mes.mes.quality.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.mes.pm.dto.WorkOrderDTO;
import com.erp_mes.mes.pop.dto.DefectDTO;
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
 
 // WORK_ORDER_ID로 제품명을 조회하는 메서드
 String findTargetNameByWorkOrderId(String workOrderId);
 
 // 검사 이력 조회
 List<InspectionResultDTO> getInspectionResultList();
 
 // 검사 대기 목록 조회 (InspectionTargetDTO 사용)
 List<InspectionTargetDTO> getIncomingInspectionTargets();
 List<InspectionTargetDTO> getProcessInspectionTargetsGrouped();
 List<InspectionTargetDTO> getProcessDetails(String workOrderId);
 
 // 검사 완료 후 상태 업데이트
 int updateWorkOrderStatus(String workOrderId);
 int updateInputStatus(String inputId);
 // 공정 검사 후 WORK_RESULT 테이블의 수량을 업데이트하는 메서드
 int updateWorkResultCounts(
     @Param("workOrderId") String workOrderId, 
     @Param("acceptedCount") Long acceptedCount, 
     @Param("defectiveCount") Long defectiveCount
 );
 
 // 검사 항목 및 허용 공차 조회 
 List<InspectionItemDTO> findInspectionItemsByMaterialId(String materialId);
 List<InspectionItemDTO> findInspectionItemsByProductId(String productId);
 List<InspectionItemDTO> findInspectionItemsByProcessIdAndSeq(@Param("processId") Long processId,@Param("proSeq") String proSeq);
 void updateInputStatusByInId(@Param("inId") String inId, @Param("newStatus") String newStatus, @Param("acceptedCount") Long acceptedCount);
 Integer findInCountByInId(String inId);
 String findTargetNameByInId(String inId);
 void insertDefectItem(DefectDTO defectDTO);
 
// 검사결과 상세 조회
InspectionTargetDTO getIncomingDetail(Long inspectionId);
List<InspectionTargetDTO> getProcessDetail(Long inspectionId);
String findInspectionTypeById(Long inspectionId);

// =========================================================================================
// 불량관리관련
List<DefectDTO> getDefectHistory(Map<String, Object> params);
List<Map<String, Object>> getMonthlyDefectTrend();
List<Map<String, Object>> getTypeRatio();
Map<String, Object> getDefectSummary();

}