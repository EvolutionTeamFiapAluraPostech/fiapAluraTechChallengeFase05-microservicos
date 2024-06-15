package br.com.fiap.order.infrastructure.repository;

import br.com.fiap.order.domain.entity.Order;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderQueryRepository {

  @Query(value = """
      SELECT o
      FROM Order o
      LEFT JOIN FETCH o.orderItems
      WHERE o.companyId = :companyId
      OR o.customerId = :customerId
      """)
  Page<Order> findOrderByCompanyIdOrCustomerId(@Param("companyId") UUID companyId,
      @Param("customerId") UUID customerId, Pageable pageable);
}
