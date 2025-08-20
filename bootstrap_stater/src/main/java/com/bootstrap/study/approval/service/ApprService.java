package com.bootstrap.study.approval.service;

import com.bootstrap.study.approval.dto.ApprDTO;
import com.bootstrap.study.approval.entity.Appr;
import com.bootstrap.study.approval.repository.ApprRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2; // Log4j2 임포트 추가!
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2 // 로그를 사용하기 위한 어노테이션 추가!
public class ApprService {

    private final ApprRepository apprRepository;
    // TODO: 직원 정보 조회를 위해 EmployeeRepository 등이 필요합니다.
    // private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<ApprDTO> getApprovalList() {

        // 1. DB에서 데이터를 몇 건 가져왔는지 로그로 확인합니다.
        List<Appr> apprList = apprRepository.findAll(Sort.by(Sort.Direction.DESC, "reqId"));
        log.info(">>>>>> DB에서 조회된 결재 문서 건수: " + apprList.size() + "건");

        // 2. DTO로 변환하는 로직 (기존과 동일)
        List<ApprDTO> apprDtoList = apprList.stream()
                .map(appr -> {
                    ApprDTO dto = ApprDTO.fromEntity(appr);
                    dto.setDrafterName(appr.getEmpId() + " (이름)");
                    dto.setDepartment("인사부");
                    dto.setCurrentApprover("김이사 (나)");
                    return dto;
                })
                .collect(Collectors.toList());
        
        // 3. 최종적으로 Controller에게 몇 건의 데이터를 전달하는지 로그로 확인합니다.
        log.info(">>>>>> DTO로 변환 후 최종 반환 건수: " + apprDtoList.size() + "건");

        return apprDtoList;
    }
}