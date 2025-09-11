package com.erp_mes.mes.lot.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "lot_master")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LotMaster {
 	@Id
 	@Column(updatable = false, length = 50)
    private String lotId;
 	
// 	각테이블 고유 pk id
 	@Column(name = "TARGET_ID", nullable = false, updatable = false)
 	private Long targetId;
 	
// 	조회 대상 테이블
 	@Column(length = 40, nullable = false, updatable = false)
 	private String tableName;

//RM, PR, FG, QA 등
 	@Column(length = 20, nullable = false)
    private String type; 
 	
    @Column(length = 50)
    private String materialCode;
    
//수량
    private int qty;
    
    @Column(length = 50)
    private String machineId;
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    
    
}
