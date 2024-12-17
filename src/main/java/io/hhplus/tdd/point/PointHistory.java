package io.hhplus.tdd.point;

import io.hhplus.tdd.point.domain.PointHistoryDomain;
import io.hhplus.tdd.point.domain.TransactionType;
import lombok.Builder;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {

    public PointHistoryDomain to (){
        return PointHistoryDomain.builder()
                .id(id)
                .userId(userId)
                .amount(amount)
                .type(type)
                .updateMillis(updateMillis)
                .build();
    }
}
