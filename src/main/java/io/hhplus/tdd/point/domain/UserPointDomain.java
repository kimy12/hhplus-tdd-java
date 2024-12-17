package io.hhplus.tdd.point.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserPointDomain {
    private long id;
    private long point;
    private long updateMillis;

    @Builder
    public UserPointDomain(long id, long point, long updateMillis) {
        this.id = id;
        this.point = point;
        this.updateMillis = updateMillis;
    }
}
