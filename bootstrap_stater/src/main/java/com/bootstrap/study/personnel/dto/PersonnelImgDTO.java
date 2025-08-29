//package com.bootstrap.study.personnel.dto;
//
//import com.bootstrap.study.personnel.entity.PersonnelImg;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@ToString
//public class PersonnelImgDTO {
//	
//	private String imgId;
//	private String empId;
//	private String name;
//	private String location;
//	private String fileName;
//	
//	
//	public static PersonnelImgDTO fromEntity(PersonnelImg img) {
//		return PersonnelImgDTO.builder().imgId(img.getImgId()).empId(img.getPersonnel().getEmpId()).name(img.getName()).location(img.getLocation())
//				.build();
//	}
//}
