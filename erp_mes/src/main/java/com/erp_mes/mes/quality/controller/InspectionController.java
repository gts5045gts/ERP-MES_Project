package com.erp_mes.mes.quality.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.service.CommonCodeService;
import com.erp_mes.erp.personnel.dto.PersonnelLoginDTO;
import com.erp_mes.mes.plant.dto.ProcessDTO;
import com.erp_mes.mes.plant.service.ProcessService;
import com.erp_mes.mes.quality.dto.InspectionFMDTO;
import com.erp_mes.mes.quality.dto.InspectionItemDTO;
import com.erp_mes.mes.quality.dto.InspectionRegistrationRequestDTO;
import com.erp_mes.mes.quality.dto.InspectionResultDTO;
import com.erp_mes.mes.quality.dto.InspectionTargetDTO;
import com.erp_mes.mes.quality.service.InspectionService;
import com.erp_mes.mes.stock.dto.MaterialDTO;
import com.erp_mes.mes.stock.service.StockService;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/quality")
@Log4j2
public class InspectionController {

	private final InspectionService inspectionService;
	private final CommonCodeService commonCodeService;
	private final ProcessService processService;
	private final StockService stockService;

	public InspectionController(InspectionService inspectionService, CommonCodeService commonCodeService,
			ProcessService processService, StockService stockService) {
		this.inspectionService = inspectionService;
		this.commonCodeService = commonCodeService;
		this.processService = processService;
		this.stockService = stockService;
	}

	@GetMapping("/qcinfo")
	public String qualityDashboard(Model model) {
		List<CommonDetailCode> qcTypes = commonCodeService.findByComId("QC");
		Map<String, String> qcTypeMap = qcTypes.stream()
				.collect(Collectors.toMap(CommonDetailCode::getComDtId, CommonDetailCode::getComDtNm));

		List<InspectionFMDTO> inspectionFMs = inspectionService.findAllInspectionFMs();
		inspectionFMs.forEach(fm -> {
			String typeName = qcTypeMap.get(fm.getInspectionType());
			if (typeName != null) {
				fm.setInspectionTypeName(typeName);
			}
		});

		List<InspectionItemDTO> inspectionItems = inspectionService.getInspectionItems();

		Map<Long, String> inspectionFmNameMap = inspectionFMs.stream()
				.collect(Collectors.toMap(InspectionFMDTO::getInspectionFMId, InspectionFMDTO::getInspectionTypeName));

		inspectionItems.forEach(item -> {
			String typeName = inspectionFmNameMap.get(item.getInspectionFMId());
			if (typeName != null) {
				item.setInspectionTypeName(typeName);
			}
		});

		List<CommonDetailCode> units = commonCodeService.findByComId("UNIT");
		List<ProcessDTO> processes = processService.getProcessList();
		List<MaterialDTO> materials = stockService.getMaterialList();

		model.addAttribute("inspectionFMs", inspectionFMs);
		model.addAttribute("inspectionItems", inspectionItems);
		model.addAttribute("qcTypes", qcTypes);
		model.addAttribute("units", units);
		model.addAttribute("processes", processes);
		model.addAttribute("materials", materials);

		return "qc/qcinfo";
	}

	@GetMapping("/qih")
	public String qualityHistory(Model model) {
		List<InspectionResultDTO> inspectionResultList = inspectionService.getInspectionResultList();

		List<CommonDetailCode> qcTypes = commonCodeService.findByComId("QC");
		Map<String, String> qcTypeMap = qcTypes.stream()
				.collect(Collectors.toMap(CommonDetailCode::getComDtId, CommonDetailCode::getComDtNm));

		inspectionResultList.forEach(result -> {
			String typeName = qcTypeMap.get(result.getInspectionType());
			if (typeName != null) {
				result.setInspectionTypeName(typeName);
			}
		});

		model.addAttribute("inspectionResultList", inspectionResultList);
		return "qc/qih";
	}

	// 상단 그리드 (검사 이력)를 위한 API
	@GetMapping("/api/history-list")
	@ResponseBody
	public Map<String, Object> getInspectionHistoryList(
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "perPage", defaultValue = "100") int perPage) {

		Map<String, Object> response = new HashMap<>();
		try {
			List<InspectionResultDTO> allResults = inspectionService.getInspectionResultList();

			int totalCount = allResults.size();
			int start = (page - 1) * perPage;
			int end = Math.min(start + perPage, totalCount);

			List<InspectionResultDTO> pagedResults = allResults.subList(start, end);

			// 공통 코드 이름 매핑
			List<CommonDetailCode> qcTypes = commonCodeService.findByComId("QC");
			Map<String, String> qcTypeMap = qcTypes.stream()
					.collect(Collectors.toMap(CommonDetailCode::getComDtId, CommonDetailCode::getComDtNm));
			pagedResults.forEach(result -> result.setInspectionTypeName(qcTypeMap.get(result.getInspectionType())));

			Map<String, Object> data = new HashMap<>();
			data.put("contents", pagedResults);
			data.put("pagination", Map.of("totalCount", totalCount));

			response.put("result", true);
			response.put("data", data);
		} catch (Exception e) {
			log.error("Failed to fetch inspection history list: {}", e.getMessage());
			response.put("result", false);
			response.put("data", Map.of());
		}
		return response;
	}

	// 수입 검사 대기 목록을 위한 API
	@GetMapping("/api/incoming-targets")
	@ResponseBody
	public Map<String, Object> getIncomingTargetsWithPagination(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "perPage", defaultValue = "100") int perPage) {

		Map<String, Object> response = new HashMap<>();
		try {
			List<InspectionTargetDTO> allTargets = inspectionService.getIncomingInspectionTargets();

			int totalCount = allTargets.size();
			int start = (page - 1) * perPage;
			int end = Math.min(start + perPage, totalCount);

			List<InspectionTargetDTO> pagedTargets = allTargets.subList(start, end);

			Map<String, Object> data = new HashMap<>();
			data.put("contents", pagedTargets);
			data.put("pagination", Map.of("totalCount", totalCount));

			response.put("result", true);
			response.put("data", data);
		} catch (Exception e) {
			log.error("Failed to fetch incoming inspection targets: {}", e.getMessage());
			response.put("result", false);
			response.put("data", Map.of());
		}
		return response;
	}

	// 공정 검사 대기 목록을 위한 API
    @GetMapping("/api/process-targets")
    @ResponseBody
    public Map<String, Object> getProcessTargetsWithPagination(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "perPage", defaultValue = "100") int perPage) {

        Map<String, Object> response = new HashMap<>();
        try {
            // Service에서 WORK_ORDER_ID별로 그룹화된 데이터를 가져옴
            List<InspectionTargetDTO> allTargets = inspectionService.getProcessInspectionTargetsGrouped();

            int totalCount = allTargets.size();
            int start = (page - 1) * perPage;
            int end = Math.min(start + perPage, totalCount);

            List<InspectionTargetDTO> pagedTargets = allTargets.subList(start, end);
            
            // 공통 코드 이름 매핑
            Map<String, String> qcTypeMap = commonCodeService.findByComId("QC").stream()
                .collect(Collectors.toMap(CommonDetailCode::getComDtId, CommonDetailCode::getComDtNm));
            pagedTargets.forEach(target -> target.setInspectionTypeName(qcTypeMap.get(target.getInspectionType())));

            Map<String, Object> data = new HashMap<>();
            data.put("contents", pagedTargets);
            data.put("pagination", Map.of("totalCount", totalCount));

            response.put("result", true);
            response.put("data", data);
        } catch (Exception e) {
            log.error("Failed to fetch process inspection targets: {}", e.getMessage());
            response.put("result", false);
            response.put("data", Map.of());
        }
        return response;
    }
    
    @GetMapping("/api/process-details/{workOrderId}")
    @ResponseBody
    public ResponseEntity<List<InspectionTargetDTO>> getProcessDetails(@PathVariable("workOrderId") String workOrderId) {
        try {
            List<InspectionTargetDTO> details = inspectionService.getProcessDetails(workOrderId);
            return new ResponseEntity<>(details, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to fetch process details for workOrderId {}: {}", workOrderId, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	@GetMapping("/api/inspection-results")
	@ResponseBody
	public List<InspectionResultDTO> getInspectionResults() {
		return inspectionService.getInspectionResultList();
	}

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

	@PostMapping("/item")
	public ResponseEntity<String> registerInspectionItem(@RequestBody InspectionItemDTO inspectionItemDTO) {
		log.info("수신된 DTO: " + inspectionItemDTO);
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

	@PutMapping("/fm")
	public ResponseEntity<Map<String, Object>> updateInspectionRecord(@RequestBody InspectionFMDTO inspectionFMDTO) {
		Map<String, Object> result = new HashMap<>();
		log.info("수정 요청된 FM DTO: " + inspectionFMDTO);
		try {
			int updatedRows = inspectionService.updateInspectionFm(inspectionFMDTO);
			if (updatedRows > 0) {
				result.put("success", true);
				result.put("message", "검사 유형이 성공적으로 수정되었습니다.");
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				result.put("success", false);
				result.put("message", "수정할 항목을 찾을 수 없습니다.");
				return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("Failed to update inspection record: {}", e.getMessage());
			result.put("success", false);
			result.put("message", "수정 실패: " + e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/item")
	public ResponseEntity<Map<String, Object>> updateInspectionItem(@RequestBody InspectionItemDTO inspectionItemDTO) {
		Map<String, Object> result = new HashMap<>();
		log.info("수정 요청된 DTO: " + inspectionItemDTO);
		try {
			int updatedRows = inspectionService.updateInspectionItem(inspectionItemDTO);
			if (updatedRows > 0) {
				result.put("success", true);
				result.put("message", "검사 항목이 성공적으로 수정되었습니다.");
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				result.put("success", false);
				result.put("message", "수정할 항목을 찾을 수 없습니다.");
				return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("Failed to update inspection item: {}", e.getMessage());
			result.put("success", false);
			result.put("message", "수정 실패: " + e.getMessage());
			return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/fm")
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

	@DeleteMapping("/item")
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

	// 불량 코드 가져오기
	@GetMapping("/api/defect-codes")
	@ResponseBody
	public List<CommonDetailCode> getDefectCodes() {
		return commonCodeService.findByComId("DEFECT");
	}

	@GetMapping("/api/defect-reasons/{defectType}")
	@ResponseBody
	public List<CommonDetailCode> getDefectReasons(@PathVariable("defectType") String defectType) {
		// 불량 유형에 맞는 사유 코드를 필터링하여 반환
		return commonCodeService.findByComId(defectType);
	}

	@PostMapping("/api/verify-incoming-count")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> verifyIncomingCount(@RequestBody Map<String, Object> request,
			@AuthenticationPrincipal PersonnelLoginDTO personnelLoginDTO) {
		Map<String, Object> response = new HashMap<>();
		try {
			String targetId = request.get("targetId").toString();
			Long acceptedCount = Long.parseLong(request.get("acceptedCount").toString());
			Long defectiveCount = Long.parseLong(request.get("defectiveCount").toString());
			String empId = personnelLoginDTO.getEmpId();
			String lotId = request.get("lotId").toString();
			String inspectionType = request.get("inspectionType").toString();
			String defectType = (String) request.get("defectType");
			String remarks = (String) request.get("remarks");
			String materialId = (String) request.get("materialId");

			inspectionService.verifyIncomingCount(targetId, acceptedCount, defectiveCount, empId, lotId, inspectionType,
					defectType, remarks, materialId);

			response.put("success", true);
			response.put("message", "수량 검사 및 입고 처리가 성공적으로 완료되었습니다.");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Failed to verify incoming count: {}", e.getMessage());
			response.put("success", false);
			response.put("message", "수량 검사 실패: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/api/inspection-item/process/{processId}/seq/{proSeq}")
	@ResponseBody
	public List<InspectionItemDTO> getInspectionItemByProcessIdAndSeq(@PathVariable("processId") Long processId,
			@PathVariable("proSeq") String proSeq) {
		return inspectionService.findInspectionItemsByProcessIdAndSeq(processId, proSeq);
	}

	@PostMapping("/api/register-inspection-result")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> registerInspection(
			@RequestBody InspectionRegistrationRequestDTO requestDTO,
			@AuthenticationPrincipal PersonnelLoginDTO personnelLoginDTO) {
		Map<String, Object> response = new HashMap<>();
		try {
			requestDTO.setEmpId(personnelLoginDTO.getEmpId());

			inspectionService.registerInspection(requestDTO);
			response.put("success", true);
			response.put("message", "검사 등록이 성공적으로 완료되었습니다.");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Failed to register inspection: {}", e.getMessage());
			response.put("success", false);
			response.put("message", "등록 실패: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
    // 공정 검사 수량 등록 API
	@PostMapping("/api/register-process-inspection-result")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> registerProcessInspectionResult(
			@RequestBody InspectionRegistrationRequestDTO requestDTO,
			@AuthenticationPrincipal PersonnelLoginDTO personnelLoginDTO) {
		
		Map<String, Object> response = new HashMap<>();
		try {
			// 클라이언트로부터 받은 데이터에 로그인한 사원 ID 추가
			requestDTO.setEmpId(personnelLoginDTO.getEmpId());
            
			inspectionService.registerProcessInspectionResult(requestDTO);
			
			response.put("success", true);
			response.put("message", "공정 검사 결과 등록 및 처리가 성공적으로 완료되었습니다.");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Failed to register process inspection result: {}", e.getMessage());
			response.put("success", false);
			response.put("message", "공정 검사 등록 실패: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/api/inspection-detail/{inspectionId}")
	@ResponseBody
	public ResponseEntity<InspectionTargetDTO> getInspectionDetail(@PathVariable("inspectionId") Long inspectionId) {
	    try {
	        // inspectionService.getInspectionDetail(inspectionId) 호출
	    	InspectionTargetDTO detail = inspectionService.getInspectionDetail(inspectionId);
	        return new ResponseEntity<>(detail, HttpStatus.OK);
	    } catch (Exception e) {
	        log.error("Failed to fetch inspection detail: {}", e.getMessage());
	        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
}

