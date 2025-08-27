package com.bootstrap.study.commonCode.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HolidayService {

    @Value("${service.key}") // application.properties 파일에 저장된 API 키를 주입받습니다.
    private String serviceKey;

    private static final String API_URL = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public HolidayService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
    }

    public List<HolidayDTO> getHolidays(int year, int month) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("serviceKey", serviceKey)
                .queryParam("solYear", year)
                .queryParam("solMonth", String.format("%02d", month)) // 01, 02 형식으로 변환
                .queryParam("_type", "json")
                .build()
                .toUriString();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String jsonString = response.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode itemNode = rootNode.path("response").path("body").path("items").path("item");

            List<HolidayDTO> holidays = new ArrayList<>();
            if (itemNode.isArray()) {
                holidays.addAll(objectMapper.convertValue(itemNode, new TypeReference<List<HolidayDTO>>() {}));
            } else if (!itemNode.isMissingNode()) { // 공휴일이 하나일 때
                holidays.add(objectMapper.convertValue(itemNode, HolidayDTO.class));
            }

            // isHoliday가 "Y"인 경우만 필터링하여 반환
            return holidays.stream()
                    .filter(h -> "Y".equals(h.getIsHoliday()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}