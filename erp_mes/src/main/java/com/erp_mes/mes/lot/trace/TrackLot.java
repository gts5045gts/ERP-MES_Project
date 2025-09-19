package com.erp_mes.mes.lot.trace;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackLot {
	String tableName(); //로트해당 insert 되는 table
	String pkColumnName(); //로트해당 insert 되는 table
//	String domain(); // 공정 단계 (Inbound, Cutting, Machining, Assembly, Shipping 등)
//
//	boolean createLot() default true; // LOT 생성 여부
//
//	boolean linkParent() default true; // 부모 LOT 연결 여부
}
