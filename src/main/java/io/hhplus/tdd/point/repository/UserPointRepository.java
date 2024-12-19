package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPointRepository {
    private final UserPointTable userPointTable;

    public UserPoint findById (long id) { return userPointTable.selectById(id); }

    public UserPoint createUserPoint (long id, long amount){ return userPointTable.insertOrUpdate(id, amount); }
}
