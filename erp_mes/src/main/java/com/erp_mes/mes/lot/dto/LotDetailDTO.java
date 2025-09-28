package com.erp_mes.mes.lot.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LotDetailDTO {

    private String lotId;
    private BigDecimal outCount;
    private String materialName;
    private String materialType;
    private String materialId;
    private String equipNm;
    private String equipId;
    private String note;

    public LotDetailDTO(String lotId, BigDecimal outCount, String materialName, String materialType, String materialId) {
        this.lotId = lotId;
        this.outCount = outCount;
        this.materialName = materialName;
        this.materialType = materialType;
        this.materialId = materialId;
    }
    
    public LotDetailDTO(String equipNm, String equipId, String note) {
        this.equipNm = equipNm;
        this.equipId = equipId;
        this.note = note;
    }

}
