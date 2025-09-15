package com.erp_mes.mes.quality.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.erp_mes.mes.quality.dto.InspectionItemDTO;
import com.erp_mes.mes.quality.mapper.InspectionItemMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionItemMapper inspectionItemMapper;

    public List<InspectionItemDTO> getInspectionItems() {
        return inspectionItemMapper.findAllInspectionItems();
    }
}