package br.com.fiap.order.domain.messages;

public final class OrderMessages {

  public static final String ORDER_WITH_INVALID_UUID_MESSAGE = "Order with invalid UUID. ID %s";
  public static final String ORDER_ITEM_QUANTITY_INVALID = "Order item quantity must be greater than zero. Entered value %s.";
  public static final String ORDER_ITEM_PRICE_INVALID = "Order item price must be greater than zero. Entered value %s.";
  public static final String ORDER_NOT_FOUND_WITH_ID = "Order not found. ID %s";
  public static final String ORDER_NOT_PAID_TO_DELIVER = "Order is not paid and it not be possible to deliver it. ID %s";
  public static final String ORDER_ALREADY_PAID = "Order is already paid. ID %s";
  public static final String ORDER_ALREADY_AWAITING_DELIVERY = "Order is already awaiting delivery. ID %s";
  public static final String ORDER_ALREADY_DELIVERED = "Order is already delivered. ID %s";
  public static final String ORDER_IS_NOT_VALID_FOR_UPDATE = "Order is not valid for update, it must be %s, but is %s. ID %s";


  private OrderMessages() {
  }
}
