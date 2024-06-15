package br.com.fiap.order.infrastructure.repository;

import br.com.fiap.order.domain.entity.Order;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends OrderQueryRepository, JpaRepository<Order, UUID> {

}
