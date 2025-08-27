package com.bootstrap.study.approval.repository;

import com.bootstrap.study.approval.entity.ApprLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ApprLineRepository extends JpaRepository<ApprLine,Long> {
    
    // 0827 현재 로그인 사용자의 결재 처리
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
    
    // 0827 아직 결재 안 한 사람 수 확인
    @Query(value = """
        SELECT COUNT(*) 
        FROM approval_line 
        WHERE req_id = :reqId 
        AND (decision IS NULL OR decision = 'PENDING')
        """, nativeQuery = true)
    int countRemainingApprovals(@Param("reqId") Long reqId);
    
    // 0827 반려된 결재가 있는지 확인
    @Query(value = """
        SELECT COUNT(*) 
        FROM approval_line 
        WHERE req_id = :reqId 
        AND decision = 'DENY'
        """, nativeQuery = true)
    int countDeniedApprovals(@Param("reqId") Long reqId);
}