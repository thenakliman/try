package com.thenakliman.tries;

public class TryWithResources {
  final private AutoCloseable[] resources;

  public TryWithResources(final AutoCloseable[] resources) {
    this.resources = resources;
  }

  public TryToCall toCall(final Callable callable) {
    return new TryToCall(callable, this.resources);
  }
}
