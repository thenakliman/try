package com.thenakliman.tries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.thenakliman.tries.SneakyThrower.sneakyThrow;
import static com.thenakliman.tries.Utils.executeCallable;
import static java.util.Collections.emptyList;

class TryToGet<T> {
  final private Supplier<T> valueSupplier;

  TryToGet(final Supplier<T> valueSupplier) {
    this.valueSupplier = valueSupplier;
  }

  <X extends Throwable> ThenHandler<T> acceptException(final Class<? extends X> exceptionClass) throws X {
    return new ThenHandler<>(valueSupplier, exceptionClass, emptyList());
  }

  public static class ThenHandler<T> {
    final private Supplier<T> valueSupplier;
    final private Class<? extends Throwable> exceptionClass;
    final private List<IExceptionHandler<T>> exceptionHandlers;
    final private Consumer<T> DO_NOTHING_CONSUMER = (value) -> {
    };

    public ThenHandler(final Supplier<T> valueSupplier,
                       final Class<? extends Throwable> exceptionClass,
                       final List<IExceptionHandler<T>> exceptionHandlers) {

      this.valueSupplier = valueSupplier;
      this.exceptionClass = exceptionClass;
      this.exceptionHandlers = exceptionHandlers;
    }

    public Executor<T> thenGet(final Function<Throwable, T> getterFromException) {
      final List<IExceptionHandler<T>> registeredExceptionHandlers = new ArrayList<>(this.exceptionHandlers);
      registeredExceptionHandlers.add(new ExceptionConsumer<>(this.exceptionClass, getterFromException));
      return new Executor<T>(this.valueSupplier, exceptionClass, registeredExceptionHandlers, DO_NOTHING_CONSUMER);
    }

    public Executor<T> thenRethrow(final Function<Throwable, ? extends Throwable> exceptionGetter) {
      final List<IExceptionHandler<T>> registeredExceptionHandlers = new ArrayList<>(this.exceptionHandlers);
      registeredExceptionHandlers.add(new ExceptionThrower<>(this.exceptionClass, exceptionGetter));
      return new Executor<>(this.valueSupplier, exceptionClass, registeredExceptionHandlers, DO_NOTHING_CONSUMER);
    }
  }

  interface IExceptionHandler<T> {
    T handleException(final Throwable exception);

    Class<? extends Throwable> getThrowableClass();
  }

  static class ExceptionConsumer<T> implements IExceptionHandler<T> {
    final private Class<? extends Throwable> throwableClass;
    final private Function<Throwable, ? extends T> valueGetter;

    ExceptionConsumer(final Class<? extends Throwable> throwableClass,
                      final Function<Throwable, ? extends T> valueGetter) {
      this.throwableClass = throwableClass;
      this.valueGetter = valueGetter;
    }

    public Class<? extends Throwable> getThrowableClass() {
      return throwableClass;
    }

    @Override
    public T handleException(Throwable exception) {
      return this.valueGetter.apply(exception);
    }
  }

  static class ExceptionThrower<T> implements IExceptionHandler<T> {
    final private Class<? extends Throwable> throwableClass;
    final Function<Throwable, ? extends Throwable> exceptionFunction;

    ExceptionThrower(final Class<? extends Throwable> throwableClass,
                     final Function<Throwable, ? extends Throwable> exceptionFunction) {
      this.throwableClass = throwableClass;
      this.exceptionFunction = exceptionFunction;
    }

    public Class<? extends Throwable> getThrowableClass() {
      return throwableClass;
    }

    @Override
    public T handleException(final Throwable exception) {
      throw sneakyThrow(exceptionFunction.apply(exception));
    }
  }

  public static class Executor<T> {
    final private Supplier<T> valueSupplier;
    final private Class<? extends Throwable> exceptionClass;
    final private List<IExceptionHandler<T>> exceptionHandlers;
    final private Consumer<T> elseConsumer;

    public Executor(final Supplier<T> valueSupplier,
                    final Class<? extends Throwable> exceptionClass,
                    final List<IExceptionHandler<T>> exceptionHandlers,
                    final Consumer<T> elseConsumer) {

      this.valueSupplier = valueSupplier;
      this.exceptionClass = exceptionClass;
      this.exceptionHandlers = exceptionHandlers;
      this.elseConsumer = elseConsumer;
    }

    public T done() {
      final T value;
      try {
        value = this.valueSupplier.get();
      } catch (Throwable exception) {
        return handleException(exception);
      }
      this.elseConsumer.accept(value);
      return value;
    }

    private T handleException(Throwable exception) {
      Optional<IExceptionHandler<T>> first = this.exceptionHandlers.stream()
              .filter(exceptionHandler -> exceptionHandler.getThrowableClass().isInstance(exception))
              .findFirst();

      if (first.isPresent()) {
        return first.get().handleException(exception);
      }

      throw sneakyThrow(exception);
    }

    public T finallyDone(final Callable finallyCallable) {
      final T value;
      try {
        value = this.valueSupplier.get();
      } catch (Throwable exception) {
        return handleException(exception);
      } finally {
        executeCallable(finallyCallable);
      }
      this.elseConsumer.accept(value);
      return value;
    }

    public Executor<T> elseCall(final Consumer<T> elseConsumer) {
      return new Executor<>(
              this.valueSupplier,
              this.exceptionClass,
              this.exceptionHandlers,
              elseConsumer);
    }
  }
}