//package com.bootstrap.study.personnel.entity;
//
//import java.sql.Timestamp;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
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
//
//	@Id
//	@Column(nullable = false, name = "img_id")
//	private Long imgId;								//이미지 번호
//	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(nullable = false, name = "emp_id", referencedColumnName = "emp_id")
//	private Personnel personnel;
//	
//	private String name;
//	private String location;
//	
//	
//}
