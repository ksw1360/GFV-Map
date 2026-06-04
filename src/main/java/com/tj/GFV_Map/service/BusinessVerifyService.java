package com.tj.GFV_Map.service;

import com.tj.GFV_Map.dto.response.BusinessStatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BusinessVerifyService {

    private final RestTemplate restTemplate;   // SecurityConfig에 이미 빈 있음

    @Value("${nts.service-key}")
    private String serviceKey;

    private static final String STATUS_URL =
            "https://api.odcloud.kr/api/nts-businessman/v1/status";

    /**
     * 사업자번호 상태조회 → 계속사업자(01)면 true
     */
    public boolean isValidBusiness(String bno) {
        // 하이픈 제거 (123-45-67890 → 1234567890)
        String cleanBno = bno.replaceAll("-", "").trim();

        // URL 조립 (이 인증키는 16진수라 디코딩 불필요)
        String url = STATUS_URL + "?serviceKey=" + serviceKey;
        URI uri = URI.create(url);   // URI 객체로 만들어 RestTemplate 이중 인코딩 방지

        // 요청 바디
        Map<String, Object> body = Map.of("b_no", List.of(cleanBno));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            BusinessStatusResponseDto response = restTemplate.postForObject(
                    uri, request, BusinessStatusResponseDto.class);

            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                return false;
            }

            String sttCd = response.getData().get(0).getBSttCd();
            // 01 = 계속사업자 → 유효
            return "01".equals(sttCd);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("사업자번호 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 상세 정보까지 반환하는 버전 (필요시)
     */
    public BusinessStatusResponseDto.BusinessData getBusinessStatus(String bno) {
        String cleanBno = bno.replaceAll("-", "").trim();
        String decodedKey = URLDecoder.decode(serviceKey, StandardCharsets.UTF_8);
        String url = STATUS_URL + "?serviceKey=" + decodedKey;

        Map<String, Object> body = Map.of("b_no", List.of(cleanBno));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        BusinessStatusResponseDto response = restTemplate.postForObject(
                url, request, BusinessStatusResponseDto.class);

        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            throw new IllegalArgumentException("조회 결과가 없습니다.");
        }
        return response.getData().get(0);
    }
}