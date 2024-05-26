package br.com.users.user.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "UserPaymentMethodDto", description = "DTO de entrada para represetação de um método de pagamento")
public record UserPaymentMethodDto(
    @Schema(example = "PIX, CREDIT_CARD, DEBIT_CARD", description = "Descrição do meio de pagamento")
    String paymentMethod) {
}
