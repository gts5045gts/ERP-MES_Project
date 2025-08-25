package com.bootstrap.study.approval.repository;

import com.bootstrap.study.approval.entity.Appr;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprRepository extends JpaRepository<Appr,Long> {
	
	// ㅇㅇ
	// JOIN 쿼리를 네이티브 쿼리로 추가
	@Query(value = """
		SELECT
		    al.step_no,
		    a.title,
		    e.emp_name,
		    a.request_at, 
		    al.dec_date,
		    al.decision,
		    a.req_id,
		    a.req_type,
		    a.emp_id,
		    a.tot_step as current_step
		FROM approval_line al
		JOIN approval a ON al.req_id = a.req_id
		JOIN employee e ON a.emp_id = e.emp_id
		ORDER BY a.request_at DESC, al.step_no ASC  
		""", nativeQuery = true)
		List<Object[]> findApprovalListWithJoin();
    
    
    //0821
   	// 승인버튼 누를시 승인처리되게 하기 (결재목록에서 대기 -> 승인으로 바뀜, 데이터도 반영)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE approval_line 
        SET decision = :decision, 
            dec_date = SYSDATE,
            comments = :comments
        WHERE req_id = :reqId 
        AND decision = 'PENDING'
        AND step_no = (
            SELECT MIN(step_no) 
            FROM approval_line 
            WHERE req_id = :reqId 
            AND decision = 'PENDING'
        )
        """, nativeQuery = true)
    int updateApprovalLineDecision(@Param("reqId") Long reqId, 
                                  @Param("decision") String decision,
                                  @Param("comments") String comments);
}
