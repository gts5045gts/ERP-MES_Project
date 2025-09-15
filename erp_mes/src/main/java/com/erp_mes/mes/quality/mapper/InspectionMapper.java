package com.erp_mes.mes.quality.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.erp_mes.mes.quality.dto.InspectionDTO;
import java.util.List;

@Mapper
public interface InspectionMapper {
    void insertInspection(InspectionDTO inspectionDto);
    List<InspectionDTO> findInspectionsByProductId(Long productId);
}