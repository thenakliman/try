package com.thenakliman.tries;

import java.util.stream.IntStream;

import static com.thenakliman.tries.SneakyThrower.sneakyThrow;

public class Utils {
  static void executeCallable(final Callable callable) {
    try {
      callable.call();
    } catch (Throwable throwable) {
      throw sneakyThrow(throwable);
    }
  }

  static void closeResources(final AutoCloseable[] resources) {
    IntStream.rangeClosed(1, resources.length)
            .mapToObj(i -> resources[resources.length - i])
            .forEach(Utils::closeResource);
  }

  private static void closeResource(final AutoCloseable autoCloseable) {
    try {
      autoCloseable.close();
    } catch (Throwable throwable) {
      // do nothing
    }
  }

  static void executeCallable(final Callable callable, final Throwable exception) {
    try {
      callable.call();
    } catch (Throwable throwable) {
      throw sneakyThrow(exception);
    }
  }
}
