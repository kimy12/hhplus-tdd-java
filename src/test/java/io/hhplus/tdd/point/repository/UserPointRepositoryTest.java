package io.hhplus.tdd.point.repository;



import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserPointRepositoryTest  {

    @Autowired
    private UserPointRepository userPointRepository;

    @DisplayName("특정 유저의 아이디로 포인트를 조회한다.")
    @Test
    void findUserPointById (){
        // given
        UserPoint userPoint1 = userPointRepository.createUserPoint(1, 50);
        UserPoint userPoint2 = userPointRepository.createUserPoint(2, 100);
        UserPoint userPoint3 = userPointRepository.createUserPoint(3, 40);

        // where
        UserPoint userPoint = userPointRepository.findById(3);

        // then
        assertThat(userPoint.id()).isEqualTo(3);
        assertThat(userPoint.point()).isEqualTo(40);
    }

    @DisplayName("특정 유저의 아이디로 포인트를 충전한다.")
    @Test
    void createUserPointById (){
        // given // where
        UserPoint userPoint = userPointRepository.createUserPoint(1, 50);

        // then
        assertThat(userPoint.id()).isEqualTo(1);
        assertThat(userPoint.point()).isEqualTo(50);
    }

}