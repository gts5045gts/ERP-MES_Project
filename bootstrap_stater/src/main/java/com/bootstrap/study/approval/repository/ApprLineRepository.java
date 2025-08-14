package com.bootstrap.study.approval.repository;

import com.bootstrap.study.approval.entity.AppLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprLineRepository extends JpaRepository<AppLine,Long> {
}
