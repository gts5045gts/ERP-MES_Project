package com.bootstrap.study.commonCode.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bootstrap.study.commonCode.entity.CommonCode;
import com.bootstrap.study.commonCode.entity.CommonDetailCode;

@Repository
public interface CommonDetailCodeRepository extends JpaRepository<CommonDetailCode, String> {

	List<CommonDetailCode> findByComId_ComId(String comId);

	// 정렬순서
	List<CommonDetailCode> findByComIdOrderByComDtOrderAsc(CommonCode comId);

	
	
	// 검색
	@Query("SELECT d FROM CommonDetailCode d WHERE " +
	       "LOWER(d.comDtId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	       "LOWER(d.comDtNm) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	       "LOWER(d.useYn) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<CommonDetailCode> searchDtCode(@Param("keyword") String keyword);

}
