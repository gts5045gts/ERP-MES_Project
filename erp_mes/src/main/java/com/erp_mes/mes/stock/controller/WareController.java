package com.erp_mes.mes.stock.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.erp_mes.erp.config.util.SessionUtil;
import com.erp_mes.mes.lot.trace.TrackLot;
import com.erp_mes.mes.stock.dto.WarehouseDTO;
import com.erp_mes.mes.stock.service.StockService;
import com.erp_mes.mes.stock.service.WareService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2  
@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class WareController {
	
	private final WareService wareService;
    private final StockService stockService;
	
	// ==================== 1. 페이지 라우팅 ====================
	
	// 창고내 재고 현황 페이지
	@GetMapping("/warehouse")
    public String warehouseList(Model model) {
        log.info("창고 현황 페이지 접속");
        return "inventory/warehouse_list";
    }
	
	// 창고 정보 관리 페이지
	@GetMapping("/ware")
    public String wareList(Model model) {
        log.info("창고 정보 관리 페이지 접속");
        return "inventory/ware_list";
    }
	
	// 입고 관리 페이지
	@GetMapping("/goods")
	public String inputList(Model model) {
	    log.info("입고 관리 페이지 접속");
	    
	    LocalDate today = LocalDate.now();
	    String pageTitle = today.getMonthValue() + "월 " + today.getDayOfMonth() + "일 입고내역입니다.";
	    model.addAttribute("pageTitle", pageTitle);
	    
	    List<WarehouseDTO> materialWarehouses = wareService.getWarehouseListByType("원자재");
	    model.addAttribute("warehouseList", materialWarehouses);
	    
	    return "inventory/inbound_list";
	}
	
	// ==================== 2. 창고 관리 API ====================
	
	// 창고 목록 조회
	@GetMapping("/api/warehouses")
	@ResponseBody
	public List<WarehouseDTO> getWarehouseList(
	        @RequestParam(name = "warehouseType", required = false) String warehouseType,
	        @RequestParam(name = "warehouseStatus", required = false) String warehouseStatus,
	        @RequestParam(name = "searchKeyword", required = false) String searchKeyword) {
	    
	    log.info("창고 목록 API 호출");
	    return wareService.getWarehouseList(warehouseType, warehouseStatus, searchKeyword);
	}
	
	// 창고 신규 등록
	@PostMapping("/api/warehouses")
	@ResponseBody
	public Map<String, Object> addWarehouse(@RequestBody WarehouseDTO dto, Principal principal) {
	    Map<String, Object> result = new HashMap<>();
	    
	    try {
	        if(dto.getWarehouseStatus() == null) {
	            dto.setWarehouseStatus("Y");
	        }
	        wareService.addWarehouse(dto);
	        result.put("success", true);
	        result.put("message", "창고가 등록되었습니다.");
	    } catch (Exception e) {
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    
	    return result;
	}
	
	// 창고 정보 수정
	@PutMapping("/api/warehouses/{warehouseId}")
	@ResponseBody
	public Map<String, Object> updateWarehouse(
	        @PathVariable("warehouseId") String warehouseId,
	        @RequestBody WarehouseDTO dto,
	        Principal principal) {
	    
	    Map<String, Object> result = new HashMap<>();
	    dto.setWarehouseId(warehouseId);
	    
	    boolean success = wareService.updateWarehouse(dto);
	    result.put("success", success);
	    result.put("message", success ? "수정 완료" : "수정 실패");
	    
	    return result;
	}
	
	// 창고 삭제 (다중 선택 가능)
	@DeleteMapping("/api/warehouses")
	@ResponseBody
	public Map<String, Object> deleteWarehouses(@RequestBody List<String> ids) {
	    Map<String, Object> result = wareService.deleteWarehouses(ids);
	    
	    if(result.containsKey("failed")) {
	        List<String> failed = (List<String>) result.get("failed");
	        result.put("message", 
	            failed.size() + "개 창고는 재고가 있어 삭제할 수 없습니다.\n" +
	            "창고ID: " + String.join(", ", failed));
	    } else {
	        result.put("message", "삭제 완료");
	    }
	    return result;
	}
	
	// ==================== 3. 입고 관리 API ====================
	
	// 입고 목록 조회
	@GetMapping("/api/inputs")
	@ResponseBody
	public List<Map<String, Object>> getInputList(
	        @RequestParam(name = "itemType", required = false) String itemType,  // itemType 파라미터 추가
	        @RequestParam(name = "batchId", required = false) String batchId,
	        @RequestParam(name = "inType", required = false) String inType,
	        @RequestParam(name = "inStatus", required = false) String inStatus) {
	    
	    // itemType으로 분기 처리 필요할 수도
	    if(batchId != null && !batchId.isEmpty()) {
	        return wareService.getInputListByBatch(batchId);
	    }
	    
	    return wareService.getInputList(inType, inStatus);
	}
	
	// 날짜별 그룹화된 입고 목록 조회
	@GetMapping("/api/inputs/grouped")
	@ResponseBody
	public List<Map<String, Object>> getGroupedInputList(
	        @RequestParam(name = "date", required = false) String date,
	        @RequestParam(name = "inType", required = false) String inType) {
	    
	    return wareService.getGroupedInputList(date, inType);
	}

	// 개별 입고 등록
	@PostMapping("/api/inputs")
	@ResponseBody
	public Map<String, Object> addInput(@RequestBody Map<String, Object> params, Principal principal) {
	    Map<String, Object> result = new HashMap<>();
	    try {
	        params.put("empId", principal.getName());
	        String inId = wareService.addInput(params);
	        result.put("success", true);
	        result.put("inId", inId);
	        result.put("message", "입고 등록 완료");
	    } catch(Exception e) {
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    return result;
	}
	
	// 배치 단위 입고 등록
	@PostMapping("/api/inputs/batch")
	@ResponseBody
	public Map<String, Object> addInputBatch(@RequestBody List<Map<String, Object>> items, Principal principal) {
	    Map<String, Object> result = new HashMap<>();
	    try {
	        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
	        Integer batchCount = wareService.getTodayBatchCount(today);
	        String batchId = "B" + today + String.format("%03d", batchCount + 1);
	        
	        for(Map<String, Object> item : items) {
	            item.put("empId", principal.getName());
	            item.put("batchId", batchId);
	            
	            String inId = wareService.addInput(item);
	        }
	        
	        result.put("success", true);
	        result.put("batchId", batchId);
	        result.put("message", items.size() + "건 입고 등록 완료");
	    } catch(Exception e) {
	        log.error("입고 배치 등록 오류:", e);
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    return result;
	}

	// 입고 검사 완료 처리
	@PutMapping("/api/inputs/{inId}/complete")
	@ResponseBody
	public Map<String, Object> completeInput(@PathVariable("inId") String inId, Principal principal) {
	    Map<String, Object> result = new HashMap<>();
	    try {
	        wareService.completeInput(inId, principal.getName());
	        result.put("success", true);
	        result.put("message", "입고 완료 처리");
	    } catch(Exception e) {
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    return result;
	}
	
	// 입고 반려
	@PutMapping("/api/inputs/{inId}/reject")
	@ResponseBody
	public Map<String, Object> rejectInput(
	        @PathVariable("inId") String inId,
	        @RequestParam("reason") String reason,
	        Principal principal) {
	    
	    Map<String, Object> result = new HashMap<>();
	    try {
	        wareService.rejectInput(inId, reason, principal.getName());
	        result.put("success", true);
	        result.put("message", "입고가 반려되었습니다.");
	    } catch(Exception e) {
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    return result;
	}
	
	// 반려 사유 코드 조회
	@GetMapping("/api/reject-reasons")
	@ResponseBody
	public List<Map<String, Object>> getRejectReasons() {
	    return wareService.getRejectReasons();
	}
	// ==================== 4. 출고 관리 API ====================

	// 출고 목록 조회
	@GetMapping("/api/outputs")
	@ResponseBody
	public List<Map<String, Object>> getOutputList(
	        @RequestParam(name = "outType", required = false) String outType,
	        @RequestParam(name = "outStatus", required = false) String outStatus,
	        @RequestParam(name = "startDate", required = false) String startDate,
	        @RequestParam(name = "endDate", required = false) String endDate) {
	    
	    return wareService.getOutputList(outType, outStatus, startDate, endDate);
	}

	// 배치(batchId) 출고 등록
	@PostMapping("/api/outputs/batch")
	@ResponseBody
	@TrackLot(tableName = "output", pkColumnName = "batch_id")
	public Map<String, Object> addOutputBatch(@RequestBody List<Map<String, Object>> items, Principal principal) {
	    log.info("출고 배치 등록 요청: {}", items);
	    
	    Map<String, Object> result = new HashMap<>();
	    try {
	        // 첫 번째 아이템에서 사유 추출
	        String outReason = items.get(0).get("outReason") != null ? 
	                          (String) items.get(0).get("outReason") : "정상출고";
	                          
	        String batchId = wareService.addOutputBatch(items, principal.getName());
	        
//		    out_id or batch_id 등을 넣는 위치
		    HttpSession session = SessionUtil.getSession();
	        session.setAttribute("targetIdValue", batchId); //pk_id의 값 입력
	        
	        result.put("success", true);
	        result.put("batchId", batchId);
	        result.put("message", items.size() + "건 출고 등록 완료");
	    } catch(Exception e) {
	        log.error("출고 배치 등록 실패: ", e);
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    

        
	    return result;
	}

	// 출고 완료 처리
	@PutMapping("/api/outputs/{outId}/complete")
	@ResponseBody
	public Map<String, Object> completeOutput(@PathVariable("outId") String outId, Principal principal) {
	    Map<String, Object> result = new HashMap<>();
	    try {
	        wareService.completeOutput(outId, principal.getName());
	        result.put("success", true);
	        result.put("message", "출고 완료");
	    } catch(Exception e) {
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    return result;
	}

	// 출고 취소
	@DeleteMapping("/api/outputs/{outId}")
	@ResponseBody
	public Map<String, Object> cancelOutput(@PathVariable("outId") String outId) {
	    Map<String, Object> result = new HashMap<>();
	    try {
	        wareService.cancelOutput(outId);
	        result.put("success", true);
	    } catch(Exception e) {
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    return result;
	}
	
	// 배치별 출고 목록 조회 추가
	@GetMapping("/api/outputs/batch/{batchId}")
	@ResponseBody
	public List<Map<String, Object>> getOutputListByBatch(@PathVariable("batchId") String batchId) {
	    return wareService.getOutputListByBatch(batchId);
	}

	// 그룹화된 출고 목록 조회
	@GetMapping("/api/outputs/grouped")
	@ResponseBody
	public List<Map<String, Object>> getGroupedOutputList(
	    @RequestParam(name = "date", required = false) String date,
	    @RequestParam(name = "outType", required = false) String outType) {
	    return wareService.getOutputBatches(date, outType);
	}
	
	// 0923 출고내역 삭제
	@DeleteMapping("/api/outputs")
	@ResponseBody
	public Map<String, Object> deleteOutputs(@RequestBody List<String> outIds) {
	    Map<String, Object> result = new HashMap<>();
	    try {
	        int deleteCount = wareService.deleteOutputs(outIds);
	        result.put("success", true);
	        result.put("message", deleteCount + "건 삭제 완료");
	    } catch(Exception e) {
	        result.put("success", false);
	        result.put("message", e.getMessage());
	    }
	    return result;
	}

	// ==================== 5. 데이터 조회 API ====================

	// 입고 가능한 자재 목록 조회
	@GetMapping("/api/materials-for-input")
	@ResponseBody
	public List<Map<String, Object>> getMaterialsForInput() {
	    return wareService.getMaterialsForInput();
	}

	// 거래처 목록 조회
	@GetMapping("/api/clients")
	@ResponseBody
	public List<Map<String, Object>> getClientsList() {
	    return wareService.getClientsList();
	}

	// 창고 타입 공통코드 조회
	@GetMapping("/api/common-codes/warehouse-types")
	@ResponseBody
	public List<Map<String, String>> getWarehouseTypes() {
	    return stockService.getMaterialTypes();
	}

	@GetMapping("/api/products-for-input")
	@ResponseBody
	public List<Map<String, Object>> getProductsForInput() {
	    return wareService.getProductsForInput();
	}

	// 출고용 자재 목록 조회 (warehouse_item 재고 합산)
	@GetMapping("/api/materials-with-stock")
	@ResponseBody
	public List<Map<String, Object>> getMaterialsWithStock() {
	    return wareService.getMaterialsWithStock();
	}

	// 출고용 완제품 목록 조회 (warehouse_item 재고 합산)
	@GetMapping("/api/products-with-stock")
	@ResponseBody
	public List<Map<String, Object>> getProductsWithStock() {
	    return wareService.getProductsWithStock();
	}
}