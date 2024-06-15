package br.com.fiap.company.presentation.api;

import br.com.fiap.company.presentation.api.dto.CompanyFilter;
import br.com.fiap.company.presentation.api.dto.CompanyInputDto;
import br.com.fiap.company.presentation.api.dto.CompanyOutputDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "CompaniesApi", description = "API de cadastro de empresas")
public interface CompaniesApi {

  @Operation(summary = "Cadastro de empresas",
      description = "Endpoint para cadastrar novas empresas.",
      tags = {"CompaniesApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "successful operation",
          content = {
              @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyOutputDto.class))}),
      @ApiResponse(responseCode = "400", description = "bad request para validação de nome, e-mail, número do documento (CPF/CNPJ), rua, número, bairro, cidade, Estado, país e CEP.",
          content = {@Content(schema = @Schema(hidden = true))})})
  CompanyOutputDto postCompany(
      @Parameter(description = "DTO com atributos para se cadastrar uma nova empresa. Requer validação de nome, e-mail, número do documento (CPF/CNPJ), rua, número, bairro, cidade, Estado, país e CEP.")
      CompanyInputDto companyInputDto);

  @Operation(summary = "Recupera uma empresa",
      description = "Endpoint para recuperar uma empresa pelo ID cadastrado",
      tags = {"CompaniesApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "successful operation", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyOutputDto.class))}),
      @ApiResponse(responseCode = "404", description = "not found para empresa não encontrada",
          content = {@Content(schema = @Schema(hidden = true))})})
  CompanyOutputDto getCompanyById(
      @Parameter(description = "UUID válido de uma empresa") String id);

  @Operation(summary = "Atualiza uma empresa",
      description = "Endpoint para atualizar dados de uma empresa",
      tags = {"CompaniesApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "successful operation", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyOutputDto.class))}),
      @ApiResponse(responseCode = "400", description = "bad request para UUID inválido", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "404", description = "not found para empresa não encontrada", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "409", description = "conflict para CPF/CNPJ já cadastrado em outra empresa", content = {
          @Content(schema = @Schema(hidden = true))})})
  CompanyOutputDto putCompany(@Parameter(description = "UUID válido de uma empresa") String id,
      @Parameter(description = "DTO com atributos para se atualizar uma empresa. Requer validação de nome, e-mail, número do documento (CPF/CNPJ), rua, número, bairro, cidade, Estado, país e CEP.")
      CompanyInputDto companyInputDto);

  @Operation(summary = "Exclui uma empresa",
      description = "Endpoint para excluir uma empresa. A exclusão é feita por soft delete",
      tags = {"CompaniesApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "successful operation", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "400", description = "bad request para UUID inválido", content = {
          @Content(schema = @Schema(hidden = true))}),
      @ApiResponse(responseCode = "404", description = "not found para empresa não encontrada", content = {
          @Content(schema = @Schema(hidden = true))})})
  void deleteCompany(@Parameter(description = "UUID válido do empresa") String id);

  @Operation(summary = "Lista de empresas paginada",
      description = "Endpoint para recuperar uma lista paginada de empresas, filtrada por nome OU email, ordenada por nome",
      tags = {"CompaniesApi"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "successful operation", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyOutputDto.class))}),
      @ApiResponse(responseCode = "404", description = "not found para empresa não encontrada",
          content = {@Content(schema = @Schema(hidden = true))})})
  Page<CompanyOutputDto> getCompaniesByNameOrEmail(
      @Parameter(description = "DTO com os atributos nome ou email para serem utilizados como filtro de pesquisa.") CompanyFilter companyFilter,
      @Parameter(description = "Interface com atributos para paginação") Pageable pageable);
}
