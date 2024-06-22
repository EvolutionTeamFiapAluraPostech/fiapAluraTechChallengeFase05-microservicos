package br.com.fiap.product.presentation.api;

import br.com.fiap.product.presentation.api.dto.ProductFilter;
import br.com.fiap.product.presentation.api.dto.ProductInputDto;
import br.com.fiap.product.presentation.api.dto.ProductOutputDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "ProductsApi", description = "API de cadastro de produtos")
public interface ProductsApi {

  @Operation(summary = "Cadastro de produtos",
      description = "Endpoint para cadastrar novos produtos.",
      tags = {"ProductsApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "successful operation",
          content = {
              @Content(mediaType = "application/json", schema = @Schema(implementation = ProductOutputDto.class))}),
      @ApiResponse(responseCode = "400", description = "bad request para validação de nome, descrição e unidade de medida.",
          content = {@Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "409", description = "conflict para SKU já cadastrado em outro produto", content = {
          @Content(schema = @Schema(hidden = true))})})
  ProductOutputDto postProduct(
      @Parameter(description = "DTO de entrada com atributos para se cadastrar um novo produto. "
          + "Campos obrigatórios sku, descrição e unidade de medida.")
      ProductInputDto productInputDto);

  @Operation(summary = "Recupera um produto",
      description = "Endpoint para recuperar um produto pelo ID cadastrado",
      tags = {"ProductsApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "successful operation", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ProductOutputDto.class))}),
      @ApiResponse(responseCode = "404", description = "not found para produto não encontrado com o ID",
          content = {@Content(schema = @Schema(hidden = true))})})
  ProductOutputDto getProductById(@Parameter(description = "UUID válido de um produto") String id);

  @Operation(summary = "Atualiza um produto",
      description = "Endpoint para atualizar dados de um produto",
      tags = {"ProductsApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "successful operation", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ProductOutputDto.class))}),
      @ApiResponse(responseCode = "400", description = "bad request para UUID inválido", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "404", description = "not found para produto não encontrado", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "409", description = "conflict para SKU já cadastrado em outro produto", content = {
          @Content(schema = @Schema(hidden = true))})})
  ProductOutputDto putProduct(@Parameter(description = "UUID válido de um produto") String id,
      @Parameter(description = "DTO de entrada com atributos para se cadastrar um novo produto. "
          + "Campos obrigatórios sku, descrição e unidade de medida.")
      ProductInputDto productInputDto);

  @Operation(summary = "Exclui um produto",
      description = "Endpoint para excluir um produto. A exclusão é feita por soft delete",
      tags = {"ProductsApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "successful operation", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "400", description = "bad request para UUID inválido", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "404", description = "not found para produto não encontrado", content = {
          @Content(schema = @Schema(hidden = true))})})
  void deleteProduct(@Parameter(description = "UUID válido de um produto") String id);

  @Operation(summary = "Lista de produtos paginada",
      description = "Endpoint para recuperar uma lista paginada de produtos, filtrada por sku OU descrição, ordenada por descrição",
      tags = {"ProductsApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "successful operation", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ProductOutputDto.class))}),
      @ApiResponse(responseCode = "404", description = "not found para produto não encontrado",
          content = {@Content(schema = @Schema(hidden = true))})})
  Page<ProductOutputDto> getAllProductsBySkuOrDescription(
      @Parameter(description = "DTO com os atributos sku e description para serem utilizados como filtro de pesquisa.") ProductFilter productFilter,
      @Parameter(description = "Interface com atributos para paginação") Pageable pageable);

  @Operation(summary = "Lista de todos os produtos paginada",
      description = "Endpoint para recuperar uma lista paginada de todos os produtos, ordenada por descrição",
      tags = {"ProductsApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "successful operation", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ProductOutputDto.class))}),
      @ApiResponse(responseCode = "404", description = "not found para produto não encontrado",
          content = {@Content(schema = @Schema(hidden = true))})})
  Page<ProductOutputDto> getAllProducts(
      @Parameter(description = "Interface com atributos para paginação") Pageable pageable);
}
