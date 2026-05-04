package com.renewable.ai.service;

import com.renewable.ai.entity.Order;
import com.renewable.ai.entity.OrderLog;
import com.renewable.ai.repository.OrderLogRepository;
import com.renewable.ai.repository.OrderRepository;
import com.renewable.ai.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderLogRepository orderLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void testUpdateStatus_ShouldCreateLog() {
        // Arrange
        Long orderId = 1L;
        Order mockOrder = new Order();
        mockOrder.setId(orderId);
        mockOrder.setStatus(20);
        mockOrder.setDriverId(100L);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // Act
        orderService.updateStatus(orderId, 30);

        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderLogRepository, times(1)).save(any(OrderLog.class));
    }

    @Test
    public void testCreateSelfOrder_Success() {
        Order order = new Order();
        order.setPickupAddress("Address A");
        
        when(orderRepository.existsByDriverIdAndPickupAddressAndCreatedAtAfter(any(), any(), any())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);
        
        Order result = orderService.createSelfOrder(order, 2L);
        
        org.junit.jupiter.api.Assertions.assertEquals(2L, result.getDriverId());
        org.junit.jupiter.api.Assertions.assertEquals(2L, result.getOwnerId());
        org.junit.jupiter.api.Assertions.assertEquals("SELF_CREATE", result.getSourceType());
        org.junit.jupiter.api.Assertions.assertEquals(20, result.getStatus());
        verify(orderLogRepository, times(1)).save(any());
    }

    @Test
    public void testCreateSelfOrder_Duplicate() {
        Order order = new Order();
        order.setPickupAddress("Address A");
        
        when(orderRepository.existsByDriverIdAndPickupAddressAndCreatedAtAfter(any(), any(), any())).thenReturn(true);
        
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            orderService.createSelfOrder(order, 2L);
        });
        
        org.junit.jupiter.api.Assertions.assertTrue(exception.getMessage().contains("重复提交"));
    }
}
