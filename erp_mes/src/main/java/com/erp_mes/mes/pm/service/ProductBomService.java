package com.erp_mes.mes.pm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.mes.pm.dto.ProductDTO;
import com.erp_mes.mes.pm.mapper.ProductBomMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class ProductBomService {
	
	private final ProductBomMapper productBomMapper;

	public ProductBomService(ProductBomMapper productBomMapper) {
		this.productBomMapper = productBomMapper;
	}
	
	// 제품 리스트
	public List<ProductDTO> getProductList() {
		return productBomMapper.getProductList();
	}

	// 제품유형(공통코드)
	public List<CommonDetailCodeDTO> getCommonPtype() {
		List<CommonDetailCodeDTO> commonPtype = productBomMapper.getCommonPtype("PTYPE");
		return commonPtype;
	}

	// 단위(공통코드)
	public List<CommonDetailCodeDTO> getCommonUnit() {
		List<CommonDetailCodeDTO> commonUnit = productBomMapper.getCommonUnit("UNIT");
		return commonUnit;
	}

	// 제품 등록
	public void productRegist(ProductDTO productDTO) {
		productBomMapper.ProductRegist(productDTO);
	}

}
