package com.bootstrap.study.personnel.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bootstrap.study.personnel.entity.Personnel;
import com.bootstrap.study.personnel.mapper.PersonnelMapper;
import com.bootstrap.study.personnel.repository.PersonnelRepository;

@Service
public class PersonnelService {

	private final PersonnelMapper personnelMapper;
	private final PersonnelRepository personnelRepository;
	
	public PersonnelService(PersonnelMapper personnelMapper, PersonnelRepository personnelRepository) {
		this.personnelMapper = personnelMapper;
		this.personnelRepository = personnelRepository;
	}

	public List<Personnel> getPersonList() {
		
		
//		return personnelRepository.;
		return null;
	}
	
	
	
	
	
	
	
}
