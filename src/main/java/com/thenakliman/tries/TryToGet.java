package com.thenakliman.tries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.thenakliman.tries.SneakyThrower.sneakyThrow;
import static com.thenakliman.tries.Utils.closeResources;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

class TryToGet<T> {
  final private Supplier<T> valueSupplier;
  final private AutoCloseable[] resources;

  TryToGet(final Supplier<T> valueSupplier) {
    this.valueSupplier = valueSupplier;
    this.resources = new AutoCloseable[0];
  }

  public TryToGet(final Supplier<T> valueSupplier, final AutoCloseable[] resources) {
    this.valueSupplier = valueSupplier;
    this.resources = resources;
  }

  @SuppressWarnings("unchecked")
  ThenHandler<T> ifRaises(final Class<? extends Throwable>... exceptionClasses) {
    return new ThenHandler<T>(valueSupplier, asList(exceptionClasses), Collections.emptyList(), this.resources);
  }

  public static class ThenHandler<T> {
    final private Supplier<T> valueSupplier;
    final private List<Class<? extends Throwable>> exceptionClass;
    final private List<IExceptionHandler<T>> exceptionHandlers;
    final private AutoCloseable[] resources;
    final private Consumer<T> DO_NOTHING_CONSUMER = (value) -> {
    };

    public ThenHandler(final Supplier<T> valueSupplier,
                       final List<Class<? extends Throwable>> exceptionClass,
                       final List<IExceptionHandler<T>> exceptionHandlers,
                       final AutoCloseable[] resources) {

      this.valueSupplier = valueSupplier;
      this.exceptionClass = exceptionClass;
      this.exceptionHandlers = exceptionHandlers;
      this.resources = resources;
    }

    public Executor<T> thenGet(final Function<Throwable, T> getterFromException) {
      final List<IExceptionHandler<T>> registeredExceptionHandlers = new ArrayList<>(this.exceptionHandlers);
      final List<ExceptionConsumer<T>> exceptionConsumers = this.exceptionClass.stream()
              .map(exceptionClass -> new ExceptionConsumer<T>(exceptionClass, getterFromException))
              .collect(Collectors.toList());
      registeredExceptionHandlers.addAll(exceptionConsumers);
      return new Executor<T>(
              this.valueSupplier,
              unmodifiableList(registeredExceptionHandlers),
              DO_NOTHING_CONSUMER,
              this.resources);
    }

    public Executor<T> thenThrow(final Function<Throwable, ? extends Throwable> exceptionGetter) {
      final List<IExceptionHandler<T>> registeredExceptionHandlers = new ArrayList<>(this.exceptionHandlers);
      final List<IExceptionHandler<T>> exceptionThrowers = this.exceptionClass.stream()
              .map(exceptionClass -> new ExceptionThrower<T>(exceptionClass, exceptionGetter))
              .collect(Collectors.toList());
      registeredExceptionHandlers.addAll(exceptionThrowers);
      return new Executor<T>(
              this.valueSupplier,
              unmodifiableList(registeredExceptionHandlers),
              DO_NOTHING_CONSUMER,
              this.resources);
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
    final private AutoCloseable[] resources;

    public Executor(final Supplier<T> valueSupplier,
                    final List<IExceptionHandler<T>> exceptionHandlers,
                    final Consumer<T> elseConsumer,
                    final AutoCloseable[] resources) {

      this.valueSupplier = valueSupplier;
      this.exceptionHandlers = exceptionHandlers;
      this.elseConsumer = elseConsumer;
      this.resources = resources;
    }

    @Override
    public T done() {
      final T value;
      try {
        value = this.valueSupplier.get();
      } catch (Throwable exception) {
        return handleException(exception);
      }
      closeResources(this.resources);
      this.elseConsumer.accept(value);
      return value;
    }

    private T handleException(Throwable exception) {
      closeResources(this.resources);
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
      boolean success = false;
      try {
        value = this.valueSupplier.get();
        success = true;
      } catch (Throwable exception) {
        return handleException(exception);
      } finally {
        if (success) {
          closeResources(this.resources);
        }
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
              elseConsumer,
              this.resources);
    }

    @SuppressWarnings("unchecked")
    public ThenHandler<T> elseIfRaises(final Class<? extends Throwable>... exceptionClasses) {
      return new ThenHandler<>(this.valueSupplier, asList(exceptionClasses), this.exceptionHandlers, this.resources);
    }
  }
}