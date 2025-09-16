package com.erp_mes.mes.quality.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.service.CommonCodeService;
import com.erp_mes.mes.quality.dto.InspectionDTO;
import com.erp_mes.mes.quality.dto.InspectionFMDTO;
import com.erp_mes.mes.quality.dto.InspectionItemDTO;
import com.erp_mes.mes.quality.service.InspectionService;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/quality")
@Log4j2
public class InspectionController {

	private final InspectionService inspectionService;
	private final CommonCodeService commonCodeService;

	public InspectionController(InspectionService inspectionService, CommonCodeService commonCodeService) {
		this.inspectionService = inspectionService;
		this.commonCodeService = commonCodeService;
	}

	@GetMapping("/qcinfo")
	public String qualityDashboard(Model model) {
	    // 왼쪽 테이블용 데이터: 검사 유형별 기준
	    List<InspectionFMDTO> inspectionFMs = inspectionService.findAllInspectionFMs();
	    model.addAttribute("inspectionFMs", inspectionFMs);

	    // 오른쪽 테이블용 데이터: 검사 항목별 허용 공차
	    List<InspectionItemDTO> inspectionItems = inspectionService.getInspectionItems();
	    model.addAttribute("inspectionItems", inspectionItems);

	    // 검사 유형 공통코드 가져오기 (모달 드롭다운용)
	    List<CommonDetailCode> qcTypes = commonCodeService.findByComId("QC");
	    model.addAttribute("qcTypes", qcTypes);
	    
	    return "qc/qcinfo";
	}

	@GetMapping("iqc")
	public String iqc() {
		return "qc/iqc";
	}

	// 왼쪽 테이블 (검사 유형별 기준 관리)에 대한 등록 API
	@PostMapping("/records")
	public ResponseEntity<String> registerInspectionRecord(@RequestBody InspectionDTO inspectionDTO) {
		try {
			inspectionService.registerInspectionRecord(inspectionDTO);
			String successJson = "{\"success\": true, \"message\": \"검사 유형별 기준이 성공적으로 등록되었습니다.\"}";
			return new ResponseEntity<>(successJson, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Failed to register inspection record: {}", e.getMessage());
			String errorJson = "{\"success\": false, \"message\": \"등록 실패: " + e.getMessage() + "\"}";
			return new ResponseEntity<>(errorJson, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 오른쪽 테이블 (검사 항목별 허용 공차 관리)에 대한 등록 API
	@PostMapping("/items")
	public ResponseEntity<String> registerInspectionItem(@RequestBody InspectionItemDTO inspectionItemDTO) {
		try {
			inspectionService.registerInspectionItem(inspectionItemDTO);
			String successJson = "{\"success\": true, \"message\": \"검사 항목별 허용 공차가 성공적으로 등록되었습니다.\"}";
			return new ResponseEntity<>(successJson, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Failed to register inspection item: {}", e.getMessage());
			String errorJson = "{\"success\": false, \"message\": \"등록 실패: " + e.getMessage() + "\"}";
			return new ResponseEntity<>(errorJson, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
