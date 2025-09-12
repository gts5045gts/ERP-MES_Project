package com.erp_mes.mes.pm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.erp_mes.mes.pm.dto.ProductDTO;

@Mapper
public interface ProductBomMapper {

	// 제품 리스트
	List<ProductDTO> getProductList();

}
