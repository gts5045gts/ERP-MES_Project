package com.erp_mes.erp.personnel.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.erp.approval.entity.Appr;
import com.erp_mes.erp.approval.repository.ApprRepository;
import com.erp_mes.erp.commonCode.repository.CommonDetailCodeRepository;
import com.erp_mes.erp.personnel.dto.PersonnelDTO;
import com.erp_mes.erp.personnel.dto.PersonnelTransferDTO;
import com.erp_mes.erp.personnel.entity.Personnel;
import com.erp_mes.erp.personnel.entity.PersonnelTransfer;
import com.erp_mes.erp.personnel.repository.PersonnelRepository;
import com.erp_mes.erp.personnel.repository.PersonnelTransferRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor 
@Log4j2
public class PersonnelTransferService {
	private final PersonnelTransferRepository personnelTransferRepository;
	private final PersonnelRepository personnelRepository;
	private final CommonDetailCodeRepository commonDetailCodeRepository;
	private final ApprRepository apprRepository;

	@Transactional
	public void processPersonnelTransfer(Long reqId) {
		log.info("인사발령 최종 승인 처리 시작 - reqId: {}", reqId);

		Appr appr = apprRepository.findById(reqId)
				.orElseThrow(() -> new IllegalArgumentException("결재 문서 정보를 찾을 수 없습니다: " + reqId));

		if (!"TRANSFER".equals(appr.getReqType())) {
			log.warn("인사 발령 문서가 아닙니다. 처리 중단: {}", reqId);
			return;
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, String> transInfo = objectMapper.readValue(appr.getContent(),
					new TypeReference<Map<String, String>>() {
					});
			String empId = transInfo.get("empId");

			Personnel personnel = personnelRepository.findByEmpId(empId)
					.orElseThrow(() -> new IllegalArgumentException("사원 정보 없음: " + empId));
			String name = transInfo.get("name");
			String reason = transInfo.get("transReason");
			String transType = transInfo.get("transType");
			String oldDeptId = personnel.getDepartment().getComDtId();
			String oldPosId = personnel.getPosition().getComDtId();
			String newDeptId = transInfo.get("newDeptId");
			String newPosId = transInfo.get("newPosId");
			String transDateStr = transInfo.get("transDate");

			PersonnelTransfer transfer = PersonnelTransfer.builder()
					.reqId(reqId)
					.empId(empId)
					.name(name)
					.reason(reason)
					.transferType(transType)
					.oldDept(oldDeptId)
					.newDept(newDeptId)
					.oldPosition(oldPosId)
					.newPosition(newPosId)
					.transDate(LocalDate.parse(transDateStr))
					.create(Timestamp.from(Instant.now())).build();
			
			personnelTransferRepository.save(transfer);

			personnel.setDepartment(commonDetailCodeRepository.findByComDtId(newDeptId)
					.orElseThrow(() -> new IllegalArgumentException("신규 부서 코드 없음")));
			personnel.setPosition(commonDetailCodeRepository.findByComDtId(newPosId)
					.orElseThrow(() -> new IllegalArgumentException("신규 직급 코드 없음")));
			personnelRepository.save(personnel);

			log.info("인사 발령 최종 승인 처리 완료 - reqId: {}", reqId);

		} catch (JsonProcessingException e) {
			throw new RuntimeException("인사발령 데이터 파싱 오류", e);
		}
	}
	
	// 인사발령 목록 조회
	public List<PersonnelTransferDTO> getTransferPersonnels() {
 		
 		// PersonnelTransfer 엔티티 목록을 가져와서 DTO로 변환
 		List<PersonnelTransfer> personnelList = personnelTransferRepository.findAll();
 		return personnelList.stream()
 				.map(transfer -> PersonnelTransferDTO.fromEntity(transfer, commonDetailCodeRepository))
 				.collect(Collectors.toList());
 	}
	
	
}
