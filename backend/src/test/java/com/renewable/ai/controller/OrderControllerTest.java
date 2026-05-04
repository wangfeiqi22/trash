package com.renewable.ai.controller;

import com.renewable.ai.entity.OrderLog;
import com.renewable.ai.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private com.renewable.ai.repository.OrderPhotoRepository orderPhotoRepository;

    @Test
    public void getOrderLogs_ShouldReturnLogList() throws Exception {
        // Arrange
        Long orderId = 1L;
        OrderLog log1 = new OrderLog();
        log1.setId(101L);
        log1.setOrderId(orderId);
        log1.setNewStatus(20);
        log1.setActionName("Grab");
        log1.setRemark("Driver grabbed order");
        log1.setCreatedAt(LocalDateTime.now());

        List<OrderLog> logs = Arrays.asList(log1);

        given(orderService.getOrderLogs(orderId)).willReturn(logs);

        // Act & Assert
        mockMvc.perform(get("/api/orders/{orderId}/logs", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actionName").value("Grab"))
                .andExpect(jsonPath("$[0].newStatus").value(20));
    }
    
    @Test
    public void getOrderLogs_ShouldReturnEmptyList_WhenNoLogs() throws Exception {
        // Arrange
        Long orderId = 99L;
        given(orderService.getOrderLogs(orderId)).willReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/orders/{orderId}/logs", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
