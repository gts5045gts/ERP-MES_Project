package com.bootstrap.study.approval.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.bootstrap.study.approval.constant.ApprReqType;
import com.bootstrap.study.approval.constant.ApprStatus;

import com.bootstrap.study.approval.entity.Appr;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ApprDTO {
	
	private Long reqId;
	
	private String empId;
	
	private ApprReqType reqType;
	
	@NotBlank(message = "제목은 필수 입력값입니다!") // 공백만 있거나, 길이가 0인 문자열, null 값을 허용하지 않음
	private String title;
	
	private String content;
	
	private LocalDate requestAt;
	
	private LocalDateTime createAt;
	
	private LocalDateTime updateAt;
	
 	private ApprStatus status = ApprStatus.REQUESTED;
 	
// 	private Integer currentStep;
 	
 	private Integer totStep;
 	
 	// ============== [ 추가된 필드 ] ==============
    private String drafterName;     // 기안자 이름
    private String department;      // 기안자 부서
    private String currentApprover; // 현재 결재자 이름
    // ㅇㅇ
    private LocalDateTime decDate; // 결재일자 추가
    private String decision; // 결재 상태 (PENDING, ACCEPT, DENY)
    private Integer stepNo; // 결재순번 추가
    // ===========================================
    // ㅇㅇ
    // 상태를 한글로 변환하는 메서드 추가
    public String getStatusLabel() {
        if ("PENDING".equals(decision) || decision == null) {
            return "대기";
        } else if ("ACCEPT".equals(decision)) {
            return "승인";
        } else if ("DENY".equals(decision)) {
            return "반려";
        }
        return "미정";
    }
    
 	private List<ApprLineDTO> ApprLineDTOList;
 	private List<ApprDetailDTO> ApprDetailDTOList;

 	@Builder
	public ApprDTO(Long reqId, String empId, ApprReqType reqType, String title, String content, LocalDate requestAt, LocalDateTime createAt,
			LocalDateTime updateAt, ApprStatus status, Integer totStep) {
		this.reqId = reqId;
		this.empId = empId;
		this.reqType = reqType;
		this.title = title;
		this.content = content;
		this.requestAt = requestAt;
		this.createAt = createAt;
		this.updateAt = updateAt;
		this.status = status;
//		this.currentStep = currentStep;
		this.totStep = totStep;
	}

	private static ModelMapper modelMapper = new ModelMapper();

 	public Appr toEntity() { return modelMapper.map(this, Appr.class); }

	public static ApprDTO fromEntity(Appr appr) { return modelMapper.map(appr, ApprDTO.class); }
}
