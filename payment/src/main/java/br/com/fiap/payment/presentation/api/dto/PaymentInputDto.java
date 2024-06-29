package br.com.fiap.payment.presentation.api.dto;

import br.com.fiap.payment.domain.enums.PaymentType;
import br.com.fiap.payment.domain.validator.ValueOfEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;

@Tag(name = "PaymentInputDto", description = "DTO de entrada para representação de um pagamento.")
public record PaymentInputDto(
    @Schema(example = "feea1d11-11b9-4e34-9848-e1174bb857e3", description = "Valid UUID.")
    @NotBlank
    String orderId,
    @Schema(example = "DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO, PIX", description = "Tipo de pagamento.")
    @NotBlank
    @ValueOfEnum(enumClass = PaymentType.class)
    String paymentType) {
}
