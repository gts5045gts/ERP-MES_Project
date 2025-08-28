package com.bootstrap.study.groupware.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bootstrap.study.groupware.dto.DocumentDTO;


@Mapper
public interface DocumentMapper {

	List<DocumentDTO> findAllDocuments();

}
