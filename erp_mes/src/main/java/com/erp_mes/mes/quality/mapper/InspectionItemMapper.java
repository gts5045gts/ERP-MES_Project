package com.erp_mes.mes.quality.mapper;

import com.erp_mes.mes.quality.dto.InspectionItemDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface InspectionItemMapper {
    List<InspectionItemDTO> findAllInspectionItems();
}