package com.erp_mes.erp.approval.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.erp.approval.entity.Appr;
import com.erp_mes.erp.approval.entity.ApprLine;

@Repository
public interface ApprLineRepository extends JpaRepository<ApprLine,Long> {
    
	// 현재 로그인 사용자의 결재 처리
	@Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = """
	    UPDATE approval_line 
	    SET decision = :decision, 
	        dec_date = SYSDATE,
	        comments = :comments
	    WHERE req_id = :reqId 
	    AND appr_id = :apprId
	    AND (decision IS NULL OR decision = 'PENDING')
	    """, nativeQuery = true)
	int updateMyApprovalLine(@Param("reqId") Long reqId, 
	                         @Param("apprId") String apprId,
	                         @Param("decision") String decision,
	                         @Param("comments") String comments);
    
	// 아직 결재 안 한 사람 수 확인 (본인 제외하고 남은 결재자 수 확인)
	@Query(value = """
	    SELECT COUNT(*) 
	    FROM approval_line al
	    JOIN approval a ON al.req_id = a.req_id
	    WHERE al.req_id = :reqId 
	    AND al.appr_id != a.emp_id  -- 기안자 본인 제외
	    AND (al.decision IS NULL OR al.decision = 'PENDING')
	    """, nativeQuery = true)
	int countRemainingApprovals(@Param("reqId") Long reqId);
    
	// 반려된 결재가 있는지 확인
	@Query(value = """
	    SELECT COUNT(*) 
	    FROM approval_line 
	    WHERE req_id = :reqId 
	    AND decision = 'DENY'
	    """, nativeQuery = true)
	int countDeniedApprovals(@Param("reqId") Long reqId);

	 // 내가 결재해야 할 문서 목록 조회 (내가 기안한 문서는 제외)
	@Query(value = """
		SELECT
		    al.step_no,           -- 0
		    a.title,              -- 1
		    e.emp_name,           -- 2
		    dept.com_dt_nm,       -- 3
		    pos.com_dt_nm,        -- 4
		    a.request_at,         -- 5
		    al.dec_date,          -- 6
		    al.decision,          -- 7
		    a.req_id,             -- 8
		    a.req_type,           -- 9
		    a.emp_id              -- 10
		FROM approval_line al
		JOIN approval a ON al.req_id = a.req_id
		JOIN employee e ON a.emp_id = e.emp_id
		LEFT JOIN common_dt_code dept ON e.emp_dept_id = dept.com_dt_id
		LEFT JOIN common_dt_code pos ON e.emp_position = pos.com_dt_id
		WHERE al.appr_id = :loginId 
		AND a.emp_id != :loginId  -- 본인이 기안한 문서 제외
		ORDER BY a.request_at DESC, al.step_no ASC
		""", nativeQuery = true)
	List<Object[]> findToApproveList(@Param("loginId") String loginId);
	
	// 결재선 정보
	@Query(value = """
		SELECT 
		    al.step_no,
		    al.appr_id,
		    e.emp_name,
		    al.decision,
		    al.dec_date,
		    al.comments
		FROM approval_line al
		JOIN employee e ON al.appr_id = e.emp_id
		JOIN approval a ON al.req_id = a.req_id  -- approval 테이블 JOIN 추가
		WHERE al.req_id = :reqId
		AND al.appr_id != a.emp_id  -- 기안자 제외
		ORDER BY al.step_no ASC
		""", nativeQuery = true)
	List<Object[]> findApprovalLinesByReqId(@Param("reqId") Long reqId);
		
	// 결재대기 알람
	@Query(value = """
	    SELECT COUNT(*) 
	    FROM approval_line al
	    JOIN approval a ON al.req_id = a.req_id
	    WHERE al.appr_id = :loginId 
	    AND a.emp_id != :loginId
	    AND (al.decision IS NULL OR al.decision = 'PENDING')
	    """, nativeQuery = true)
	int countMyPendingApprovals(@Param("loginId") String loginId);

	Optional<ApprLine> findByApprReqIdAndApprId(Long reqId, String apprId);
}