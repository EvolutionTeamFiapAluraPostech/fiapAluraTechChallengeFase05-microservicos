package br.com.fiap.payment.infrastructure.repository;

import br.com.fiap.payment.domain.entity.Payment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

}
