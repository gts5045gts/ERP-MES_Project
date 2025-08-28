//package com.bootstrap.study.personnel.entity;
//
//import org.springframework.web.multipart.MultipartFile;
//
//import com.bootstrap.study.personnel.dto.PersonnelDTO;
//import com.bootstrap.study.personnel.dto.PersonnelImgDTO;
//import com.bootstrap.study.personnel.repository.PersonnelRepository;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.OneToOne;
//import jakarta.persistence.Table;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//
//@Entity
//@Table(name = "employee_Img")
//@NoArgsConstructor
//@Getter
//@Setter
//@ToString
//public class PersonnelImg {
////프로필 사진 entity
//	
//	@Id
//	@Column(nullable = false, name = "img_id")
//	private String imgId;								//이미지 번호
//	
//	@OneToOne(fetch = FetchType.LAZY)					//한사람당 하나씩 중복 없음
//	@JoinColumn(nullable = false, name = "emp_id", referencedColumnName = "emp_id")
//	private Personnel personnel;
//	
//	@Column(nullable = false, name = "img_id")
//	private String name;
//	@Column(nullable = false, name = "img_id")
//	private String location;
//	@Column(nullable = false, name = "img_id")
//	private String fileName;
//	
//	
//public static PersonnelImg fromDTO(PersonnelImgDTO imgDTO , PersonnelRepository repo) {		//repo 추가로 사용하기 위함
//		
//	PersonnelImg img = new PersonnelImg();
//		
//	
//		img.setImgId(imgDTO.getImgId());
//		img.setName(imgDTO.getName());
//		img.setLocation(imgDTO.getLocation());
//		
//		//personnel 타입으로 변환후 넘겨주기 
//		 if(imgDTO.getEmpId() != null) {
//			 Personnel empId = repo.findById(imgDTO.getEmpId())
//		            .orElseThrow(() -> new IllegalArgumentException("사진 주인이 없습니다."));
//			 img.setPersonnel(empId);
//		 }
//		
//		
//		
//		return img;
//	}
//
//
//	
//	
//	
//	
//}
