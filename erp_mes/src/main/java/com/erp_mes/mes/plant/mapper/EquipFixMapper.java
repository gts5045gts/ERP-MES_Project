package com.erp_mes.mes.plant.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.mes.plant.dto.EquipFixDTO;
import com.erp_mes.mes.plant.service.ProcessService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Mapper
public interface EquipFixMapper {

	List<EquipFixDTO> findById(String equipId);

}
