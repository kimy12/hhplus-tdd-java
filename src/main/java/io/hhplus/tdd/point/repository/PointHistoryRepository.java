package io.hhplus.tdd.point.repository;


import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.entity.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepository {
    public final PointHistoryTable pointHistoryTable;

    public PointHistory createPointHistory(long userId, long amount, TransactionType type, long updateMillis){ return pointHistoryTable.insert(userId, amount, type, updateMillis);}

    public List<PointHistory> getAllByUserId(long userId) { return pointHistoryTable.selectAllByUserId(userId); }
}
