package com.erp_mes.mes.lot.constant;

//테이블명으로 치환함
public enum LotDomain {
	 
	// 1️⃣ 자재(Material)
	  MATERIAL("material", "RM"), // 원자재
	  PROCESS("process", "PR"), // 반제품
	  FINISHED("inspection", "FG"), // 완제품
	  QUALITY("quality", "QA"), // 품질검사
	  PRODUCT("product", "PD"), // POP 
	  EQUIPMENT("equipment", "EQ"), // 설비
	  CONSUMABLE("consumable", "SP"), // 소모품 → 새로 추가
	  
	  // 2️⃣ 공정(Process / 작업 공정)
	  INPUT("input", "IN"), // 입고
	  POP("work_result", "POP"), // 공정
	  CUTTING("cutting", "CUT"), // 절단 
	  MACHINING("machining", "MCH"), // 성형/가공
	  ASSEMBLY("assembly", "AS"), // 조립 
	  OUTPUT("output", "OUT"), // 포장/출하
	  SHIPPING("shipping", "FG"), // 포장/출하
	  ETC("etc", "XX"); // 기타 미정의
	
	/*
	 * // 1️⃣ 자재(Material) MATERIAL("RM", "원자재"), PROCESS("PR", "반제품"),
	 * FINISHED("FG", "완제품"), QUALITY("QA", "품질검사"), POP("POP", "POP"),
	 * EQUIPMENT("EQ", "설비"), CONSUMABLE("SP", "소모품"),
	 * 
	 * // 2️⃣ 공정(Process / 작업 공정) INBOUND("RM", "입고"), CUTTING("CUT", "절단"),
	 * MACHINING("MCH", "성형/가공"), ASSEMBLY("AS", "조립"), SHIPPING("FG", "포장/출하"),
	 * ETC("XX", "기타 미정의");
	 */

    private final String domain;
    private final String prefix;

    LotDomain(String domain, String prefix) {
        this.domain = domain;
        this.prefix = prefix;
    }

    public String getDomain() {
        return domain;
    }

    public String getPrefix() {
        return prefix;
    }

    // domain 문자열로 enum 찾기
    public static LotDomain fromDomain(String domain) {
        for (LotDomain d : values()) {
            if (d.getDomain().equalsIgnoreCase(domain)) {
                return d;
            }
        }
        return ETC; // 매칭 안되면 ETC
    }
}
