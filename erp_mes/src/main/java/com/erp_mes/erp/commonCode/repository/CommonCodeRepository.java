package com.erp_mes.erp.commonCode.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.erp_mes.erp.commonCode.entity.CommonCode;


@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, String> {

	
	// 공통코드 리스트 화면
	List<CommonCode> findAllByOrderByCreatedAtDesc();

	// 검색
	@Query("SELECT c FROM CommonCode c WHERE " +
           "LOWER(c.comId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.comNm) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.useYn) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<CommonCode> searchCode(@Param("keyword") String keyword);

}
