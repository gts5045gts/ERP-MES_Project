package com.erp_mes.mes.business.dto;

import java.sql.Timestamp;
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
    private Timestamp createdAt;
	private Timestamp updatedAt;
	private String clientTypeCode;   // select box value값
	private String clientStatusCode; // select box value값
}
