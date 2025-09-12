package com.erp_mes.mes.lot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp_mes.mes.lot.entity.LotProcessHistory;

@Repository
public interface LotProcessHistoryRepository extends JpaRepository<LotProcessHistory, Object> {

}
