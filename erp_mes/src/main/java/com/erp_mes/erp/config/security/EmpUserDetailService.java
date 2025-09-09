package com.erp_mes.erp.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.erp_mes.erp.personnel.dto.PersonnelLoginDTO;
import com.erp_mes.erp.personnel.entity.Personnel;
import com.erp_mes.erp.personnel.repository.PersonnelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmpUserDetailService implements UserDetailsService {
	private final PersonnelRepository personnelRepository;

	@Override
	public UserDetails loadUserByUsername(String empId) throws UsernameNotFoundException {
		log.info("EmpUserDetailService() : " + empId);

		Personnel personnel = personnelRepository.findById(empId)
				.orElseThrow(() -> new UsernameNotFoundException(empId + " : 사용자 조회 실패"));

//		PersonnelLoginDTO personnelLoginDTO = ModelMapperUtils.convertObjectByMap(personnel, PersonnelLoginDTO.class);
//		personnelLoginDTO.setEmpId(personnel.getEmpId());
//		personnelLoginDTO.setName(personnel.getName());
//		personnelLoginDTO.setPasswd(personnel.getPasswd());
//		personnelLoginDTO.setEmpDeptId(personnel.getDepartment().getComDtId());
//		personnelLoginDTO.setEmpLevelId(personnel.getLevel().getComDtId());
//		log.info("로그인객체 : " + personnelLoginDTO.toString());
		return  new PersonnelLoginDTO(personnel);
	}

}
