package br.com.users.user.presentation.dto;

import br.com.users.user.domain.entity.User;
import br.com.users.user.domain.enums.DocNumberType;
import br.com.users.user.presentation.dto.customvalidator.CPFouCNPJ;
import io.swagger.v3.oas.annotations.tags.Tag;

@CPFouCNPJ
@Tag(name = "PutUserInputDto", description = "DTO de entrada representação de um usuário, para atualização de dados")
public class PutUserInputDto extends UserInputDto {

  public PutUserInputDto(String name, String email, String docNumberType, String docNumber) {
    super(name, email, docNumberType, docNumber);
  }

  public static User toUser(PutUserInputDto putUserInputDto) {
    return User.builder()
        .name(putUserInputDto.getName())
        .email(putUserInputDto.getEmail())
        .docNumberType(DocNumberType.valueOf(putUserInputDto.getDocNumberType()))
        .docNumber(putUserInputDto.getDocNumber())
        .build();
  }
}
