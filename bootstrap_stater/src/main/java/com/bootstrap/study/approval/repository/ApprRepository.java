package com.bootstrap.study.approval.repository;

import com.bootstrap.study.approval.entity.Appr;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
		        a.create_at,
		        al.dec_date,
		        al.decision,
		        a.req_id,
		        a.req_type,
		        a.emp_id,
		        a.current_step
		    FROM C##TEAM1.approval_line al
		    JOIN C##TEAM1.approval a ON al.req_id = a.req_id
		    JOIN C##TEAM1.employee e ON a.emp_id = e.emp_id
		    ORDER BY a.create_at DESC, al.step_no ASC
		    """, nativeQuery = true)
    List<Object[]> findApprovalListWithJoin();
}
