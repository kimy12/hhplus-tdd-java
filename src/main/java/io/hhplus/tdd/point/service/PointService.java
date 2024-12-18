package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.controller.dto.RequestDto;
import io.hhplus.tdd.point.domain.PointHistoryDomain;
import io.hhplus.tdd.point.domain.UserPointDomain;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.TransactionType.USE;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointRepository userPointRepository;

    private final PointHistoryRepository pointHistoryRepository;

    private final ConcurrentHashMap<Long, ReentrantLock> locks = new ConcurrentHashMap<>();


    public UserPointDomain findUserPointById (long id){
        return userPointRepository.findById(id).to();
    }

    public List<PointHistoryDomain> findPointHistoryById (long id){
        List<PointHistory> allByUserId = pointHistoryRepository.getAllByUserId(id);
        return allByUserId.stream()
                .map(PointHistory::to)
                .collect(Collectors.toList());
    }

    public UserPointDomain createUserPoints (long id, RequestDto requestDto) {

        ReentrantLock lock = locks.computeIfAbsent(id, k -> new ReentrantLock(true));
        lock.lock();
        try {
            UserPointDomain userPointInfo = userPointRepository.findById(id).to();
            long addPoints = userPointInfo.addPoints(requestDto.getAmount());
            UserPointDomain userPointDomain = userPointRepository.createUserPoint(id, addPoints).to();
            pointHistoryRepository.createPointHistory(id, requestDto.getAmount(), CHARGE, userPointDomain.getUpdateMillis());
            return userPointDomain;
        } finally {
            lock.unlock();
        }
    }

    public UserPointDomain useUserPoints (long id, RequestDto requestDto){

        ReentrantLock lock = locks.computeIfAbsent(id, k -> new ReentrantLock(true));
        lock.lock();
        try {
            UserPointDomain userPointInfo = userPointRepository.findById(id).to();
            long deductPoints = userPointInfo.deductPoint(requestDto.getAmount());
            UserPointDomain userPointDomain = userPointRepository.createUserPoint(id, deductPoints).to();
            pointHistoryRepository.createPointHistory(id, requestDto.getAmount(), USE, userPointDomain.getUpdateMillis());
            return userPointDomain;
        } finally {
            lock.unlock();
        }
    }



}
