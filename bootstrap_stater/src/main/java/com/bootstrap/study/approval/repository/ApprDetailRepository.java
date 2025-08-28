package com.bootstrap.study.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bootstrap.study.approval.entity.ApprDetail;

@Repository
public interface ApprDetailRepository extends JpaRepository<ApprDetail, Long> {

	// 연차 사용
	List<ApprDetail> findByApprReqId(Long reqId);

}
