package com.bootstrap.study.approval.repository;

import com.bootstrap.study.approval.entity.Appr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprRepository extends JpaRepository<Appr,Long> {
}
