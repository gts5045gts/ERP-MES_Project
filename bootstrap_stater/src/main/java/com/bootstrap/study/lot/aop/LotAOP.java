package com.bootstrap.study.lot.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.bootstrap.study.lot.dto.LotDTO;

import lombok.extern.log4j.Log4j2;

@Aspect
@Component
@Log4j2
public class LotAOP {

//private CommonCodeRepository commonCodeRepository;

	@Before("execution(* com.bootstrap.study..service.*Service.*(com.bootstrap.study.lot.dto.LotDTO))") // 이부분 고민해야됨 어디서
																										// 이걸 넣어줘야 할지
	public void aopTest(JoinPoint joinPoint) {
		log.info("★★★★★★★★★★★★★★★ 메서드 정보 : " + joinPoint.getSignature().toShortString());
		log.info("★★★★★★★★★★★★★★★ 파라미터 정보 : " + Arrays.toString(joinPoint.getArgs()));

		for (Object obj : joinPoint.getArgs()) {
			if (obj instanceof LotDTO lotDTO) {
				log.info("★★★★★★★★★★★★★★★ lotdto 정보 : " + lotDTO.getTargetId());
				log.info("★★★★★★★★★★★★★★★ lotdto 정보 : " + lotDTO.getTableName());
				LOT생성메서드(lotDTO);
			}
		}
	}

	
	public void LOT생성메서드(LotDTO lotDTO) {
		log.info("★★★★★★★★★★★★★★★ ItemDTO 정보 : " + lotDTO);
	  
		String tableName = "";
	  
	}
	 
}
