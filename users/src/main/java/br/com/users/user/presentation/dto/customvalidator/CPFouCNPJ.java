package br.com.users.user.presentation.dto.customvalidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR})
@Constraint(validatedBy = UserDocNumberTypeAndDocNumberValueValidator.class)
public @interface CPFouCNPJ {

  String message() default "CPF ou CNPJ inválido";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
