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

import com.erp_mes.mes.lot.dto.LotDTO;
import com.erp_mes.mes.lot.dto.MaterialUsageDTO;
import com.erp_mes.mes.lot.service.LotService;
import com.erp_mes.mes.lot.trace.TrackLot;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/lot")
@Log4j2
@RequiredArgsConstructor
public class LotController {

	private final LotService lotService;

	@GetMapping("/testAOP")
	@ResponseBody
	public String testCallAOP() {
		// lotDTO를 먼저 호출하면서 필수정보 입력
		LotDTO lotDTO = LotDTO.builder().tableName("WAREHOUSE_ITEM2").type("process").materialCode("test-STEEL-444")
				.build();

		String targetId = lotService.registWareHouse(lotDTO);
		// 입고등을 insert한후에 pk id값을 받아서 setTargetId하면
		// lotId가 생성됨.
		lotDTO.setTargetId(targetId);
		// 생성된 lotId를 자신의 table에 저장해도 되고
		// lot_master테이블에 table_name, target_id(고유값)을 join해서
		// lot_material_usage 테이블 저장할때 다시 사용해도 됨.
		log.info(">>>>>>>>>>>>>>>>>>>>>새로 생성된 lotId === " + lotDTO.getLotId());

		return "ok";
	}

	@GetMapping("/saveLot")
	@ResponseBody
//	@TrackLot
	public LotDTO saveWareHouse() {
		LotDTO lotDTO = LotDTO.builder().tableName("WAREHOUSE_ITEM3").materialCode("test-STEEL-11")
				.build();

		String targetId = lotService.registWareHouse(lotDTO);
		// table 고유 pk값 저장
		lotDTO.setTargetId(targetId);

		// 새로 발급된 lotId 를 해당 db table에 저장
		log.info(lotDTO.getLotId());
		// return 변경 가능
		return lotDTO;
	}

	@GetMapping("/saveUsage")
	@ResponseBody
//	@TrackLot
	public LotDTO saveUsage() {
		List<MaterialUsageDTO> usages = new ArrayList<MaterialUsageDTO>();
		MaterialUsageDTO usage1 = MaterialUsageDTO.builder()
								.parentLotId("PR20250912-001") // 원자재 LOT ID
//								.qtyUsed(100) // 사용 수량
								.build();
		usages.add(usage1);
//		MaterialUsageDTO usage2 = MaterialUsageDTO.builder()
//								.parentLotId("PR20250912-005")
//								.qtyUsed(50)
//								.build();
//		usages.add(usage2);
		// LotDTO 생성
		LotDTO lotDTO = LotDTO.builder().tableName("PRODUCT_PLAN")
//				.qty(150) // 총 생산 수량
				.machineId("L1") // 생산 설비 ID
				.usages(usages) // 자재 사용 내역 등록
				.build();

		String targetId = lotService.registWareHouse(lotDTO);
		// table 고유 pk값 저장
		lotDTO.setTargetId(targetId);

		// 새로 발급된 lotId 를 해당 db table에 저장
		log.info(lotDTO.getLotId());
		// return 변경 가능
		return lotDTO;
	}
	
	
	@GetMapping("")
	public String showLotTrackingList() {
		return "/lot/lot_list";
	}
}
