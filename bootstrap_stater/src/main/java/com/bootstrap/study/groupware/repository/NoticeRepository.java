package com.bootstrap.study.groupware.repository;

import com.bootstrap.study.groupware.entity.Notice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByNotType(String notType);

    // JPQL을 사용하여 Notice와 Personnel을 조인
    @Query("SELECT n FROM Notice n JOIN n.employee e WHERE e.department.comDtId = :empDeptId AND n.notType = :notType")
    List<Notice> findByEmpDeptIdAndNotType(@Param("empDeptId") String empDeptId, @Param("notType") String notType); 
    
    @Query("SELECT n FROM Notice n JOIN n.employee p WHERE p.department.comDtNm = :empDeptName AND n.notType = :notType")
    List<Notice> findByEmpDeptNameAndNotType(@Param("empDeptName") String empDeptName, @Param("notType") String notType);
}

