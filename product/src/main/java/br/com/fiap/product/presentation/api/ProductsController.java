package br.com.fiap.product.presentation.api;

import br.com.fiap.product.application.usecase.CreateProductUseCase;
import br.com.fiap.product.application.usecase.DeleteProductUseCase;
import br.com.fiap.product.application.usecase.GetAllProductsUseCase;
import br.com.fiap.product.application.usecase.GetProductByIdUseCase;
import br.com.fiap.product.application.usecase.GetProductBySkuOrDescriptionUseCase;
import br.com.fiap.product.application.usecase.UpdateProductUseCase;
import br.com.fiap.product.presentation.api.dto.ProductFilter;
import br.com.fiap.product.presentation.api.dto.ProductInputDto;
import br.com.fiap.product.presentation.api.dto.ProductOutputDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductsController implements ProductsApi {

  private final CreateProductUseCase createProductUseCase;
  private final GetProductByIdUseCase getProductByIdUseCase;
  private final UpdateProductUseCase updateProductUseCase;
  private final DeleteProductUseCase deleteProductUseCase;
  private final GetProductBySkuOrDescriptionUseCase getProductBySkuOrDescriptionUseCase;
  private final GetAllProductsUseCase getAllProductsUseCase;

  public ProductsController(CreateProductUseCase createProductUseCase,
      GetProductByIdUseCase getProductByIdUseCase, UpdateProductUseCase updateProductUseCase,
      DeleteProductUseCase deleteProductUseCase,
      GetProductBySkuOrDescriptionUseCase getProductBySkuOrDescriptionUseCase,
      GetAllProductsUseCase getAllProductsUseCase) {
    this.createProductUseCase = createProductUseCase;
    this.getProductByIdUseCase = getProductByIdUseCase;
    this.updateProductUseCase = updateProductUseCase;
    this.deleteProductUseCase = deleteProductUseCase;
    this.getProductBySkuOrDescriptionUseCase = getProductBySkuOrDescriptionUseCase;
    this.getAllProductsUseCase = getAllProductsUseCase;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public ProductOutputDto postProduct(@RequestBody @Valid ProductInputDto productInputDto) {
    var product = productInputDto.toProduct();
    var productCreated = createProductUseCase.execute(product);
    return ProductOutputDto.from(productCreated);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Override
  public ProductOutputDto getProductById(@PathVariable String id) {
    var product = getProductByIdUseCase.execute(id);
    return ProductOutputDto.from(product);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @Override
  public ProductOutputDto putProduct(@PathVariable String id,
      @RequestBody @Valid ProductInputDto productInputDto) {
    var product = productInputDto.toProduct();
    var productUpdated = updateProductUseCase.execute(id, product);
    return ProductOutputDto.from(productUpdated);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Override
  public void deleteProduct(@PathVariable String id) {
    deleteProductUseCase.execute(id);
  }

  @GetMapping("/sku-description")
  @ResponseStatus(HttpStatus.OK)
  @Override
  public Page<ProductOutputDto> getAllProductsBySkuOrDescription(ProductFilter productFilter,
      @PageableDefault(sort = {"description"}) Pageable pageable) {
    var productsPage = getProductBySkuOrDescriptionUseCase.execute(productFilter.sku(),
        productFilter.description(), pageable);
    return !productsPage.getContent().isEmpty() ? ProductOutputDto.toPage(productsPage)
        : Page.empty();
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Override
  public Page<ProductOutputDto> getAllProducts(
      @PageableDefault(sort = {"description"}) Pageable pageable) {
    var productsPage = getAllProductsUseCase.execute(pageable);
    return !productsPage.getContent().isEmpty() ? ProductOutputDto.toPage(productsPage)
        : Page.empty();
  }
}
