package com.thenakliman.tries;

import java.util.function.Consumer;
import java.util.function.Function;

class TryToCall {
  final private Callable callable;

  TryToCall(Callable callable) {
    this.callable = callable;
  }

  CodeWithRuntimeException acceptRuntimeException(Class<? extends RuntimeException> exceptionClass) {
    return new CodeWithRuntimeException(callable, exceptionClass);
  }

  public static class CodeWithRuntimeException {
    final private Callable callable;
    final private Class<? extends RuntimeException> exceptionClass;

    public CodeWithRuntimeException(Callable callable, Class<? extends RuntimeException> exceptionClass) {
      this.callable = callable;
      this.exceptionClass = exceptionClass;
    }

    public IExceptionHandler thenCall(Consumer<RuntimeException> exceptionConsumer) {
      return new ExceptionHandler(this.callable, exceptionClass, exceptionConsumer);
    }

    public IExceptionHandler thenRethrow(Function<RuntimeException, ? extends RuntimeException> exceptionFunction) {
      return new RethrowExceptionHandler(this.callable, exceptionClass, exceptionFunction);
    }
  }

  interface IExceptionHandler {
    void done();

    void finallyDone(Callable callable);
  }

  public static class RethrowExceptionHandler implements IExceptionHandler {
    final private Callable callable;
    final private Class<? extends Throwable> exceptionClass;
    final private Function<RuntimeException, ? extends RuntimeException> exceptionFunction;

    public RethrowExceptionHandler(Callable callable,
                                   Class<? extends RuntimeException> exceptionClass,
                                   Function<RuntimeException, ? extends RuntimeException> exceptionFunction) {

      this.callable = callable;
      this.exceptionClass = exceptionClass;
      this.exceptionFunction = exceptionFunction;
    }

    @Override
    public void done() {
      try {
        callable.call();
      } catch (RuntimeException exception) {
        if (exceptionClass.isInstance(exception)) {
          throw exceptionFunction.apply(exception);
        } else {
          throw exception;
        }
      }
    }

    @Override
    public void finallyDone(Callable finallyCallable) {
      try {
        callable.call();
      } catch (RuntimeException exception) {
        if (exceptionClass.isInstance(exception)) {
          throw exceptionFunction.apply(exception);
        } else {
          throw exception;
        }
      } finally {
        finallyCallable.call();
      }
    }
  }

  public static class ExceptionHandler implements IExceptionHandler {
    final private Callable callable;
    final private Class<? extends Throwable> exceptionClass;
    final private Consumer<RuntimeException> consumer;

    public ExceptionHandler(Callable callable,
                            Class<? extends RuntimeException> exceptionClass,
                            Consumer<RuntimeException> exceptionConsumer) {

      this.callable = callable;
      this.exceptionClass = exceptionClass;
      this.consumer = exceptionConsumer;
    }

    @Override
    public void done() {
      try {
        callable.call();
      } catch (RuntimeException exception) {
        if (exceptionClass.isInstance(exception)) {
          consumer.accept(exception);
        } else {
          throw exception;
        }
      }
    }

    @Override
    public void finallyDone(Callable finallCallable) {
      try {
        callable.call();
      } catch (RuntimeException exception) {
        if (exceptionClass.isInstance(exception)) {
          consumer.accept(exception);
        } else {
          throw exception;
        }
      } finally {
        finallCallable.call();
      }
    }
  }
}
