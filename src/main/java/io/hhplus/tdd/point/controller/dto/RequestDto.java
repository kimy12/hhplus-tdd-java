package io.hhplus.tdd.point.controller.dto;

import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPointDomain;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestDto {

    private long userId;

    @Positive(message = "포인트는 양수값이 필수입니다.")
    private long amount;
    private TransactionType type;
    private long updateMillis;

    @Builder
    public RequestDto(long userId, long amount, TransactionType type, long updateMillis) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.updateMillis = updateMillis;
    }
}
