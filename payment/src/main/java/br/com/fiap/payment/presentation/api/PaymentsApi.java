package br.com.fiap.payment.presentation.api;

import br.com.fiap.payment.presentation.api.dto.PaymentDto;
import br.com.fiap.payment.presentation.api.dto.PaymentInputDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "PaymentsApi", description = "API de pagamento de pedidos")
public interface PaymentsApi {

  @Operation(summary = "Cadastro de pagamento de pedidos",
      description = "Endpoint para cadastrar o pagamento de um pedido.",
      tags = {"PaymentsApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "successful operation",
          content = {
              @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDto.class))}),
      @ApiResponse(responseCode = "400", description = "bad request para validação do id do pedido.",
          content = {@Content(schema = @Schema(hidden = true))})})
  PaymentDto postOrderPayment(
      @Parameter(description = "DTO de entrada com atributos para se cadastrar um novo pagamento de pedido. "
          + "Campos obrigatórios id do pedido.")
      PaymentInputDto paymentInputDto);

  @Operation(summary = "Recupera uma ordem de pagamento pelo ID do pedido",
      description = "Endpoint para recuperar uma ordem de pagamento pelo ID do pedido cadastrado",
      tags = {"PaymentsApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "successful operation", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDto.class))}),
      @ApiResponse(responseCode = "404", description = "not found para pedido não encontrado",
          content = {@Content(schema = @Schema(hidden = true))})})
  PaymentDto getPaymentOrderSummarizeByOrderId(@PathVariable String id);
}
