package com.erp_mes.mes.pm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WorkOrderMapper {

    // 작업지시 상태를 '작업완료'로 업데이트하는 메소드
    void updateWorkOrderStatus(@Param("workOrderId") Long workOrderId);
}