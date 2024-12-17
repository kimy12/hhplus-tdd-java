package io.hhplus.tdd.point.controller;


import io.hhplus.tdd.point.domain.PointHistoryDomain;
import io.hhplus.tdd.point.domain.UserPointDomain;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
@ExtendWith(MockitoExtension.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @DisplayName("특정 유저의 포인트를 조회한다.")
    @Test
    void getUserPointById () throws Exception{

        // given
        long nowTimeMillis = System.currentTimeMillis();
        given(pointService.findUserPointById(1))
                .willReturn(UserPointDomain.builder()
                        .id(1)
                        .point(50)
                        .updateMillis(nowTimeMillis)
                        .build());

        // when // then
        mockMvc.perform(
                        get("/point/{id}", 1)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.point").value(50))
                .andExpect(jsonPath("$.updateMillis").value(nowTimeMillis));
    }
}