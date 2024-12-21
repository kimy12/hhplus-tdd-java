package io.hhplus.tdd.point.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointHistoryDomain {
    private long id;
    private long userId;
    private long amount;
    private TransactionType type;
    private long updateMillis;

    @Builder
    public PointHistoryDomain(long id, long userId, long amount, TransactionType type, long updateMillis) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.updateMillis = updateMillis;
    }
}
