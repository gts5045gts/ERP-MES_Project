package com.erp_mes.mes.business.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ClientDTO {
    private String clientId;
    private String clientName;
    private String clientType;
    private String businessNumber;
    private String ceoName;
    private String clientAddress;
    private String clientPhone;
    private String clientStatus;
    private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
