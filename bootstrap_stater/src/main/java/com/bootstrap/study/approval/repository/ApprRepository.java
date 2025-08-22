package com.bootstrap.study.approval.repository;

import com.bootstrap.study.approval.entity.Appr;
import com.bootstrap.study.personnel.entity.Personnel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
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

    
    //로그인 되면 본인을 제외한 값만 가져와야함 현재는 임의로 1로 해놓음
    @Query(value = "" +
			"SELECT * FROM " +
			"C##TEAM1.employee e " +
			"JOIN " +
			"C##TEAM1.test_dept d ON e.emp_dept_id = d.dept_id " +
			"JOIN " +
			"C##TEAM1.test_position p ON e.emp_position = p.pos_id " +
			"WHERE e.emp_name LIKE %:keyword% and e.emp_id <> 2025082229"  +
			"",
			nativeQuery = true)
	List<Personnel> findByNameContainingIgnoreCase(@Param("keyword") String keyword);
}
