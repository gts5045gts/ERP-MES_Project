package com.erp_mes.mes.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp_mes.mes.stock.entity.Inv;

public interface InvRepository extends JpaRepository<Inv, Long> {

}
