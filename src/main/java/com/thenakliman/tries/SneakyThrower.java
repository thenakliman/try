package com.thenakliman.tries;

class SneakyThrower {
  public static <E extends Throwable> void sneakyThrow(Throwable exception) throws E {
    throw (E) exception;
  }
}
