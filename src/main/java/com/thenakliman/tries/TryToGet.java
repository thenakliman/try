package com.thenakliman.tries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.thenakliman.tries.SneakyThrower.sneakyThrow;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

class TryToGet<T> {
  final private Supplier<T> valueSupplier;

  TryToGet(final Supplier<T> valueSupplier) {
    this.valueSupplier = valueSupplier;
  }

  @SuppressWarnings("unchecked")
  ThenHandler<T> ifRaises(final Class<? extends Throwable>... exceptionClasses) {
    return new ThenHandler<T>(valueSupplier, asList(exceptionClasses), emptyList());
  }

  public static class ThenHandler<T> {
    final private Supplier<T> valueSupplier;
    final private List<Class<? extends Throwable>> exceptionClass;
    final private List<IExceptionHandler<T>> exceptionHandlers;
    final private Consumer<T> DO_NOTHING_CONSUMER = (value) -> {
    };

    public ThenHandler(final Supplier<T> valueSupplier,
                       final List<Class<? extends Throwable>> exceptionClass,
                       final List<IExceptionHandler<T>> exceptionHandlers) {

      this.valueSupplier = valueSupplier;
      this.exceptionClass = exceptionClass;
      this.exceptionHandlers = exceptionHandlers;
    }

    public Executor<T> thenGet(final Function<Throwable, T> getterFromException) {
      final List<IExceptionHandler<T>> registeredExceptionHandlers = new ArrayList<>(this.exceptionHandlers);
      final List<ExceptionConsumer<T>> exceptionConsumers = this.exceptionClass.stream()
              .map(exceptionClass -> new ExceptionConsumer<T>(exceptionClass, getterFromException))
              .collect(Collectors.toList());
      registeredExceptionHandlers.addAll(exceptionConsumers);
      return new Executor<T>(this.valueSupplier, unmodifiableList(registeredExceptionHandlers), DO_NOTHING_CONSUMER);
    }

    public Executor<T> thenThrow(final Function<Throwable, ? extends Throwable> exceptionGetter) {
      final List<IExceptionHandler<T>> registeredExceptionHandlers = new ArrayList<>(this.exceptionHandlers);
      final List<IExceptionHandler<T>> exceptionThrowers = this.exceptionClass.stream()
              .map(exceptionClass -> new ExceptionThrower<T>(exceptionClass, exceptionGetter))
              .collect(Collectors.toList());
      registeredExceptionHandlers.addAll(exceptionThrowers);
      return new Executor<T>(this.valueSupplier, unmodifiableList(registeredExceptionHandlers), DO_NOTHING_CONSUMER);
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

  interface IElseCall<T> {
    IExecutor<T> elseCall(Consumer<T> consumer);
  }

  interface IExecutor<T> {
    T done();

    T finallyDone(final Callable finallyCallable);
  }

  public static class Executor<T> implements IElseCall<T>, IExecutor<T> {
    final private Supplier<T> valueSupplier;
    final private List<IExceptionHandler<T>> exceptionHandlers;
    final private Consumer<T> elseConsumer;

    public Executor(final Supplier<T> valueSupplier,
                    final List<IExceptionHandler<T>> exceptionHandlers,
                    final Consumer<T> elseConsumer) {

      this.valueSupplier = valueSupplier;
      this.exceptionHandlers = exceptionHandlers;
      this.elseConsumer = elseConsumer;
    }

    @Override
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

    @Override
    public T finallyDone(final Callable finallyCallable) {
      final T value;
      try {
        value = this.valueSupplier.get();
      } catch (Throwable exception) {
        return handleException(exception);
      } finally {
        Utils.executeCallable(finallyCallable);
      }
      this.elseConsumer.accept(value);
      return value;
    }

    @Override
    public IExecutor<T> elseCall(final Consumer<T> elseConsumer) {
      return new Executor<T>(
              this.valueSupplier,
              this.exceptionHandlers,
              elseConsumer);
    }

    @SuppressWarnings("unchecked")
    public ThenHandler<T> elseIfRaises(final Class<? extends Throwable>... exceptionClasses) {
      return new ThenHandler<>(this.valueSupplier, asList(exceptionClasses), this.exceptionHandlers);
    }
  }
}