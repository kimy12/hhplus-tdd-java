package io.hhplus.tdd.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPointDomainTest {

    @DisplayName("포인트를 충전한다.")
    @Test
    void chargePoints(){
        //given
        long nowTimeMillis = System.currentTimeMillis();
        UserPointDomain createUserPoint = new UserPointDomain(1, 100, nowTimeMillis);
        long mount = 50;

        //when
        long result = createUserPoint.addPoints(mount);

        //then
        assertThat(result).isEqualTo(150);
    }

    @DisplayName("포인트 충전 시 최대 포인트 보다 크면 예외가 발생한다.")
    @Test
    void chargeOverMaxPoints (){
        //given
        long nowTimeMillis = System.currentTimeMillis();
        UserPointDomain createUserPoint = new UserPointDomain(1, 100, nowTimeMillis);
        long mount = 10000000;

        //when //then
        assertThatThrownBy(()->createUserPoint.addPoints(mount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 포인트를 넘었습니다.");
    }
}