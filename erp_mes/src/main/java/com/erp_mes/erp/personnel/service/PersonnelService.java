
package com.erp_mes.erp.personnel.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.erp_mes.erp.approval.constant.ApprDecision;
import com.erp_mes.erp.approval.constant.ApprReqType;
import com.erp_mes.erp.approval.constant.ApprStatus;
import com.erp_mes.erp.approval.dto.ApprDTO;
import com.erp_mes.erp.approval.entity.Appr;
import com.erp_mes.erp.approval.entity.ApprLine;
import com.erp_mes.erp.approval.repository.ApprLineRepository;
import com.erp_mes.erp.approval.repository.ApprRepository;
import com.erp_mes.erp.approval.service.ApprService;
import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.erp.commonCode.entity.CommonDetailCode;
import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.erp.personnel.dto.PersonnelDTO;
import com.erp_mes.erp.personnel.dto.PersonnelTransferDTO;
import com.erp_mes.erp.personnel.entity.Personnel;
import com.erp_mes.erp.personnel.entity.PersonnelTransfer;
import com.erp_mes.erp.personnel.repository.PersonnelRepository;
import com.erp_mes.erp.personnel.repository.PersonnelTransferRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
@Log4j2
public class PersonnelService {
	
	private final PersonnelRepository personnelRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommonDetailCodeRepository commonDetailCodeRepository;
    private final PersonnelImgService personnelImgService;
    @Lazy
    private final ApprService apprService;
    
	// 부서 리스트 조회
    public List<CommonDetailCodeDTO> getAllDepartments() {
        // 데이터베이스 쿼리로 직접 "DEP" 코드를 필터링합니다.
        List<CommonDetailCode> comList = commonDetailCodeRepository.findByComId_ComId("DEP");
        
        log.info("부서 목록 조회 결과: " + comList.toString());

        List<CommonDetailCodeDTO> departments = comList.stream()
                .map(CommonDetailCodeDTO::fromEntity)
                .collect(Collectors.toList());
         
        log.info("DTO 변환 후 데이터: " + departments.toString());
        return departments;
    }
    
    // 직책 리스트 조회
    public List<CommonDetailCodeDTO> getAllPositions() {
    	List<CommonDetailCode> comList = commonDetailCodeRepository.findAll();
    	
    	List<CommonDetailCodeDTO> position = comList.stream()
    			.filter(result -> "POS".equals(result.getComId().getComId()))
    			.map(CommonDetailCodeDTO :: fromEntity)
    			.collect(Collectors.toList());
    	
    	return position;
    }

    // 재직 상황 리스트 
    public List<CommonDetailCodeDTO> getStatus() {
    	
    	List<CommonDetailCode> comList = commonDetailCodeRepository.findAll();
    	
    	List<CommonDetailCodeDTO> position = comList.stream()
    			.filter(result -> "STA".equals(result.getComId().getComId()))
    			.filter(result2 -> "STA007".equals(result2.getComDtId()))
    			.map(CommonDetailCodeDTO :: fromEntity)
    			.collect(Collectors.toList());
    	
    	return position;
    	
    	
    }
    public List<CommonDetailCodeDTO> getAllStatus() {
    	
    	List<CommonDetailCode> comList = commonDetailCodeRepository.findAll();
    	
    	List<CommonDetailCodeDTO> position = comList.stream()
    			.filter(result -> "STA".equals(result.getComId().getComId()))
    			.map(CommonDetailCodeDTO :: fromEntity)
    			.collect(Collectors.toList());
    	
    	return position;
    	
    	
    }
    public List<CommonDetailCodeDTO> getAllLevel() {
    	
    	List<CommonDetailCode> levList = commonDetailCodeRepository.findAll();
    	
    	List<CommonDetailCodeDTO> level = levList.stream()
    			.filter(result -> "AUT".equals(result.getComId().getComId()))
    			.map(CommonDetailCodeDTO :: fromEntity)
    			.collect(Collectors.toList());
    	
    	return level;
    }
    
    // 특정 부서의 직원 목록을 조회하여 DTO 리스트로 반환
    public List<PersonnelDTO> getEmployeesByDepartmentId(String comDtId) {
        List<Personnel> personnels = personnelRepository.findByDepartment_ComDtId(comDtId);
        return personnels.stream()
        		.filter(res -> !"STA009".equalsIgnoreCase(res.getStatus().getComDtId()))	//퇴직자는 리스트에서 안보이게 하기 위함
                .map(this::convertToDto) // convertToDto 메소드를 사용해 DTO로 변환 - Entity -> DTO 변환 전용 메서드
                .collect(Collectors.toList());
    }
    
    // Employee 엔티티를 PersonnelDTO로 변환하는 private 메소드
    private PersonnelDTO convertToDto(Personnel personnel) {
    	PersonnelDTO dto = new PersonnelDTO();
        dto.setEmpId(personnel.getEmpId()); 
        dto.setDeptName(personnel.getDepartment().getComDtNm());
        dto.setPosName(personnel.getPosition().getComDtNm());
        dto.setName(personnel.getName());
        dto.setPhone(personnel.getPhone());
        dto.setEmail(personnel.getEmail());
        return dto;
    }
    
    // 인사현황 페이지에 필요한 전체 직원 목록을 조회하는 메서드
 	public List<PersonnelDTO> getAllPersonnels() {
 		
 		// Personnel 엔티티 목록을 가져와서 DTO로 변환
 		List<Personnel> personnelList = personnelRepository.findAll();
 		return personnelList.stream()
 				.filter(res -> !"STA009".equalsIgnoreCase(res.getStatus().getComDtId()) )	//퇴직자는 리스트에서 안보이게 하기 위함
 				.map(PersonnelDTO::fromEntity)
 				.collect(Collectors.toList());
 	}
 	
 	
 	
 	public void personRegist(PersonnelDTO personnelDTO, MultipartFile empImg) throws IOException {
 		  
 		
	    
 	      //현재 날짜
	      LocalDate today = LocalDate.now();
	      
	        // yyyyMMdd 포맷 지정
	      DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");      
	      String todayStr = today.format(formatter1);                           //joinDate 넣어줄 타입 변환 Date값   
	   
	      DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");      //ex) 20250821 형태로 저장
	      String empDate = today.format(formatter2);                           //현재 날짜 String타입으로 저장
	
	      //사원번호 생성
	      List<Personnel> personnelList = personnelRepository.findAll();
	      Long count = (long) (personnelList.size() + 1);                        //전체 사원수 +1 ex)2+1 
	      String employeeId = empDate + String.format("%02d", count);            //count 표시 형식 ex) 03
	                                                               //현재날짜 String 타입으로 저장한 변수 + 03 ==> ex) 2025082103
	      
	      personnelDTO.setEmpId(employeeId);            //부서 아이디 부서타입의 변수에 저장
	      personnelDTO.setJoinDate(todayStr);
	      String encodedPassword = passwordEncoder.encode(personnelDTO.getPasswd());
	   	  personnelDTO.setPasswd(encodedPassword);
	      
	      log.info("DTO 가져온 사원등록 데이터: " + personnelDTO.toString());
	      
	      Personnel personnel = new Personnel();
	      personnel = personnel.fromDTO(personnelDTO, commonDetailCodeRepository);
	      log.info("Entity 변환 후 사원등록 데이터: " + personnel.toString());
	
	      personnelRepository.save(personnel);
	      
 	      
 	      if(empImg != null && !empImg.isEmpty()) {
 	    	  //personnelImg 로 personnelDTO , 와 empImg  값 넘겨 주기 위함 
 	    	  personnelImgService.registImg(personnel, empImg);
 	      }

	}
 	
 	// 특정 사원 상세 정보 조회
 	public Optional<PersonnelDTO> getPersonnelDetails(String empId) {
 	    Optional<Personnel> personnelOpt = personnelRepository.findById(empId);
 	    return personnelOpt.map(personnel -> {
 	        PersonnelDTO dto = PersonnelDTO.fromEntity(personnel);
 	        return dto;
 	    });
 	}

 	// 사원 정보 수정
 	public void updatePersonnel(PersonnelDTO personnelDTO, MultipartFile empImg) throws IOException {
 	    Personnel personnel = personnelRepository.findById(personnelDTO.getEmpId())
 	            .orElseThrow(() -> new IllegalArgumentException("잘못된 사원 ID입니다: " + personnelDTO.getEmpId()));

 	    // 수정시 오류 방지 
 	    //받아온 PASSWD 값이 NULL 값이 아니면 수정되겠금 변경
 	    if(personnelDTO.getPasswd() != null) {
 	    	String encodedPassword = passwordEncoder.encode(personnelDTO.getPasswd());			
 	  	    personnelDTO.setPasswd(encodedPassword);
 	    }
 	    
 	    //수정시 해싱 암호로 들어가도록 변경
 	  
 	    personnel.fromDTOUpdate(personnelDTO, commonDetailCodeRepository);

 	    personnelRepository.save(personnel);

 	    //이미지 파일과 사원 아이디를 이미지 서비스로 보내기
 	    if(empImg != null && !empImg.isEmpty()) {
 	    	log.info("저장될 이미지가 발견 되었습니다.");
 	    	personnelImgService.registImg(personnel, empImg);
 	    }

 	}

    // 인사팀의 중간관리자, 상위관리자 목록만 조회
    public List<PersonnelDTO> getEmployeesByDeptIdLevel(String deptId, String... levId) {
    	List<Personnel> personnels = personnelRepository.findAll();
    	
    	return personnels.stream()
        		.filter(result -> "DEP001".equals(result.getDepartment().getComDtId())
        	            && Arrays.stream(levId).anyMatch(id -> id.equals(result.getLevel().getComDtId()))) 
    			.map(PersonnelDTO::fromEntity)
    			.collect(Collectors.toList());
    }
    
    @Transactional
    public void submitTransPersonnel(Map<String, Object> transInfo, String loginEmpId) throws IOException {
        //String empId = (String) transInfo.get("empId");
        String empName = (String) transInfo.get("name");
        
        ObjectMapper objectMapper = new ObjectMapper();
        String contentJson;
        try {
            contentJson = objectMapper.writeValueAsString(transInfo);
        } catch (JsonProcessingException e) {
            // IOException으로 래핑하여 상위 호출자에게 전달
            throw new IOException("인사발령 데이터를 JSON으로 변환하는 중 오류가 발생했습니다.", e);
        }
        
        // 1. ApprService를 호출하여 전자결재 문서 저장
        String title = "인사발령 신청 (대상: " + empName + ")";

        ApprDTO apprDTO = new ApprDTO();
        apprDTO.setReqType(ApprReqType.TRANSFER.getCode());
        apprDTO.setTitle(title);
        apprDTO.setContent(contentJson);
        apprDTO.setRequestAt(LocalDate.now());
        
        // 결재자 ID를 String 배열로 변환
        String approverId = (String) transInfo.get("approverId");
        String[] empIds = {loginEmpId, approverId}; // 09/01 17:25 현재 신청자(tot_step=1), 결재자(tot_step=2)로 설정

        Long apprId = apprService.registAppr(apprDTO, empIds, loginEmpId);
        
    }
    
}