package br.com.fiap.payment.domain.messages;

public final class PaymentMessages {

  public static final String PAYMENT_ORDER_WITHOUT_ITEMS_MESSAGE = "Order without items. Order ID: %s";
  public static final String PAYMENT_ORDER_WITH_ITEM_WITHOUT_TOTAL_AMOUNT_MESSAGE = "Order with item without total amount. Order ID: %s";
  public static final String PAYMENT_ORDER_CUSTOMER_ID_IS_DIFFERENT_OF_AUTHENTICATED_USER_MESSAGE = "Order customer is different of authenticated user. Order ID: %s. Order customer ID: %s. Authenticated user ID: %s.";
  public static final String PAYMENT_ORDER_WITH_INVALID_STATUS_MESSAGE = "Order with invalid status. Order ID: %s. Invalid status: %s. Valid status: %s";

  private PaymentMessages() {
  }
}
