package com.bootstrap.study.groupware.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bootstrap.study.groupware.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {

}
