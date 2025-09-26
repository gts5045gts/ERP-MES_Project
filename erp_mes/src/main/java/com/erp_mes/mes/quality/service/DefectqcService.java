package com.erp_mes.mes.quality.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp_mes.mes.pop.dto.DefectDTO;
import com.erp_mes.mes.quality.mapper.QualityMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class DefectqcService {
	
	private final QualityMapper qualityMapper;

	
	@Transactional(readOnly = true)
	public List<DefectDTO> getDefectHistory(String defectLocation) { 
	    Map<String, Object> params = new HashMap<>();
	    
	    params.put("defectLocation", defectLocation);
	    return qualityMapper.getDefectHistory(params);
	}
	
    @Transactional(readOnly = true)
    public Map<String, Object> getDefectDashboardData() {
        // 1. 주요 통계 지표 (총 건수, 총 검사 수량, 총 불량 수량)를 포함하는 맵 조회
        Map<String, Object> summary = qualityMapper.getDefectSummary();
        
        // 2. 월별 불량 추이 (차트 데이터)
        List<Map<String, Object>> monthlyTrend = qualityMapper.getMonthlyDefectTrend();
        
        // 3. 유형별 비율 (차트 데이터 및 TOP 1 계산용)
        List<Map<String, Object>> typeRatios = qualityMapper.getTypeRatio();

        Map<String, Object> dashboardData = new HashMap<>();
        
        // 4. 데이터 가공 및 맵핑
        
        // 총 불량 건수 (Summary 맵에서 가져오거나, typeRatios를 합산)
        int totalDefectCount = (summary != null && summary.containsKey("TOTAL_COUNT")) ? 
                               ((Number) summary.get("TOTAL_COUNT")).intValue() : 0;
        
        // 총 불량률 계산
        double totalQuantity = (summary != null && summary.containsKey("TOTAL_QTY")) ? 
                               ((Number) summary.get("TOTAL_QTY")).doubleValue() : 0.0;
        double totalDefectQty = (summary != null && summary.containsKey("DEFECT_QTY")) ? 
                                ((Number) summary.get("DEFECT_QTY")).doubleValue() : 0.0;
        
        double defectRate = (totalQuantity > 0) ? (totalDefectQty / totalQuantity) * 100 : 0.0;

        String topDefectType = (typeRatios != null && !typeRatios.isEmpty() && typeRatios.get(0).containsKey("DEFECTTYPENAME")) ? 
                               typeRatios.get(0).get("DEFECTTYPENAME").toString() : "없음";
                               
        dashboardData.put("totalDefectCount", totalDefectCount);
        dashboardData.put("totalDefectRate", Math.round(defectRate * 100) / 100.0); // 소수점 둘째 자리까지
        dashboardData.put("topDefectType", topDefectType);
        dashboardData.put("monthlyTrend", monthlyTrend);
        dashboardData.put("typeRatios", typeRatios);
        
        return dashboardData;
    }
}
