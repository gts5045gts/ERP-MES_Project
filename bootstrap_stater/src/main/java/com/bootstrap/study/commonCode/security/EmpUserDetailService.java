package com.bootstrap.study.commonCode.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bootstrap.study.personnel.dto.PersonnelLoginDTO;
import com.bootstrap.study.personnel.entity.Personnel;
import com.bootstrap.study.personnel.repository.PersonnelRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class EmpUserDetailService implements UserDetailsService {
	
	private final PersonnelRepository personnelRepository;
	
	public EmpUserDetailService(PersonnelRepository personnelRepository) {
		super();
		this.personnelRepository = personnelRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("EmpUserDetailService() : " + username);

		Personnel personnel = personnelRepository.findByEmpId(username)
				.orElseThrow(() -> new UsernameNotFoundException(username + " : 사용자 조회 실패"));

		return new PersonnelLoginDTO(personnel);
	}



}
