package br.com.fiap.order.presentation.api.dto;

import br.com.fiap.order.domain.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;

@Tag(name = "OrderDto", description = "DTO de saída de pedido.")
public record OrderDto(
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do pedido.")
    String id,
    @Schema(example = "ENTREGUE", description = "Status do pedido.")
    String orderStatus,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único da empresa.")
    @NotBlank
    String companyId,
    @Schema(example = "bae0fc3d-be9d-472a-bf03-7a7ee2411ce1", description = "Identificador único do cliente.")
    @NotBlank
    String customerId,
    @Schema(example = "Thomas Anderson", description = "Nome do cliente.", minLength = 3, maxLength = 500)
    @NotBlank
    @Size(min = 3, max = 500, message = "Size must be between 3 e 500 characters.")
    String customerName,
    @Schema(example = "thomas.anderson@itcompany.com", description = "Endereço de e-mail do cliente.", minLength = 3, maxLength = 500)
    @NotBlank
    @Size(min = 3, max = 500, message = "Size must be between 3 e 500 characters.")
    @Email
    String customerEmail,
    @Schema(example = "11955975094", description = "Número do documento do cliente.", minLength = 11, maxLength = 14)
    @NotBlank
    @Size(min = 11, max = 14, message = "Size must be between 11 e 14 characters.")
    String customerDocNumber,
    @Schema(example = "CPF ou CNPJ", description = "Tipo do número do documento do cliente.")
    String customerDocNumberType,
    @Schema(example = "Av. Lins de Vasconcelos", description = "Rua do endereço do cliente.", minLength = 3, maxLength = 255)
    @NotBlank
    @Size(min = 3, max = 255, message = "Size must be between 3 e 255 characters.")
    String customerStreet,
    @Schema(example = "1222", description = "Número do endereço do cliente.", minLength = 3, maxLength = 255)
    @NotBlank
    @Size(min = 3, max = 100, message = "Size must be between 3 e 100 characters.")
    String customerNumber,
    @Schema(example = "Cambuci", description = "Bairro do endereço do cliente.", minLength = 3, maxLength = 100)
    @NotBlank
    @Size(min = 3, max = 100, message = "Size must be between 3 e 100 characters.")
    String customerNeighborhood,
    @Schema(example = "São Paulo", description = "Cidade do endereço do cliente.", minLength = 3, maxLength = 100)
    @NotBlank
    @Size(min = 3, max = 100, message = "Size must be between 3 e 100 characters.")
    String customerCity,
    @Schema(example = "SP", description = "Sigla do Estado do endereço do cliente.", minLength = 2, maxLength = 2)
    @NotBlank
    @Size(min = 2, max = 2, message = "Size must be 2 characters.")
    String customerState,
    @Schema(example = "Brasil", description = "País do endereço do cliente.", minLength = 3, maxLength = 100)
    @NotBlank
    @Size(min = 3, max = 100, message = "Size must be between 3 e 100 characters.")
    String customerCountry,
    @Schema(example = "01538001", description = "Código postal do endereço do cliente.", minLength = 8, maxLength = 8)
    @NotBlank
    @Size(min = 8, max = 8, message = "Size must be between 8 characters.")
    @Digits(integer = 8, fraction = 0, message = "Postal code must be a valid number with a maximum of 8 integral digits")
    String customerPostalCode,
    @Schema(example = "-23.56391", description = "Latitude.", minLength = -90, maxLength = 90)
    BigDecimal customerLatitude,
    @Schema(example = "-46.65239", description = "Longitude.", minLength = -90, maxLength = 90)
    BigDecimal customerLongitude,
    @Schema(example = "{[]}", description = "Lista de produtos do pedido.")
    @NotEmpty
    List<@Valid OrderItemDto> orderItems
) {

  public OrderDto(Order order) {
    this(order.getId().toString(),
        order.getOrderStatus().name(),
        order.getCompanyId().toString(),
        order.getCustomerId().toString(),
        order.getCustomerName(),
        order.getCustomerEmail(),
        order.getCustomerDocNumber(),
        order.getCustomerDocNumberType(),
        order.getCustomerStreet(),
        order.getCustomerNumber(),
        order.getCustomerNeighborhood(),
        order.getCustomerCity(),
        order.getCustomerState(),
        order.getCustomerCountry(),
        order.getCustomerPostalCode(),
        order.getCustomerLatitude(),
        order.getCustomerLongitude(),
        OrderItemDto.getOrderItemsOutputDtofrom(order.getOrderItems()));
  }

  public static OrderDto from(Order order) {
    var orderItemsDto = new ArrayList<OrderItemDto>();

    order.getOrderItems().forEach(orderItem -> {
      var orderItemOutputDto = new OrderItemDto(orderItem.getId().toString(),
          orderItem.getProductId().toString(),
          orderItem.getQuantity(),
          orderItem.getPrice(),
          orderItem.getTotalAmount());
      orderItemsDto.add(orderItemOutputDto);
    });

    return new OrderDto(order.getId() != null ? order.getId().toString() : null,
        order.getOrderStatus().name(),
        order.getCompanyId().toString(),
        order.getCustomerId().toString(),
        order.getCustomerName(),
        order.getCustomerEmail(),
        order.getCustomerDocNumber(),
        order.getCustomerDocNumberType(),
        order.getCustomerStreet(),
        order.getCustomerNumber(),
        order.getCustomerNeighborhood(),
        order.getCustomerCity(),
        order.getCustomerState(),
        order.getCustomerCountry(),
        order.getCustomerPostalCode(),
        order.getCustomerLatitude(),
        order.getCustomerLongitude(),
        orderItemsDto);
  }

  public static Page<OrderDto> toPage(Page<Order> ordersPage) {
    return ordersPage.map(OrderDto::new);
  }
}
