package br.com.fiap.order.domain.service;

import static br.com.fiap.order.domain.fields.OrderFields.*;
import static br.com.fiap.order.domain.messages.OrderMessages.*;

import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.domain.exception.NoResultException;
import br.com.fiap.order.infrastructure.repository.OrderRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

@Service
public class OrderService {

  private final OrderRepository orderRepository;

  public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public Order save(Order order) {
    return orderRepository.save(order);
  }

  public Order findByIdRequired(UUID orderId) {
    return orderRepository.findById(orderId).orElseThrow(() -> new NoResultException(
        new FieldError(this.getClass().getSimpleName(), ORDER_ID_FIELD,
            ORDER_NOT_FOUND_WITH_ID.formatted(orderId))));
  }

  public Page<Order> findOrderByCompanyIdOrCustomerId(UUID companyId, UUID customerId,
      Pageable pageable) {
    return orderRepository.findOrderByCompanyIdOrCustomerId(companyId, customerId, pageable);
  }
}
