package br.com.fiap.order.presentation.api;

import br.com.fiap.order.presentation.api.dto.OrderDto;
import br.com.fiap.order.presentation.api.dto.OrderFilter;
import br.com.fiap.order.presentation.api.dto.OrderInputDto;
import br.com.fiap.order.presentation.api.dto.OrderOutputDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "OrdersApi", description = "API de cadastro de pedidos")
public interface OrdersApi {

  @Operation(summary = "Cadastro de pedidos",
      description = "Endpoint para cadastrar novos pedidos.",
      tags = {"OrdersApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "successful operation",
          content = {
              @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))}),
      @ApiResponse(responseCode = "400", description = "bad request para validação do id da empresa, do id do cliente, id do produto, quantidade e preço.",
          content = {@Content(schema = @Schema(hidden = true))})})
  OrderDto postOrder(
      @Parameter(description = "DTO de entrada com atributos para se cadastrar um novo pedido. "
          + "Campos obrigatórios id da empresa, id do cliente, id do produto, quantidade e preço unitário.")
      OrderInputDto orderInputDto);

  @Operation(summary = "Recupera um pedido",
      description = "Endpoint para recuperar um pedido pelo ID cadastrado",
      tags = {"OrdersApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "successful operation", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))}),
      @ApiResponse(responseCode = "404", description = "not found para pedido não encontrado",
          content = {@Content(schema = @Schema(hidden = true))})})
  OrderDto getOrderById(
      @Parameter(description = "UUID válido de um pedido") String id);

  @Operation(summary = "Lista de pedidos paginada",
      description = "Endpoint para recuperar uma lista paginada de pedidos, filtrada por código da empresa OU código do cliente",
      tags = {"OrdersApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "successful operation", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))}),
      @ApiResponse(responseCode = "404", description = "not found para pedido não encontrado",
          content = {@Content(schema = @Schema(hidden = true))})})
  Page<OrderDto> getOrdersByCompanyIdOrCustomerId(
      @Parameter(description = "DTO com os atributos do código da empresa ou código do cliente para serem utilizados como filtro de pesquisa.") OrderFilter orderFilter,
      @Parameter(description = "Interface com atributos para paginação") Pageable pageable);

  @Operation(summary = "Atualiza um pedido",
      description = "Endpoint para atualizar dados de um pedido",
      tags = {"OrdersApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "successful operation", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))}),
      @ApiResponse(responseCode = "400", description = "bad request para UUID inválido", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "404", description = "not found para pedido não encontrado", content = {
          @Content(schema = @Schema(hidden = true))}),
  })
  OrderOutputDto putOrder(@Parameter(description = "UUID válido de um pedido") String id,
      @Parameter(description = "DTO com atributos para se atualizar um pedido.") OrderDto orderDto);

  @Operation(summary = "Confirma o pagamento do pedido",
      description = "Endpoint para confirmar o pagamento do pedido",
      tags = {"OrdersApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "successful operation", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "400", description = "bad request para UUID inválido ou pagamento já realizado", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "404", description = "not found para pedido não encontrado", content = {
          @Content(schema = @Schema(hidden = true))}),
  })
  void patchOrderPaymentConfirmation(
      @Parameter(description = "UUID válido de um pedido") String id);

  @Operation(summary = "Atualiza o status do pedido para aguardando entrega",
      description = "Endpoint para indicar que o pedido está aguardando entrega",
      tags = {"OrdersApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "successful operation", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "400", description = "bad request para UUID inválido ou pedido já entregue", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "404", description = "not found para pedido não encontrado", content = {
          @Content(schema = @Schema(hidden = true))}),
  })
  void putOrderAwaitingDelivery(
      @Parameter(description = "UUID válido de um pedido") String id);

  @Operation(summary = "Confirma a entrega do pedido",
      description = "Endpoint para confirmar a entrega do pedido",
      tags = {"OrdersApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "successful operation", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "400", description = "bad request para UUID inválido ou entrega já realizado", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "404", description = "not found para pedido não encontrado", content = {
          @Content(schema = @Schema(hidden = true))}),
  })
  void putOrderDeliveryConfirmation(
      @Parameter(description = "UUID válido de um pedido") String id);

  @Operation(summary = "Exclui um pedido",
      description = "Endpoint para excluir um pedido. A exclusão é feita por soft delete",
      tags = {"OrdersApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "successful operation", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "400", description = "bad request para UUID inválido", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "404", description = "not found para pedido não encontrado", content = {
          @Content(schema = @Schema(hidden = true))})})
  void deleteOrder(@Parameter(description = "UUID válido de um pedido") String id);
}
