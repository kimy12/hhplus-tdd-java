package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.controller.dto.RequestDto;
import io.hhplus.tdd.point.domain.PointHistoryDomain;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPointDomain;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.assertj.core.groups.Tuple;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PointServiceTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;


    @DisplayName("포인트를 충전한다.")
    @Test
    void addPoint () {
        // given
        UserPoint userPointInfo = userPointRepository.createUserPoint(1, 50);
        RequestDto userPointInfoDto = RequestDto.builder()
                .amount(50)
                .build();

        // when
        UserPointDomain result = pointService.createUserPoints(1, userPointInfoDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getPoint()).isEqualTo(100);
    }

    @DisplayName("포인트를 충전하면 히스토리 테이블에 정보를 저장한다.")
    @Test
    void getAllHistoryInfoAfterCharge () {
        // given
        UserPoint userPointInfo = userPointRepository.createUserPoint(2, 50);
        RequestDto userPointInfoDto = RequestDto.builder()
                .amount(50)
                .build();

        UserPointDomain result = pointService.createUserPoints(2, userPointInfoDto);

        // when
        List<PointHistory> allHistoryById = pointHistoryRepository.getAllByUserId(2);

        // then
        assertThat(allHistoryById).hasSize(1)
                .extracting("userId","amount","type")
                .containsExactlyInAnyOrder(
                        tuple(2L,50L,CHARGE)
                );
    }

}