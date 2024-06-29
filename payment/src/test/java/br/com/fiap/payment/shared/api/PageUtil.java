package br.com.fiap.payment.shared.api;

import br.com.fiap.payment.domain.entity.Payment;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class PageUtil {

  public static final int PAGE_NUMBER = 0;
  public static final int PAGE_SIZE = 1;

  public static PageImpl<Payment> generatePageOfOrder(Payment payment) {
    var pageNumber = 0;
    var pageSize = 2;
    var totalItems = 3;
    var pageable = PageRequest.of(pageNumber, pageSize);
    return new PageImpl<>(List.of(payment), pageable, totalItems);
  }
}
