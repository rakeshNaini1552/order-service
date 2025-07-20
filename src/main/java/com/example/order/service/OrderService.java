package com.example.order.service;


import com.example.order.client.UserClient;
import com.example.order.dto.OrderRequest;
import com.example.order.dto.OrderResponse;
import com.example.order.dto.UserDto;
import com.example.order.model.Order;
import com.example.order.model.OrderStatus;
import com.example.order.repository.OrderRepository;
import com.example.order.config.JwtUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final UserClient userClient;

    public OrderService(OrderRepository repo, UserClient userClient){
        this.orderRepository = repo;
        this.userClient = userClient;
    }
    public OrderResponse createOrder(OrderRequest orderRequest){

        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Order order = Order.builder()
                .userId(jwtUser.userId())
                .item(orderRequest.getItem())
                .price(orderRequest.getPrice())
                .createdAt(LocalDateTime.now())
                .status(OrderStatus.CREATED)
                .build();
        return mapToResponse(orderRepository.save(order));
    }

    public OrderResponse getOrderDetailsById(Integer orderId){
       Order order = orderRepository.findById(orderId).orElseThrow();
        System.out.println("ORder is :" +order.toString());
        System.out.println("User id is : "+order.getUserId());
        UserDto userDto = userClient.getUserById(order.getId());
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .item(order.getItem())
                .price(order.getPrice())
                .createdAt(order.getCreatedAt())
                .userDto(userDto)
                .build();
    }

    public List<OrderResponse> getAllOrdersbyUserId(Integer userId){
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public OrderResponse mapToResponse(Order order){
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .item(order.getItem())
                .price(order.getPrice())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
