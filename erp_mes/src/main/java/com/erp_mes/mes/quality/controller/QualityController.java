package com.erp_mes.mes.quality.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erp_mes.erp.personnel.dto.PersonnelLoginDTO;

import lombok.extern.log4j.Log4j2;


@Controller
@RequestMapping("/quality")
@Log4j2
public class QualityController {
	@GetMapping("")
	public String qcinfo(@AuthenticationPrincipal PersonnelLoginDTO personnelLoginDTO) {
		log.info("QualityController qcinfo()" + personnelLoginDTO);
		return "qc/qcinfo";
	}
	
}
