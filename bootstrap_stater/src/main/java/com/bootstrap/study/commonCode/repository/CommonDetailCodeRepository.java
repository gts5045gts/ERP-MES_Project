package com.bootstrap.study.commonCode.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bootstrap.study.commonCode.entity.CommonCode;
import com.bootstrap.study.commonCode.entity.CommonDetailCode;

@Repository
public interface CommonDetailCodeRepository extends JpaRepository<CommonDetailCode, String> {

	List<CommonDetailCode> findByComId_ComId(String comId);

	// 정렬순서
	List<CommonDetailCode> findByComIdOrderByComDtOrderAsc(CommonCode comId);

}
