package com.erp_mes.mes.lot.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.erp.config.util.SessionUtil;
import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.lot.service.LotService;
import com.erp_mes.mes.lot.trace.TrackLot;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/lot")
@Log4j2
@RequiredArgsConstructor
public class LotController {

	private final LotService lotService;

	@GetMapping("/test/{wordOrderId}")
	@ResponseBody
	@TrackLot(tableName = "work_result", pkColumnName = "work_order_id")
	public String testLotTrack(@PathVariable("wordOrderId") String workOrderId) {
		
		HttpSession session = SessionUtil.getSession();
        session.setAttribute("targetIdValue", workOrderId); //pk_id의 값 입력
		return "ok";
	}
	
	//로트 추적 리스트
	@GetMapping("")
	public String showLotTrackingList(@RequestParam(value = "page", defaultValue = "0") int page, 
			@RequestParam(value = "size", defaultValue = "20") int size, Model model) {
		
		List<LotDTO> lotDTOList = lotService.getLotTrackingList(page, size);
		model.addAttribute("lotDTOList", lotDTOList);
		
		return "/lot/lot_list";
	}
}