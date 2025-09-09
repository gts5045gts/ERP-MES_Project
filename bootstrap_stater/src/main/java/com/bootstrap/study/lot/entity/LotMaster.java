package com.bootstrap.study.lot.entity;

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
