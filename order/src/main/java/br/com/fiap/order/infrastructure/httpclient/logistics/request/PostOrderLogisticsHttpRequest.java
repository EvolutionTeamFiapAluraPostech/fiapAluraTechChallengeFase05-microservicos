package br.com.fiap.order.infrastructure.httpclient.logistics.request;

import br.com.fiap.order.OrderApplication;
import br.com.fiap.order.domain.entity.Order;
import br.com.fiap.order.infrastructure.httpclient.logistics.LogisticsClient;
import br.com.fiap.order.infrastructure.httpclient.logistics.dto.LogisticOrderDto;
import br.com.fiap.order.infrastructure.httpclient.logistics.dto.LogisticOrderInputDto;
import org.springframework.stereotype.Service;

@Service
public class PostOrderLogisticsHttpRequest {

  private final LogisticsClient logisticsClient;

  public PostOrderLogisticsHttpRequest(LogisticsClient logisticsClient) {
    this.logisticsClient = logisticsClient;
  }

  public LogisticOrderDto request(Order order) {
    try {
      var logisticOrderInputDto = LogisticOrderInputDto.from(order);
      var responseEntityLogisticOrderDto = logisticsClient.postOrderLogistics(
          logisticOrderInputDto);
      return responseEntityLogisticOrderDto.getBody();
    } catch (Exception e) {
      OrderApplication.logger.error(e.getMessage());
    }
    return null;
  }
}
