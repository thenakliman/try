package com.thenakliman.tries;

import static com.thenakliman.tries.SneakyThrower.sneakyThrow;

public class Utils {
  static void executeCallable(Callable callable) {
    try {
      callable.call();
    } catch (Throwable throwable) {
      throw sneakyThrow(throwable);
    }
  }
}
