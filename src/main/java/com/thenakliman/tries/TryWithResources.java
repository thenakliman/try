package com.thenakliman.tries;

import java.util.function.Supplier;

public class TryWithResources {
  final private AutoCloseable[] resources;

  public TryWithResources(final AutoCloseable[] resources) {
    this.resources = resources;
  }

  public TryToCall toCall(final Callable callable) {
    return new TryToCall(callable, this.resources);
  }

  public <T> TryToGet<T> toGet(Supplier<T> supplier) {
    return new TryToGet<>(supplier, this.resources);
  }
}
