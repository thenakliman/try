package com.thenakliman.tries;

import java.util.function.Supplier;

public class Try {
  public static TryToCall toCall(Callable callable) {
    return new TryToCall(callable);
  }

  public static <T> TryToGet<T> toGet(Supplier<T> supplier) {
    return new TryToGet<>(supplier);
  }
}
