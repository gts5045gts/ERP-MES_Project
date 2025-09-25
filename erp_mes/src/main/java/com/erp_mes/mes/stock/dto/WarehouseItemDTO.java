package com.erp_mes.mes.stock.dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class WarehouseItemDTO {
// 필요해서 일단 여기 만들었습니다.
	private String manageId; // manage_id
    private String warehouseId; // warehouse_id
    private String productId; // product_id
    private Integer itemAmount; // item_amount
    private Integer maxAmount; // max_amount
    private String useYn; // use_yn
    private String description; // description
    private Timestamp updateDate; // update_date
    private String empId; // emp_id
    private String locationId; // location_id
    private String lotId; // lot_id
    private String materialId; // material_id
}
