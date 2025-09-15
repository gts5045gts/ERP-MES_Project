package com.erp_mes.mes.quality.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erp_mes.erp.personnel.dto.PersonnelLoginDTO;
import com.erp_mes.mes.quality.dto.InspectionItemDTO;
import com.erp_mes.mes.quality.service.InspectionService;

import lombok.extern.log4j.Log4j2;


@Controller
@RequestMapping("/quality")
@Log4j2
public class InspectionController {
    
	private final InspectionService inspectionService;

    public InspectionController(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @GetMapping("/qcinfo")
    public String qualityDashboard(Model model) {
        // 1. 품질 검사 항목 목록 조회
        List<InspectionItemDTO> inspectionItems = inspectionService.getInspectionItems();
        model.addAttribute("inspectionItems", inspectionItems);

        return "qc/qcinfo";
    }
	
	@GetMapping("iqc")
	public String iqc() {
		return "qc/iqc";
	}
}
