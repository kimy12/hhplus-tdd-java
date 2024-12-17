package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@SpringBootTest
class PointHistoryRepositoryTest {

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @DisplayName("특정 유저의 아이디로 포인트의 히스토리를 조회한다.")
    @Test
    void findUserPointById (){
        // given
        PointHistory pointHistory1 = pointHistoryRepository.createPointHistory(1, 50, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory pointHistory2 = pointHistoryRepository.createPointHistory(1, 30, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory pointHistory3 = pointHistoryRepository.createPointHistory(1, 10, TransactionType.CHARGE, System.currentTimeMillis());

        // where
        List<PointHistory> allByUserId = pointHistoryRepository.getAllByUserId(1);

        // then
        assertThat(allByUserId).hasSize(3)
                .extracting("userId", "amount","type")
                .containsExactlyInAnyOrder(
                        tuple(1L, 50L, TransactionType.CHARGE),
                        tuple(1L, 30L, TransactionType.CHARGE),
                        tuple(1L, 10L, TransactionType.CHARGE)
                );
    }

    @DisplayName("특정 유저의 아이디로 포인트의 히스토리가 저장된다.")
    @Test
    void createPointHistory (){
        // given
        long userId = 1;
        long amount = 50;
        TransactionType type = TransactionType.CHARGE;
        long currentTimeMillis = System.currentTimeMillis();

        // where
        PointHistory pointHistory = pointHistoryRepository.createPointHistory(userId, amount, type, currentTimeMillis);

        // then
        assertThat(pointHistory.userId()).isEqualTo(userId);
        assertThat(pointHistory.amount()).isEqualTo(amount);
        assertThat(pointHistory.type()).isNotNull();
        assertThat(pointHistory.updateMillis()).isEqualTo(currentTimeMillis);
    }
}