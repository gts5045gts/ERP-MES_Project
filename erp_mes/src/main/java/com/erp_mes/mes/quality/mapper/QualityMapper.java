package com.erp_mes.mes.quality.mapper;

import com.erp_mes.mes.quality.dto.InspectionItemDTO;
import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.mes.quality.dto.InspectionDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface QualityMapper {

    // 검사 유형별 기준 (왼쪽 테이블)
    void insertRecord(InspectionDTO inspectionDTO);
    List<InspectionDTO> findAllRecords();
    
    // 검사 항목별 허용 공차 (오른쪽 테이블)
    void insertItem(InspectionItemDTO inspectionItemDTO);
    List<InspectionItemDTO> findAllItems();

    // 공통코드
    List<CommonDetailCodeDTO> findInspectionTypes();
}