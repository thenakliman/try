package com.thenakliman.tries;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class TryToGet<T> {
  final private Supplier<T> valueSupplier;

  TryToGet(final Supplier<T> valueSupplier) {
    this.valueSupplier = valueSupplier;
  }

  SupplierWithRuntimeException<T> acceptRuntimeException(final Class<? extends RuntimeException> exceptionClass) {
    return new SupplierWithRuntimeException<>(valueSupplier, exceptionClass);
  }

  public static class SupplierWithRuntimeException<T> {
    final private Supplier<T> valueSupplier;
    final private Class<? extends RuntimeException> exceptionClass;
    final private Consumer<T> DO_NOTHING_CONSUMER = (value) -> {
    };

    public SupplierWithRuntimeException(final Supplier<T> valueSupplier, final Class<? extends RuntimeException> exceptionClass) {
      this.valueSupplier = valueSupplier;
      this.exceptionClass = exceptionClass;
    }

    public IExceptionHandler<T> thenGet(final Function<RuntimeException, T> supplierFromException) {
      return new ExceptionHandler<>(this.valueSupplier, exceptionClass, supplierFromException, DO_NOTHING_CONSUMER);
    }

    public IThrowExceptionHandler<T> thenRethrow(final Function<RuntimeException, ? extends RuntimeException> exceptionFunction) {
      return new RethrowExceptionHandler<>(this.valueSupplier, exceptionClass, exceptionFunction);
    }
  }

  interface IThrowExceptionHandler<T> {
    T done();

    T finallyDone(final Callable callable);
  }

  interface IExceptionHandler<T> extends IThrowExceptionHandler<T> {
    IThrowExceptionHandler<T> elseCall(final Consumer<T> consumer);
  }

  public static class RethrowExceptionHandler<T> implements IThrowExceptionHandler<T> {
    final private Supplier<T> valueSupplier;
    final private Class<? extends RuntimeException> exceptionClass;
    final private Function<RuntimeException, ? extends RuntimeException> exceptionFunction;

    public RethrowExceptionHandler(final Supplier<T> valueSupplier,
                                   final Class<? extends RuntimeException> exceptionClass,
                                   final Function<RuntimeException, ? extends RuntimeException> exceptionFunction) {

      this.valueSupplier = valueSupplier;
      this.exceptionClass = exceptionClass;
      this.exceptionFunction = exceptionFunction;
    }

    public T done() {
      try {
        return this.valueSupplier.get();
      } catch (RuntimeException exception) {
        if (this.exceptionClass.isInstance(exception)) {
          throw this.exceptionFunction.apply(exception);
        } else {
          throw exception;
        }
      }
    }

    @Override
    public T finallyDone(Callable finallyCallable) {
      try {
        return this.valueSupplier.get();
      } catch (RuntimeException exception) {
        if (this.exceptionClass.isInstance(exception)) {
          throw this.exceptionFunction.apply(exception);
        } else {
          throw exception;
        }
      } finally {
        finallyCallable.call();
      }
    }
  }

  public static class ExceptionHandler<T> implements IExceptionHandler<T> {
    final private Supplier<T> valueSupplier;
    final private Class<? extends RuntimeException> exceptionClass;
    final private Function<RuntimeException, T> exceptionFunction;
    final private Consumer<T> elseConsumer;

    public ExceptionHandler(final Supplier<T> valueSupplier,
                            final Class<? extends RuntimeException> exceptionClass,
                            final Function<RuntimeException, T> exceptionFunction,
                            final Consumer<T> elseConsumer) {

      this.valueSupplier = valueSupplier;
      this.exceptionClass = exceptionClass;
      this.exceptionFunction = exceptionFunction;
      this.elseConsumer = elseConsumer;
    }

    @Override
    public T done() {
      final T value;
      try {
        value = this.valueSupplier.get();
      } catch (RuntimeException exception) {
        if (this.exceptionClass.isInstance(exception)) {
          return this.exceptionFunction.apply(exception);
        }
        throw exception;
      }
      this.elseConsumer.accept(value);
      return value;
    }

    @Override
    public T finallyDone(Callable finallyCallable) {
      final T value;
      try {
        value = this.valueSupplier.get();
      } catch (RuntimeException exception) {
        if (this.exceptionClass.isInstance(exception)) {
          return this.exceptionFunction.apply(exception);
        }
        throw exception;
      } finally {
        finallyCallable.call();
      }
      this.elseConsumer.accept(value);
      return value;
    }

    @Override
    public IThrowExceptionHandler<T> elseCall(final Consumer<T> elseConsumer) {
      return new ExceptionHandler<>(
              this.valueSupplier,
              this.exceptionClass,
              this.exceptionFunction,
              elseConsumer);
    }
  }
}