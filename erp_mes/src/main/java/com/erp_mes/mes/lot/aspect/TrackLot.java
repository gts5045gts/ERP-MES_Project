package com.erp_mes.mes.lot.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackLot {
    String step();       // 공정 단계 (Inbound, Cutting, Machining, Assembly, Shipping 등)
    boolean createLot() default true;  // LOT 생성 여부
    boolean linkParent() default true; // 부모 LOT 연결 여부
}
