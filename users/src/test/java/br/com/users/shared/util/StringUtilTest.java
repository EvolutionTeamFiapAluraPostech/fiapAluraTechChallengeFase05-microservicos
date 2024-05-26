package br.com.users.shared.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StringUtilTest {

  @Test
  void shouldGenerateStringWith100Length() {
    var userName = StringUtil.generateStringLength(100);
    assertThat(userName).isNotNull().isNotBlank();
    assertThat(userName.length()).isEqualTo(100);
  }

}