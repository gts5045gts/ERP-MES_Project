package com.erp_mes.mes.pm.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.erp_mes.erp.commonCode.dto.CommonDetailCodeDTO;
import com.erp_mes.mes.pm.dto.BomDTO;
import com.erp_mes.mes.pm.dto.MaterialDTO;
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

	// bom 리스트
	public List<BomDTO> getBomList(String productId) {
		return productBomMapper.getBomList(productId);
	}

	// bom 등록
	// 여러개의 자재를 등록할 경우 반복문으로 하나씩 insert
	public void insertBomList(BomDTO bomDTO) {
	    for (MaterialDTO m : bomDTO.getMaterials()) {
	        BomDTO row = new BomDTO();
	        row.setBomId(bomDTO.getBomId());
	        row.setProductId(bomDTO.getProductId());
	        row.setRevisionNo(bomDTO.getRevisionNo());
	        row.setMaterialId(m.getMaterialId());
	        row.setQuantity(m.getQuantity());
	        row.setUnit(m.getUnit());

	        productBomMapper.insertBomList(row); // 단일 insert 호출
	    }
	}

}
