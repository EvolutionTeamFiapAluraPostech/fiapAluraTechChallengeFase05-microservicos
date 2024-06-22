package br.com.fiap.product.shared.api;

import br.com.fiap.product.domain.entity.Product;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class PageUtil {

  public static final int PAGE_NUMBER = 0;
  public static final int PAGE_SIZE = 1;
  public static final int TOTAL_ITEMS = 2;

  public static PageImpl<Product> generatePageOfProduct(List<Product> products) {
    var pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    return new PageImpl<>(products, pageable, TOTAL_ITEMS);
  }
}
