package com.tj.GFV_Map.controller;

import com.tj.GFV_Map.dto.response.BusinessStatusResponseDto;
import com.tj.GFV_Map.service.BusinessVerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Map;

@RestController
@RequestMapping("/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessVerifyService businessVerifyService;

    // 사업자번호 검증 (유효한 계속사업자인가?)
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody Map<String, String> body) throws URISyntaxException {
        String bno = body.get("bno");
        boolean valid = businessVerifyService.isValidBusiness(bno);

        return ResponseEntity.ok(Map.of(
                "bno", bno,
                "valid", valid,
                "message", valid ? "유효한 사업자입니다." : "유효하지 않거나 휴/폐업 사업자입니다."
        ));
    }

    // 상세 조회
    @PostMapping("/status")
    public ResponseEntity<BusinessStatusResponseDto.BusinessData> status(
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                businessVerifyService.getBusinessStatus(body.get("bno")));
    }
}