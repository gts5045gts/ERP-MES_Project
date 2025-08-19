package com.bootstrap.study.groupware.repository;

import com.bootstrap.study.groupware.entity.Notice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
	 List<Notice> findByNotType(String notType);
}