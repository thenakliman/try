package com.thenakliman.tries;

import java.util.function.Function;
import java.util.function.Supplier;

class TryToGet<T> {
  final private Supplier<T> supplier;

  TryToGet(Supplier<T> supplier) {
    this.supplier = supplier;
  }

  CodeWithRuntimeException<T> acceptRuntimeException(Class<? extends RuntimeException> exceptionClass) {
    return new CodeWithRuntimeException<>(supplier, exceptionClass);
  }

  public static class CodeWithRuntimeException<T> {
    final private Supplier<T> supplier;
    final private Class<? extends RuntimeException> exceptionClass;

    public CodeWithRuntimeException(Supplier<T> supplier, Class<? extends RuntimeException> exceptionClass) {
      this.supplier = supplier;
      this.exceptionClass = exceptionClass;
    }

    public IExceptionHandler<T> thenGet(Function<RuntimeException, T> exceptionFunction) {
      return new ExceptionHandler<>(this.supplier, exceptionClass, exceptionFunction);
    }

    public IExceptionHandler<T> thenRethrow(Function<RuntimeException, ? extends RuntimeException> exceptionFunction) {
      return new RethrowExceptionHandler<>(this.supplier, exceptionClass, exceptionFunction);
    }
  }

  interface IExceptionHandler<T> {
    T done();

    T finallyDone(Callable callable);
  }

  public static class RethrowExceptionHandler<T> implements IExceptionHandler<T> {
    final private Supplier<T> supplier;
    final private Class<? extends Throwable> exceptionClass;
    final private Function<RuntimeException, ? extends RuntimeException> exceptionFunction;

    public RethrowExceptionHandler(Supplier<T> supplier,
                                   Class<? extends RuntimeException> exceptionClass,
                                   Function<RuntimeException, ? extends RuntimeException> exceptionFunction) {

      this.supplier = supplier;
      this.exceptionClass = exceptionClass;
      this.exceptionFunction = exceptionFunction;
    }

    public T done() {
      try {
        return supplier.get();
      } catch (RuntimeException exception) {
        if (exceptionClass.isInstance(exception)) {
          throw exceptionFunction.apply(exception);
        } else {
          throw exception;
        }
      }
    }

    @Override
    public T finallyDone(Callable finallyCallable) {
      try {
        return supplier.get();
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

  public static class ExceptionHandler<T> implements IExceptionHandler<T> {
    final private Supplier<T> supplier;
    final private Class<? extends Throwable> exceptionClass;
    final private Function<RuntimeException, T> exceptionTFunction;

    public ExceptionHandler(final Supplier<T> supplier,
                            Class<? extends RuntimeException> exceptionClass,
                            Function<RuntimeException, T> exceptionTFunction) {

      this.supplier = supplier;
      this.exceptionClass = exceptionClass;
      this.exceptionTFunction = exceptionTFunction;
    }

    public T done() {
      try {
        return supplier.get();
      } catch (RuntimeException exception) {
        if (exceptionClass.isInstance(exception)) {
          return exceptionTFunction.apply(exception);
        }
        throw exception;
      }
    }

    @Override
    public T finallyDone(Callable finallyCallable) {
      try {
        return supplier.get();
      } catch (RuntimeException exception) {
        if (exceptionClass.isInstance(exception)) {
          return exceptionTFunction.apply(exception);
        }
        throw exception;
      } finally {
        finallyCallable.call();
      }
    }
  }
}