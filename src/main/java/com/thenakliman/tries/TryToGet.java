package com.thenakliman.tries;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.thenakliman.tries.SneakyThrower.sneakyThrow;

class TryToGet<T> {
  final private Supplier<T> valueSupplier;

  TryToGet(final Supplier<T> valueSupplier) {
    this.valueSupplier = valueSupplier;
  }


  <X extends Throwable> ExceptionSupplier<T> acceptException(final Class<? extends X> exceptionClass) throws X {
    return new ExceptionSupplier<>(valueSupplier, exceptionClass);
  }

  public static class ExceptionSupplier<T> {
    final private Supplier<T> valueSupplier;
    final private Class<? extends Throwable> exceptionClass;
    final private Consumer<T> DO_NOTHING_CONSUMER = (value) -> {
    };

    public ExceptionSupplier(final Supplier<T> valueSupplier, final Class<? extends Throwable> exceptionClass) {
      this.valueSupplier = valueSupplier;
      this.exceptionClass = exceptionClass;
    }

    public IExceptionHandler<T> thenGet(final Function<Throwable, T> exceptionSupplier) {
      return new ExceptionHandler<>(this.valueSupplier, exceptionClass, exceptionSupplier, DO_NOTHING_CONSUMER);
    }

    public IThrowExceptionHandler<T> thenRethrow(final Function<Throwable, ? extends Throwable> exceptionFunction) {
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
    final private Class<? extends Throwable> exceptionClass;
    final private Function<Throwable, ? extends Throwable> exceptionFunction;

    public RethrowExceptionHandler(final Supplier<T> valueSupplier,
                                   final Class<? extends Throwable> exceptionClass,
                                   final Function<Throwable, ? extends Throwable> exceptionFunction) {

      this.valueSupplier = valueSupplier;
      this.exceptionClass = exceptionClass;
      this.exceptionFunction = exceptionFunction;
    }

    public T done() {
      try {
        return this.valueSupplier.get();
      } catch (Throwable exception) {
        if (this.exceptionClass.isInstance(exception)) {
          sneakyThrow(this.exceptionFunction.apply(exception));
          return null; // needed for compiler
        } else {
          throw exception;
        }
      }
    }

    @Override
    public T finallyDone(Callable finallyCallable) {
      try {
        return this.valueSupplier.get();
      } catch (Throwable exception) {
        if (this.exceptionClass.isInstance(exception)) {
          sneakyThrow(this.exceptionFunction.apply(exception));
          return null; // needed for compiler
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
    final private Class<? extends Throwable> exceptionClass;
    final private Function<Throwable, T> exceptionFunction;
    final private Consumer<T> elseConsumer;

    public ExceptionHandler(final Supplier<T> valueSupplier,
                            final Class<? extends Throwable> exceptionClass,
                            final Function<Throwable, T> exceptionFunction,
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
      } catch (Throwable exception) {
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
      } catch (Throwable exception) {
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