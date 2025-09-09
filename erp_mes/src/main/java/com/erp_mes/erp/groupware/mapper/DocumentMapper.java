package com.erp_mes.erp.groupware.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.erp_mes.erp.groupware.dto.DocumentDTO;


@Mapper
public interface DocumentMapper {

	List<DocumentDTO> findAllDocuments();

}
