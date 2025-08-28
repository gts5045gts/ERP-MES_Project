
package com.bootstrap.study.personnel.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bootstrap.study.commonCode.dto.CommonDetailCodeDTO;
import com.bootstrap.study.commonCode.entity.CommonDetailCode;
import com.bootstrap.study.commonCode.repository.CommonDetailCodeRepository;
import com.bootstrap.study.personnel.dto.PersonnelDTO;
import com.bootstrap.study.personnel.entity.Personnel;
import com.bootstrap.study.personnel.repository.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
@Log4j2
public class PersonnelService {
	
	private final PersonnelRepository personnelRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommonDetailCodeRepository commonDetailCodeRepository;
    
    // 부서 리스트 조회
    public List<CommonDetailCodeDTO> getAllDepartments() {
    	
        List<CommonDetailCode> comList = commonDetailCodeRepository.findAll();
        log.info("depList" + comList.toString());

        List<CommonDetailCodeDTO> departments = comList.stream()
        		.filter(result -> "DEP".equals(result.getComId().getComId()))
        		.map(CommonDetailCodeDTO :: fromEntity)
        		.collect(Collectors.toList());
         
        log.info("필터 후 데이터 : " + departments.toString());
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
                .map(this::convertToDto) // convertToDto 메소드를 사용해 DTO로 변환 - Entity -> DTO 변환 전용 메서드
                .collect(Collectors.toList());
    }
    
    // Employee 엔티티를 PersonnelDTO로 변환하는 private 메소드
    private PersonnelDTO convertToDto(Personnel personnel) {
    	PersonnelDTO dto = new PersonnelDTO();
        dto.setEmpId(personnel.getEmpId()); 
        dto.setDeptName(personnel.getDepartment().getComDtId());
        dto.setPosName(personnel.getPosition().getComDtId());
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
 				.map(PersonnelDTO::fromEntity)
 				.collect(Collectors.toList());
 	}
 	
 	
 	
 	public void personRegist(PersonnelDTO personnelDTO) {
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
 	      
 	      log.info("사원등록 정보: " + personnelDTO.toString());
 	      
 	      Personnel personnel = new Personnel();
 	      personnel = personnel.fromDTO(personnelDTO, commonDetailCodeRepository);
 	      log.info("사원등록 정보: " + personnel.toString());

 	      personnelRepository.save(personnel);

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
 	public void updatePersonnel(PersonnelDTO personnelDTO) {
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
 	}


 	

}

