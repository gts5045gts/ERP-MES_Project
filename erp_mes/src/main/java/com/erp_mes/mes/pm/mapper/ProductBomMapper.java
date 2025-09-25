package com.erp_mes.mes.pm.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.mes.pm.dto.BomDTO;
import com.erp_mes.mes.pm.dto.ProductDTO;

@Mapper
public interface ProductBomMapper {

	// 제품 리스트
	List<ProductDTO> getProductList();

	// 제품유형
	List<CommonDetailCodeDTO> getCommonPtype(String comId);

	// 단위
	List<CommonDetailCodeDTO> getCommonUnit(String comId);

	// 제품 등록
	int ProductRegist(ProductDTO productDTO);

	// bom 리스트
	List<BomDTO> getBomList(String productId);

	// bom 등록
	int insertBomList(BomDTO bomDTO);

	List<BomDTO> getBomListRoute(String productId);
	

}
