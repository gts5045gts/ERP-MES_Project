package com.bootstrap.study.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bootstrap.study.personnel.dto.PersonnelDTO;
import com.bootstrap.study.personnel.dto.PersonnelLoginDTO;
import com.bootstrap.study.personnel.entity.Personnel;
import com.bootstrap.study.personnel.repository.PersonnelRepository;
import com.bootstrap.study.util.ModelMapperUtils;

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
		
		PersonnelLoginDTO personnelLoginDTO = ModelMapperUtils.convertObjectByMap(personnel, PersonnelLoginDTO.class);
		
		return personnelLoginDTO;
	}

}
