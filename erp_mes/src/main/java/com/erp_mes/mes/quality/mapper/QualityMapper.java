package com.erp_mes.mes.quality.mapper;

import com.erp_mes.mes.quality.dto.InspectionItemDTO;
import com.erp_mes.mes.quality.dto.InspectionDTO;
import com.erp_mes.mes.quality.dto.InspectionFMDTO; 
import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface QualityMapper {

 // Inspection 테이블 관련 메서드
 void insertRecord(InspectionDTO inspectionDTO);
 List<InspectionDTO> findAllRecords();
 
 // Inspection_FM 테이블 관련 메서드
 // Inspection_FM에 대한 매퍼가 따로 없다면 여기에 추가할 수 있습니다.
 List<InspectionFMDTO> findAllInspectionFMs();
 void insertFM(InspectionFMDTO inspectionFMDTO);

 // Inspection_ITEM 테이블 관련 메서드
 // 등록
 void insertItem(InspectionItemDTO inspectionItemDTO);
 // 조회: 조인을 통해 inspection_fm 정보도 함께 가져와야 함
 List<InspectionItemDTO> findAllItems();
 // 삭제
 void deleteItems(@Param("itemIds") List<Long> itemIds);

 // 공통코드
 List<CommonDetailCodeDTO> findInspectionTypes();
 List<CommonDetailCodeDTO> findUnits(); // 단위 공통코드를 위한 메서드 추가
}