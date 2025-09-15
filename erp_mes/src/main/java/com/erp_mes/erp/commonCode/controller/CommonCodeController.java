package com.erp_mes.erp.commonCode.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ResponseBody;

import com.erp_mes.erp.commonCode.dto.CommonCodeDTO;
import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.erp.commonCode.entity.CommonCode;
import com.erp_mes.erp.commonCode.repository.CommonCodeRepository;
import com.erp_mes.erp.commonCode.service.CommonCodeService;

import org.springframework.web.bind.annotation.PostMapping;




@Controller
@RequestMapping("/admin")
@Log4j2
@RequiredArgsConstructor
public class CommonCodeController {
	
	private final CommonCodeService comService;

//===========================================================================
	
	

	// 화면이동
	@GetMapping("/commonCode")
	public String commonCode(Model model) {
		
		model.addAttribute("commonCodeDTO", new CommonCodeDTO()); 
		model.addAttribute("commonDetailCodeDTO", new CommonDetailCodeDTO());
		model.addAttribute("codes", comService.findAllCodes());
		
		return "admin/commonCode";
	}
	
	// 상세공통코드 Ajax
	@GetMapping("/detail/{comId}")
	public String coommDetail(@PathVariable("comId") String comId, Model model) {
		model.addAttribute("dtCodes", comService.findByComId(comId));
		return "admin/commonCodeDetail :: detailTable";
	}

	
	//================================== 등록 =========================================	
	
	// 공통코드 등록
	@PostMapping("/comRegist")
	public String comRegist(@ModelAttribute("commonCodeDTO") @Valid CommonCodeDTO codeDTO, BindingResult bindingResult, Model model) {
		
		if(bindingResult.hasErrors()) {
		    model.addAttribute("codes", comService.findAllCodes()); // 테이블 데이터
		    return "admin/commonCode"; // redirect 없이 바로 화면 렌더링
		}

	    comService.registCode(codeDTO);
	    return "redirect:/admin/commonCode"; // GET 컨트롤러에서 codes를 가져오기 때문에 테이블 정상 표시
	
	}
	
	// 상세공통코드 등록 
	@PostMapping("/comDtRegist")
	public String comDtRegist(@ModelAttribute("commonDetailCodeDTO") @Valid CommonDetailCodeDTO codeDtDTO, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
	        // 에러 시 부모코드 기준으로 상세코드 fragment 다시 렌더링
	        model.addAttribute("dtCodes", comService.findByComId(codeDtDTO.getComId()));
	        return "admin/commonCodeDetail :: detailTable"; // fragment 반환
	    }
	    comService.registDtCode(codeDtDTO);
	    // 등록 후 다시 fragment 반환
	    model.addAttribute("dtCodes", comService.findByComId(codeDtDTO.getComId()));
	    return "admin/commonCodeDetail :: detailTable";
	}

//================================== 삭제 =========================================	
	
	
	// 공통코드 삭제
	@DeleteMapping("comDelete/{comId}")
	@ResponseBody
	public String comDelete(@PathVariable("comId") String comId) {
	    
	    return comService.comDelete(comId); // 메시지를 그대로 반환
	}
	
	// 상세공통 코드 삭제 
	@DeleteMapping("comDtDelete/{comDtId}")
	@ResponseBody
	public String comDtDelete(@PathVariable("comDtId") String comDtId) {
		return comService.deleteDtCode(comDtId); // 서비스에서 성공/실패 메시지 반환
	}
	
	
	
//================================== 수정 =========================================	 
	
	// 공통코드 수정
	@PostMapping("/comUpdate/{comId}")
	@ResponseBody
	public String comUpdate (@PathVariable("comId") String comId, @RequestParam("comNm") String comNm, @RequestParam("useYn") String useYn) {
		 comService.updateCode(comId, comNm, useYn);
		 
		 return "해당 코드가 수정되었습니다";
	}
	
	
	// 상세코드 수정
	@PostMapping("/comDtUpdate/{comDtId}")
	@ResponseBody
	public String comDtUpdate(@PathVariable("comDtId") String comDtId, @RequestParam("comDtNm") String comDtNm,
	        @RequestParam("comDtOrder") Integer comDtOrder, @RequestParam("useYn") String useYn) {

		
	    comService.updateDtCode(comDtId, comDtOrder, comDtNm, useYn);
	    return "해당 코드가 수정되었습니다";
	}
	

//================================== 검색 =========================================	 
	
	// 공통코드 검색
	@GetMapping("/comSearch")
	@ResponseBody
	public List<CommonCodeDTO> comSearch(@RequestParam("keyword") String keyword) {
		
		return comService.searchCode(keyword);
	}
	
	// 상세코드 검색
	@GetMapping("/comDtSearch")
	@ResponseBody
	public List<CommonDetailCodeDTO> comDtSearch(@RequestParam("parentId") String parentId, 
			@RequestParam(value = "keyword", required = false) String keyword) {
		
		 return comService.searchDtCode(parentId, keyword);
		
	}
	
	
	
	
}