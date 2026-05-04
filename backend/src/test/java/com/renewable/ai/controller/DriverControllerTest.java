package com.renewable.ai.controller;

import com.renewable.ai.entity.Order;
import com.renewable.ai.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DriverController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private com.renewable.ai.interceptor.AuthenticationInterceptor authenticationInterceptor;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() throws Exception {
        when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    public void getHistoryTasks_WithDates_ReturnsList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        Page<Order> page = new PageImpl<>(Collections.singletonList(order));

        when(orderService.getDriverHistory(eq(1L), anyInt(), anyInt(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/driver/task/history")
                        .param("driverId", "1")
                        .param("startDate", "2023-01-01T00:00:00Z")
                        .param("endDate", "2023-01-31T23:59:59Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    public void getHistoryTasks_ValidDriver_ReturnsList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(60);
        Page<Order> page = new PageImpl<>(Collections.singletonList(order));

        when(orderService.getDriverHistory(eq(1L), anyInt(), anyInt(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/driver/task/history")
                        .param("driverId", "1")
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].status").value(60));
    }

    @Test
    public void getHistoryTasks_InvalidDriver_ReturnsEmpty() throws Exception {
        Page<Order> emptyPage = new PageImpl<>(Collections.emptyList());

        when(orderService.getDriverHistory(eq(999L), anyInt(), anyInt(), any(), any()))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/driver/task/history")
                        .param("driverId", "999")
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }
}
