package com.erp_mes.mes.quality.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.erp_mes.mes.quality.dto.InspectionResultDTO;
import java.util.List;

@Mapper
public interface InspectionResultMapper {
    void insertInspectionResult(InspectionResultDTO resultDto);
    List<InspectionResultDTO> findResultsByInspectionId(Long inspectionId);
}