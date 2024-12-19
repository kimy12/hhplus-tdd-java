package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.controller.dto.RequestDto;
import io.hhplus.tdd.point.domain.PointHistoryDomain;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPointDomain;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

/**
 * 1. repository를 주입 받아 통합테스트를 작성하였습니다.
 * 2. 읽고 쓰는 로직 등이 함께 포함 되어 있는 로직의 경우, 동시성을 고려하여 테스트 코드를 작성하였습니다.
 */
@SpringBootTest
class PointServiceTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @DisplayName("포인트를 충전한다. : 동시성 테스트")
    @Test
    void concurrentAddPoint () throws InterruptedException {
        // Given
        long userId = 1L;
        RequestDto requestDto = RequestDto.builder()
                .amount(50)
                .build(); // 요청 포인트: 50
        int threadCount = 100; // 100개의 스레드가 동시에 요청
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.createUserPoints(userId, requestDto);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // Then
        UserPointDomain result = userPointRepository.findById(userId).to();
        long expectedPoints = 5000; // 50포인트 * 100 스레드
        assertThat(result.getPoint()).isEqualTo(expectedPoints);
    }

    @DisplayName("포인트를 사용한다. : 동시성 테스트")
    @Test
    void concurrentUsePoint () throws InterruptedException {
        // Given
        long userId = 2L;
        RequestDto requestDto = RequestDto.builder()
                .amount(2)
                .build(); // 요청 포인트: 50

        UserPoint currentUserPoint = userPointRepository.createUserPoint(userId, 1000);

        int threadCount = 100; // 100개의 스레드가 동시에 요청
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.useUserPoints(userId, requestDto);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // Then
        UserPointDomain result = userPointRepository.findById(userId).to();
        long expectedPoints = 800;
        assertThat(result.getPoint()).isEqualTo(expectedPoints);
    }

    @DisplayName("특정 유저의 포인트를 조회한다.")
    @Test
    void findUserPointById () {
        // given
        RequestDto userPointInfoDto = RequestDto.builder()
                .amount(50)
                .build();

        UserPointDomain result = pointService.createUserPoints(2, userPointInfoDto);

        // when
        UserPoint userPoint = userPointRepository.findById(2);

        // then
        assertThat(userPoint.id()).isEqualTo(2);
        assertThat(userPoint.point()).isEqualTo(50);
    }

    @DisplayName("특정 유저의 아이디로 포인트의 히스토리를 조회한다.")
    @Test
    void findUserHistoryPointById (){
        // given
        PointHistory pointHistory1 = pointHistoryRepository.createPointHistory(1, 50, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory pointHistory2 = pointHistoryRepository.createPointHistory(1, 30, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory pointHistory3 = pointHistoryRepository.createPointHistory(1, 10, TransactionType.CHARGE, System.currentTimeMillis());

        // where
        List<PointHistoryDomain> pointHistoryById = pointService.findPointHistoryById(1);

        // then
        assertThat(pointHistoryById).hasSize(3)
                .extracting("userId", "amount","type")
                .containsExactlyInAnyOrder(
                        tuple(1L, 50L, TransactionType.CHARGE),
                        tuple(1L, 30L, TransactionType.CHARGE),
                        tuple(1L, 10L, TransactionType.CHARGE)
                );
    }

    @DisplayName("포인트를 충전하면 히스토리 테이블에 정보를 저장한다.")
    @Test
    void getAllHistoryInfoAfterCharge () {
        // given
        RequestDto userPointInfoDto = RequestDto.builder()
                .amount(50)
                .build();

        UserPointDomain result = pointService.createUserPoints(5, userPointInfoDto);

        // when
        List<PointHistory> allHistoryById = pointHistoryRepository.getAllByUserId(5);

        // then
        assertThat(allHistoryById).hasSize(1)
                .extracting("userId","amount","type")
                .containsExactlyInAnyOrder(
                        tuple(5L,50L,CHARGE)
                );
    }

    @DisplayName("포인트를 사용하면 히스토리 테이블에 정보를 저장한다.")
    @Test
    void getAllHistoryInfoAfterUse () {
        // given
        UserPoint userPointInfo = userPointRepository.createUserPoint(4, 100);
        RequestDto userPointInfoDto = RequestDto.builder()
                .amount(40)
                .build();
        UserPointDomain result = pointService.useUserPoints(4, userPointInfoDto);

        // when
        List<PointHistory> allHistoryById = pointHistoryRepository.getAllByUserId(4);

        // then
        assertThat(allHistoryById).hasSize(1)
                .extracting("userId","amount","type")
                .containsExactlyInAnyOrder(
                        tuple(4L,40L,USE)
                );
    }


}