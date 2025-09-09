package com.erp_mes.erp.config.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HolidayAPIResponse {
    private Response response;

    @Data
    public static class Response {
        private Header header;
        private Body body;
    }

    @Data
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    public static class Body {
        private Object items;
    }

    @Data
    public static class Items {
        @JsonProperty("item")
        private List<HolidayDTO> holidayList;
    }
}

