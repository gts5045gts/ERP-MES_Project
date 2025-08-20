package com.bootstrap.study.personnel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "test_position")
public class Position {
	@Id
    @Column(name = "pos_id")
    private Long posId;

    @Column(name = "pos_name")
    private String posName;
}
