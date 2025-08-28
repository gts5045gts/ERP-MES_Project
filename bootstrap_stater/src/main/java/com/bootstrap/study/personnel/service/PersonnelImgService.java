package com.bootstrap.study.personnel.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bootstrap.study.personnel.dto.PersonnelDTO;
import com.bootstrap.study.personnel.entity.Personnel;
import com.bootstrap.study.personnel.entity.PersonnelImg;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
@Log4j2
public class PersonnelImgService {

	@Value("${file.uploadBaseLocation}")
	private String uploadBaseLocation;
	
	@Value("${file.itemImgLocation}")
	private String itemImgLocation;

	public void registImg(Personnel personnel, MultipartFile empImg) throws IOException {
		
		// 원본 파일명 추출 
		String originalFileName = empImg.getOriginalFilename();
		
		//파일 이름 중복방지 대책
		String fileName = UUID.randomUUID().toString() + "_" + originalFileName;
		
		//기본 경로 + 상세 경로 서브 디렉토리 결합하여 디렉토리 생성
		Path uploadDir = Paths.get(uploadBaseLocation, itemImgLocation).toAbsolutePath().normalize();
		
		
		//생성된 Path 객체에 해당하는 디렉토리가 실제 디렉토리로 존재하지 않을 경우 해당 디렉토리 생성
		if(!Files.exists(uploadDir)) { 
			Files.createDirectories(uploadDir); // 하위 경로를 포함한 경로 상의 모든 디렉토리 생성
		}
		
		
		// 디렉토리와 파일명 결합하여 Path 객체 생성
		// => 기존 경로를 담고 있는 Path 객체의 resolve() 메서드를 사용하여 기존 경로에 파일명 추가
		Path uploadPath = uploadDir.resolve(fileName);
		
		
		// 임시 경로에 보관되어 있는 첨부파일 1개를 실제 업로드 경로로 이동
		empImg.transferTo(uploadPath);
		
		

		
		PersonnelImg perImg = new PersonnelImg();
		perImg.setPersonnel(personnel);
		perImg.setImgId(fileName);
		perImg.setFileName(originalFileName);
//		perImg.getLocation(itemImgLocation);
		
	}
	
	
}
