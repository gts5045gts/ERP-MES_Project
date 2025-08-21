package com.bootstrap.study.personnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bootstrap.study.personnel.entity.Position;

public interface PositionRepository extends JpaRepository<Position, Long>{

}
