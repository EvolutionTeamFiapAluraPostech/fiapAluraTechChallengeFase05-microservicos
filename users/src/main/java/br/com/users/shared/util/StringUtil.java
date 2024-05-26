package br.com.users.shared.util;

import java.util.Random;

public final class StringUtil {

  public static String generateStringLength(Integer targetStringLength) {
    var leftLimit = 97; // letter 'a'
    var rightLimit = 122; // letter 'z'
    var random = new Random();

    return random.ints(leftLimit, rightLimit + 1)
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
