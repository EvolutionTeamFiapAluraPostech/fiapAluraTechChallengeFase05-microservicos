package br.com.fiap.payment.presentation.api;

import br.com.fiap.payment.application.usecase.CreatePaymentUseCase;
import br.com.fiap.payment.presentation.api.dto.PaymentDto;
import br.com.fiap.payment.presentation.api.dto.PaymentInputDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController implements PaymentsApi {

  private final CreatePaymentUseCase createPaymentUseCase;

  public PaymentController(CreatePaymentUseCase createPaymentUseCase) {
    this.createPaymentUseCase = createPaymentUseCase;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public PaymentDto postOrderPayment(@RequestBody @Valid PaymentInputDto paymentInputDto) {
    var payment = createPaymentUseCase.execute(paymentInputDto);
    return PaymentDto.from(payment);
  }
}
