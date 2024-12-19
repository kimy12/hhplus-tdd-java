package io.hhplus.tdd.point.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.point.controller.dto.RequestDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 1. Controller에는 다른 로직이 존재 하지 않고(권한 등의 내용), return 값만 존재합니다.
 * 그래서 요청 값과 return 값에 대해 집중 하여 테스트 코드를 작성 하였습니다.
 * 통합테스트를 완료한 service에 대해서는 mockBean 처리 하였습니다.
 * 2. PATCH : 사용자가 요청한 값이 음수일 경우 예외가 발생하는지 테스트 코드를 작성하였습니다.
 */
@WebMvcTest(PointController.class)
@ExtendWith(MockitoExtension.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @Autowired
    protected ObjectMapper objectMapper;

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

    @DisplayName("특정 유저의 포인트 충전/이용 내역을 조회 한다.")
    @Test
    void getUserPointHistoryId () throws Exception{

        // given
        List<PointHistoryDomain> result = List.of();
        given(pointService.findPointHistoryById(1)).willReturn(result);

        // when // then
        mockMvc.perform(
                        get("/point/{id}/histories", 1)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
        ;
    }

    @DisplayName("특정 유저의 포인트 충전/이용 내역을 조회 한다.")
    @Test
    void chargePoint () throws Exception{

        // given
        long id = 1;
        RequestDto request = RequestDto.builder()
                .amount(50)
                .build();


        // when // then
        mockMvc.perform(
                        patch("/point/{id}/charge", id)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("포인트를 충전 할때는 충전할 포인트가 양수여야 한다.")
    @Test
    void chargePointWithoutMount () throws Exception{

        // given
        long id = 1;
        RequestDto request = RequestDto.builder()
                .amount(-2)
                .build();


        // when // then
        mockMvc.perform(
                        patch("/point/{id}/charge", id)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("포인트는 양수값이 필수입니다."))
        ;
    }

    @DisplayName("특정 유저의 포인트 충전/이용 내역을 조회 한다.")
    @Test
    void usePoint () throws Exception{

        // given
        long id = 1;
        RequestDto request = RequestDto.builder()
                .amount(50)
                .build();

        // when // then
        mockMvc.perform(
                        patch("/point/{id}/use", id)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("포인트를 충전 할때는 충전할 포인트가 양수여야 한다.")
    @Test
    void usePointWithoutMount () throws Exception{

        // given
        long id = 1;
        RequestDto request = RequestDto.builder()
                .amount(-2)
                .build();


        // when // then
        mockMvc.perform(
                        patch("/point/{id}/use", id)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("포인트는 양수값이 필수입니다."));
    }

}