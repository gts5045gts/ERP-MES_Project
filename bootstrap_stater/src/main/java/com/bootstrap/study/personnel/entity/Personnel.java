package com.bootstrap.study.personnel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Personnel {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "emp_id")
    private String empId; // 사원번호
    
    // Mybatis 방식으로 -> dept_name이 필요하면 join 해야 함.
    @Column(nullable = false, name = "emp_dept_id")
    private String dept; 
    
    @Column(nullable = false, name = "emp_position")
    private String position; // 직책 (예: 사원, 대리, 과장)
    
    @Column(nullable = false, name = "emp_name")
    private String name; // 이름.
    
    @Column(nullable = false, name = "emp_phone")
    private String phone; // 전화번호

    @Column(nullable = false, name = "emp_email")
    private String email; // 이메일

    // JPA 방식 -> employee.getDeaprtment.getDeptName() 이런식으로 부서 정보 바로 접근 가능
//    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
//    @JoinColumn(name = "dept_id", nullable = false)
//    private Department department;


    @Override
    public String toString() {
        return "Employee{" +
               "id=" + id +
               ", empId='" + empId + '\'' +
               ", name='" + name + '\'' +
               ", position='" + position + '\'' +
               ", phone='" + phone + '\'' +
               ", email='" + email + '\'' +
               ", dept=" + (dept != null ? dept : "N/A") +
               '}';
    }

}
