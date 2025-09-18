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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.service.CommonCodeService;
import com.erp_mes.mes.pm.dto.ProductDTO;
import com.erp_mes.mes.pm.dto.WorkOrderDTO;
import com.erp_mes.mes.pm.service.ProductBomService;
import com.erp_mes.mes.quality.dto.InspectionFMDTO;
import com.erp_mes.mes.quality.dto.InspectionItemDTO;
import com.erp_mes.mes.quality.dto.InspectionResultDTO;
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
	    // inspectionItems의 inspectionType을 이름으로 변환하여 모델에 추가
	    inspectionItems.forEach(item -> {
	        String typeName = qcTypeMap.get(item.getInspectionType());
	        if (typeName != null) {
	            item.setInspectionType(typeName);
	        }
	    });
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
	
    @GetMapping("/iqc")
    public String iqc(Model model) {
        List<InspectionResultDTO> inspectionResultList = inspectionService.getInspectionResultList();
        model.addAttribute("inspectionResultList", inspectionResultList);
        return "qc/iqc";
    }
    
    @GetMapping("/api/inspection-results") 
    @ResponseBody
    public List<InspectionResultDTO> getInspectionResults() {
        // 기존 서비스 메서드를 호출하여 데이터를 가져옵니다.
        return inspectionService.getInspectionResultList();
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
	@DeleteMapping("/item") // DELETE 요청 처리
	public ResponseEntity<String> deleteInspectionItems(@RequestBody List<Long> itemIds) {
	    try {
	        inspectionService.deleteInspectionItems(itemIds);
	        String successJson = "{\"success\": true, \"message\": \"선택된 검사 항목이 성공적으로 삭제되었습니다.\"}";
	        return new ResponseEntity<>(successJson, HttpStatus.OK);
	    } catch (Exception e) {
	        log.error("Failed to delete inspection items: {}", e.getMessage());
	        String errorJson = "{\"success\": false, \"message\": \"삭제 실패: " + e.getMessage() + "\"}";
	        return new ResponseEntity<>(errorJson, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
    // 1. 검사 대기 목록 API
    @GetMapping("/api/inspection-targets")
    @ResponseBody
    public List<WorkOrderDTO> getInspectionTargets() {
        return inspectionService.getInspectionTargets();
    }

    // 2. 특정 제품의 검사 기준 API
    @GetMapping("/api/inspection-item/{productId}")
    @ResponseBody
    public List<InspectionItemDTO> getInspectionItemByProductId(@PathVariable String productId) {
        return inspectionService.getInspectionItemByProductId(productId);
    }

    // 3. 검사 결과 등록 API
    @PostMapping("/api/register-inspection-result")
    @ResponseBody
    public ResponseEntity<String> registerInspectionResult(@RequestBody InspectionResultDTO resultDTO) {
        try {
            inspectionService.registerInspectionResult(resultDTO);
            return new ResponseEntity<>("{\"success\": true}", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to register inspection result: {}", e.getMessage());
            return new ResponseEntity<>("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
