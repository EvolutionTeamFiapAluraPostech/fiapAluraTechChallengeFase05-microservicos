package br.com.fiap.payment.presentation.api.dto;

import br.com.fiap.payment.domain.entity.Payment;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Tag(name = "PaymentDto", description = "DTO de saída para representação de um pagamento.")
public record PaymentDto(
    @Schema(example = "feea1d11-11b9-4e34-9848-e1174bb857e3", description = "ID do pagamento.")
    String id,
    @Schema(example = "feea1d11-11b9-4e34-9848-e1174bb857e3", description = "ID do pedido.")
    String orderId,
    @Schema(example = "feea1d11-11b9-4e34-9848-e1174bb857e3", description = "ID da empresa.")
    String companyId,
    @Schema(example = "Matrix Company", description = "Nome da empresa.")
    String companyName,
    @Schema(example = "feea1d11-11b9-4e34-9848-e1174bb857e3", description = "ID do cliente.")
    String customerId,
    @Schema(example = "Matrix Company", description = "Nome do cliente.")
    String customerName,
    @Schema(example = "DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO, PIX", description = "Tipo de pagamento.")
    String PaymentType,
    @Schema(example = "PENDENTE, REALIZADO", description = "Status do pagamento.")
    String paymentStatus,
    @Schema(description = "Data do pagamento.")
    LocalDateTime paymentDate,
    @Schema(description = "Valor do pagamento.")
    BigDecimal paymentTotalAmount) {

  public static PaymentDto from(Payment payment) {
    return new PaymentDto(payment.getId().toString(), payment.getOrderId(), payment.getCompanyId(),
        payment.getCompanyName(), payment.getCustomerId(), payment.getCustomerName(),
        payment.getPaymentType().name(), payment.getPaymentStatus().name(),
        payment.getPaymentDate(), payment.getPaymentTotalAmount());
  }
}
