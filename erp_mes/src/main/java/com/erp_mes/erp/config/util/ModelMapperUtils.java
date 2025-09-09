package com.erp_mes.erp.config.util;

import org.modelmapper.ModelMapper;

// X 클래스와 XDTO 클래스간의 상호 변환을 수행할 클래스
// => ModelMapper 객체를 활용하여 모든 클래스 간의 일치하는 필드를 사용하여 상호 변환
public class ModelMapperUtils {
	private static final ModelMapper MODEL_MAPPER = new ModelMapper();
	
	// A객체와 B클래스를 전달 받아 A객체를 B클래스 타입 객체로 변환하여 리턴하는 메서드 정의
	// ex) convertObjectByMap(member, MemberDTO.class) => Member 엔티티를 전달받아 MemberDTO 객체로 변환하여 리턴 
	// ex) convertObjectByMap(item, ItemDTO.class) => Item 엔티티를 전달받아 ItemDTO 객체로 변환하여 리턴 
	// -> 메서드가 호출될 때마다 전달되는 객체와 클래스명에 대한 제네릭 타입을 적용하기 위해 메서드 단위 제네릭 타입 지정
	// 	  (메서드 리턴타입 앞에 메서드에서 사용할 제네릭타입을 <> 내부에 지정)
	public static <T, D> D convertObjectByMap(T sourceObject, Class<D> destinationClass) {
		// 첫번째 파라미터 T : 엔티티 객체
		// 두번째 파라미터 D : 변환할 클래스명
		// 리턴타입 D : 변환된 D 타입 객체
		return MODEL_MAPPER.map(sourceObject, destinationClass);
	}
}
