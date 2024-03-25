package com.fastcampus.loan.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

public class BalanceDTO implements Serializable {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    @Setter
    public static class Request{
        private Long applicationId;
        private BigDecimal entryAmount;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    @Setter
    public static class UpdateRequest{
        private Long applicationId;
        private BigDecimal beforeEntryAmount;
        private BigDecimal afterEntryAmount;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    @Setter
    public static class Response{
        private Long balanceId;
        private Long applicationId;
        private BigDecimal balance;
    }
}
