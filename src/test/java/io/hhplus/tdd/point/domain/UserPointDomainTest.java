package io.hhplus.tdd.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 1. 서비스 로직의 복잡도가 높아질 것같아, DDD 로 개발하여 domain 객체에 연산 로직을 포함하여 해당 부분에 대해 단위테스트를 진행했습니다.
 * 2. 최대 포인트 정책 / 가지고 있는 포인트 보다 사용하려는 포인트가 클 경우 관련 예외가 발생하는지에 대해 테스트 코드를 작성했습니다.
 */
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
        UserPointDomain createUserPoint = new UserPointDomain(1, 1, nowTimeMillis);
        long mount = 10000000;

        //when //then
        assertThatThrownBy(()->createUserPoint.addPoints(mount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 포인트를 넘었습니다.");
    }

    @DisplayName("사용하려는 포인트가 존재하는 포인트보다 작다.")
    @Test
    void isAmountLessThen(){
        //given
        long nowTimeMillis = System.currentTimeMillis();
        UserPointDomain createUserPoint = new UserPointDomain(1, 100, nowTimeMillis);
        long mount = 50;

        //when
        boolean result = createUserPoint.isPointLessThan(mount);

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("사용하려는 포인트가 존재하는 포인트보다 크면, 예외가 발생한다.")
    @Test
    void usePoint(){
        //given
        long nowTimeMillis = System.currentTimeMillis();
        UserPointDomain createUserPoint = new UserPointDomain(1, 100, nowTimeMillis);
        long mount = 101;

        //when //then
        assertThatThrownBy(()->createUserPoint.deductPoint(mount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("차감할 포인트가 부족합니다.");
    }
}