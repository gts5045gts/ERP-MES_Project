package com.erp_mes.erp.commonCode.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.erp_mes.erp.commonCode.entity.CommonCode;
import com.erp_mes.erp.commonCode.entity.CommonDetailCode;

@Repository
public interface CommonDetailCodeRepository extends JpaRepository<CommonDetailCode, String> {

	List<CommonDetailCode> findByComId_ComId(String parentId);

	// 정렬순서
	List<CommonDetailCode> findByComIdOrderByComDtOrderAsc(CommonCode comId);

	//검색	
	@Query("SELECT c FROM CommonDetailCode c " +
			"WHERE c.comId.comId = :parentId " +
			"AND (" +
			"LOWER(c.comDtNm) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"STR(c.comDtOrder) LIKE CONCAT('%', :keyword, '%')" + // 숫자 컬럼 검색
			")")
	List<CommonDetailCode> searchByParentAndKeyword(@Param("parentId") String parentId, @Param("keyword") String keyword);

	Optional<CommonDetailCode> findByComDtId(String comDtId);




}
