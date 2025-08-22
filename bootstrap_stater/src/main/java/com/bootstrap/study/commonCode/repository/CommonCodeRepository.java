package com.bootstrap.study.commonCode.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bootstrap.study.commonCode.entity.CommonCode;


@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, String> {

	
	// 공통코드 리스트 화면
	List<CommonCode> findAllByOrderByCreatedAtDesc();
}
