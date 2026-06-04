package com.tj.GFV_Map.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BusinessStatusResponseDto {

    @JsonProperty("status_code")
    private String statusCode;

    @JsonProperty("request_cnt")
    private Integer requestCnt;

    private List<BusinessData> data;

    @Getter
    @NoArgsConstructor
    public static class BusinessData {
        @JsonProperty("b_no")
        private String bNo;           // 사업자번호

        @JsonProperty("b_stt")
        private String bStt;          // 상태 (계속사업자/휴업자/폐업자)

        @JsonProperty("b_stt_cd")
        private String bSttCd;        // 상태코드 (01/02/03)

        @JsonProperty("tax_type")
        private String taxType;       // 과세유형 (or 미등록 메시지)

        @JsonProperty("end_dt")
        private String endDt;         // 폐업일자
    }
}