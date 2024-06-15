package br.com.fiap.product.application.usecase;

import br.com.fiap.product.domain.entity.Product;
import br.com.fiap.product.domain.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GetProductBySkuOrDescriptionUseCase {

  private final ProductService productService;

  public GetProductBySkuOrDescriptionUseCase(ProductService productService) {
    this.productService = productService;
  }

  public Page<Product> execute(String sku, String description, Pageable pageable) {
    return productService.queryProductsBySkuLikeIgnoreCaseOrDescription(sku, description, pageable);
  }
}
