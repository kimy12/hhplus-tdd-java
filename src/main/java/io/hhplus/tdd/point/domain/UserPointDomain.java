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

    public boolean isOverMax (long mount){
        long maxPoint = 10000000;
        long currentPoints = this.point;
        currentPoints += mount;
        return currentPoints > maxPoint;
    }

    public long addPoints(long mount) {
        if(isOverMax(mount)){
            throw new IllegalArgumentException("최대 포인트를 넘었습니다.");
        }
        this.updateMillis = System.currentTimeMillis();
        return this.point += mount;
    }
}
