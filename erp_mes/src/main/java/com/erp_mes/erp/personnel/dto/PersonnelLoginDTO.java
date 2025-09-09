package com.erp_mes.erp.personnel.dto;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.erp_mes.erp.personnel.entity.Personnel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@ToString
@Log4j2
@NoArgsConstructor
public class PersonnelLoginDTO implements UserDetails {
	private String empId;
	private String name;		
	private String passwd;
	private String empDeptId;
	private String empLevelId;
	private String empDeptName;
	
	 public PersonnelLoginDTO(Personnel personnel) {
	        this.empId = personnel.getEmpId();
	        this.name = personnel.getName();
	        this.passwd = personnel.getPasswd(); // 엔티티의 getPasswd()를 사용
	        this.empDeptId = personnel.getDepartment() != null ? personnel.getDepartment().getComDtId() : null;

	        // 조인된 CommonDetailCode 객체에서 com_dt_id를 가져와서 empLevelId에 할당
	        if (personnel.getDepartment() != null) {
	            this.empDeptId = personnel.getDepartment().getComDtId();
	            this.empDeptName = personnel.getDepartment().getComDtNm();
	        }
	        if (personnel.getLevel() != null) {
	            this.empLevelId = personnel.getLevel().getComDtId();
	        }
	    }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return Collections.singletonList(
		        new SimpleGrantedAuthority("ROLE_" + empLevelId)
		        );
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.passwd;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.empId;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
