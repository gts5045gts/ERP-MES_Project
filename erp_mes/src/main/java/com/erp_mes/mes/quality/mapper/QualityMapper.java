package com.erp_mes.mes.quality.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.mes.pm.dto.WorkOrderDTO;
import com.erp_mes.mes.quality.dto.InspectionDTO;
import com.erp_mes.mes.quality.dto.InspectionItemDTO;
import com.erp_mes.mes.quality.dto.InspectionResultDTO;

@Mapper
public interface QualityMapper {

 // Inspection_ITEM 테이블 관련 메서드
 // 등록
 void insertItem(InspectionItemDTO inspectionItemDTO);
 // 조회: 조인을 통해 inspection_fm 정보도 함께 가져와야 함
 List<InspectionItemDTO> findAllItems();
 // 삭제
 void deleteItems(@Param("itemIds") List<Long> itemIds);

 // 공통코드
 List<CommonDetailCodeDTO> findUnits(); // 단위 공통코드를 위한 메서드 추가
 
 // 검사결과 등록
 void insertInspection(InspectionDTO inspectionDTO);
 void insertInspectionResult(InspectionResultDTO resultDTO);
 
 // 검사결과
 List<InspectionResultDTO> getInspectionResultList();
 
 // 검사 대기 목록 조회
 List<WorkOrderDTO> getInspectionTargets();
 
 // 검사 항목 및 허용 공차 조회
 List<InspectionItemDTO> findInspectionItemsByProductId(String productId);
 
}