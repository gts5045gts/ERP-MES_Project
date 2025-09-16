package com.erp_mes.mes.quality.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.service.CommonCodeService;
import com.erp_mes.mes.pm.dto.ProductDTO;
import com.erp_mes.mes.pm.service.ProductBomService;
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
	private final ProductBomService productBomService; 

	public InspectionController(InspectionService inspectionService, CommonCodeService commonCodeService, ProductBomService productBomService) {
		this.inspectionService = inspectionService;
		this.commonCodeService = commonCodeService;
		this.productBomService = productBomService;
	}

	@GetMapping("/qcinfo")
	public String qualityDashboard(Model model) {
	    // 검사 유형 공통코드
	    List<CommonDetailCode> qcTypes = commonCodeService.findByComId("QC");
	    Map<String, String> qcTypeMap = qcTypes.stream()
	        .collect(Collectors.toMap(CommonDetailCode::getComDtId, CommonDetailCode::getComDtNm));
	    
	    // 왼쪽 테이블 데이터 (검사 유형별 기준)
	    List<InspectionFMDTO> inspectionFMs = inspectionService.findAllInspectionFMs();
	    inspectionFMs.forEach(fm -> {
	        String typeName = qcTypeMap.get(fm.getInspectionType());
	        if (typeName != null) {
	            fm.setInspectionType(typeName);
	        }
	    });
	    
	    // 오른쪽 테이블 데이터 (검사 항목별 허용 공차)
	    List<InspectionItemDTO> inspectionItems = inspectionService.getInspectionItems();

	    // UNIT 공통 코드 데이터
	    List<CommonDetailCode> units = commonCodeService.findByComId("UNIT");
	    
	    // 제품 목록 데이터
	    List<ProductDTO> products = productBomService.getProductList();

	    model.addAttribute("inspectionFMs", inspectionFMs);
	    model.addAttribute("inspectionItems", inspectionItems);
	    model.addAttribute("qcTypes", qcTypes);
	    model.addAttribute("units", units);
	    model.addAttribute("products", products); // 제품 목록 추가

	    return "qc/qcinfo";
	}
	
	@GetMapping("iqc")
	public String iqc() {
		return "qc/iqc";
	}

	// 왼쪽 테이블 (검사 유형별 기준 관리)에 대한 등록 API
	@PostMapping("/fm")
	public ResponseEntity<String> registerInspectionRecord(@RequestBody InspectionFMDTO inspectionFMDTO) {
		try {
			inspectionService.registerInspectionRecord(inspectionFMDTO);
			String successJson = "{\"success\": true, \"message\": \"검사 유형별 기준이 성공적으로 등록되었습니다.\"}";
			return new ResponseEntity<>(successJson, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Failed to register inspection record: {}", e.getMessage());
			String errorJson = "{\"success\": false, \"message\": \"등록 실패: " + e.getMessage() + "\"}";
			return new ResponseEntity<>(errorJson, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 오른쪽 테이블 (검사 항목별 허용 공차 관리)에 대한 등록 API
	@PostMapping("/item")
	public ResponseEntity<String> registerInspectionItem(@RequestBody InspectionItemDTO inspectionItemDTO) {
		try {
			// InspectionItemDTO를 받아서 서비스로 전달
			inspectionService.registerInspectionItem(inspectionItemDTO);
			String successJson = "{\"success\": true, \"message\": \"검사 항목별 허용 공차가 성공적으로 등록되었습니다.\"}";
			return new ResponseEntity<>(successJson, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Failed to register inspection item: {}", e.getMessage());
			String errorJson = "{\"success\": false, \"message\": \"등록 실패: " + e.getMessage() + "\"}";
			return new ResponseEntity<>(errorJson, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/fm") // DELETE 요청 처리
	public ResponseEntity<String> deleteInspectionRecords(@RequestBody List<Long> inspectionFMIds) {
	    try {
	        inspectionService.deleteInspectionRecords(inspectionFMIds);
	        String successJson = "{\"success\": true, \"message\": \"선택된 항목이 성공적으로 삭제되었습니다.\"}";
	        return new ResponseEntity<>(successJson, HttpStatus.OK);
	    } catch (Exception e) {
	        log.error("Failed to delete inspection records: {}", e.getMessage());
	        String errorJson = "{\"success\": false, \"message\": \"삭제 실패: " + e.getMessage() + "\"}";
	        return new ResponseEntity<>(errorJson, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
}
