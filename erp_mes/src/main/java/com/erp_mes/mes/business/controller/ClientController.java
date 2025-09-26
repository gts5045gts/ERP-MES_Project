package com.erp_mes.mes.business.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.erp_mes.erp.personnel.dto.PersonnelLoginDTO;
import com.erp_mes.mes.business.dto.ClientDTO;
import com.erp_mes.mes.business.service.ClientService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Controller
@RequestMapping("/business")
@RequiredArgsConstructor
@Log4j2
public class ClientController {
	
	private final ClientService clientService;
	private final RestTemplate restTemplate = new RestTemplate();

    @Value("${data.go.kr.api.key}") // yml에 저장
    private String serviceKey;
	
	// 화면 이동과 데이터 조회로 분리한 이유는 테스트와 유지보수를 편하게 하기 위해 + 책임 분리
	// 거래처 화면
	@GetMapping("client")
	public String client(Model model, @AuthenticationPrincipal PersonnelLoginDTO userDetails) {

		String userLevelId = userDetails.getEmpLevelId();
        
        boolean isAutLevel = "AUT001".equals(userLevelId);
        model.addAttribute("isAUTLevel", isAutLevel);
		
		return "/business/client";
	}
	
	// 거래처 전체 목록 조회
    @GetMapping("/api/clients")
    @ResponseBody
    public List<ClientDTO> getAllClients() {
        log.info("거래처 전체 목록 조회 요청");
        
        return clientService.getAllClients();
    }
	
    // 거래처 등록
	@PostMapping("/api/clients/submit")
    public ResponseEntity<?> createClient(@RequestBody ClientDTO clientDto) {
		log.info("컨트롤러 수행: {}", clientDto); 
        try {
            clientService.saveClient(clientDto);
            log.info("컨트롤러 수행했고 성공");
            return ResponseEntity.ok(Map.of("status", "success", "message", "Client created successfully"));
        } catch (Exception e) {
			log.error("컨트롤러 수행 실패: {}", e.getMessage()); 
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
	
	// 거래처 수정
	@PutMapping("/api/clients/update/{clientId}")
	public ResponseEntity<?> updateClient(@RequestBody ClientDTO clientDto) {
	    try {
	        clientService.updateClient(clientDto);
	        return ResponseEntity.ok(Map.of("status", "success", "message", "Client updated successfully"));
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
	    }
	}
	
	// 매출사 거래처 목록
	@GetMapping("/api/clients/order-type")
	@ResponseBody
	public List<ClientDTO> getOrderClients() {
	    return clientService.getOrderClients("ORDER", "ACTIVE");
	}
	
	@PostMapping("/api/validateBizNo")
    public ResponseEntity<?> validateBusinessNumber(@RequestBody Map<String, String> request) {
        String bizNo = request.get("businessNumber");

        // 공공데이터 API URL
        String url = "https://api.odcloud.kr/api/nts-businessman/v1/status"
                + "?serviceKey=" + serviceKey;

        // API 요청 body
        Map<String, Object> body = new HashMap<>();
        body.put("b_no", Collections.singletonList(bizNo)); // 사업자번호 리스트

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("API 호출 실패: " + e.getMessage());
        }
    }
	
}
