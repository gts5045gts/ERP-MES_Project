package com.erp_mes.mes.lot.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.erp.config.util.SessionUtil;
import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.lot.dto.MaterialUsageDTO;
import com.erp_mes.mes.lot.service.LotService;
import com.erp_mes.mes.lot.trace.TrackLot;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/lot")
@Log4j2
@RequiredArgsConstructor
public class LotController {

	private final LotService lotService;

	@GetMapping("/test")
	@ResponseBody
	@TrackLot(tableName = "work_order", pkColumnName = "work_order_id")
	public String testLotTrack() {
		
		HttpSession session = SessionUtil.getSession();
        session.setAttribute("targetIdValue", "3"); //pk_id의 값 입력
		return "ok";
	}
	
	@GetMapping("")
	public String showLotTrackingList() {
		
		return "/lot/lot_list";
	}
}