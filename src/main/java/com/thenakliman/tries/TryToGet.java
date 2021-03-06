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
import static com.thenakliman.tries.Utils.executeCallable;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

class TryToGet<T> {
  final private Supplier<T> valueProvider;
  final private AutoCloseable[] resources;

  TryToGet(final Supplier<T> valueProvider) {
    this.valueProvider = valueProvider;
    this.resources = new AutoCloseable[0];
  }

  public TryToGet(final Supplier<T> valueProvider, final AutoCloseable[] resources) {
    this.valueProvider = valueProvider;
    this.resources = resources;
  }

  @SuppressWarnings("unchecked")
  ThenHandler<T> ifRaises(final Class<? extends Throwable>... exceptionsToBeHandled) {
    return new ThenHandler<T>(valueProvider, asList(exceptionsToBeHandled), Collections.emptyList(), this.resources);
  }

  public static class ThenHandler<T> {
    final private Supplier<T> valueProvider;
    final private List<Class<? extends Throwable>> exceptionsToBeHandled;
    final private List<IExceptionHandler<T>> exceptionHandlers;
    final private AutoCloseable[] resources;
    final private Consumer<T> DO_NOTHING_CONSUMER = (value) -> {
    };

    public ThenHandler(final Supplier<T> valueProvider,
                       final List<Class<? extends Throwable>> exceptionsToBeHandled,
                       final List<IExceptionHandler<T>> exceptionHandlers,
                       final AutoCloseable[] resources) {

      this.valueProvider = valueProvider;
      this.exceptionsToBeHandled = exceptionsToBeHandled;
      this.exceptionHandlers = exceptionHandlers;
      this.resources = resources;
    }

    public Executor<T> thenGet(final Function<Throwable, T> onExceptionValueProvider) {
      final List<IExceptionHandler<T>> alreadyRegisteredExceptionHandlers = new ArrayList<>(this.exceptionHandlers);
      final List<ExceptionConsumer<T>> exceptionConsumers = this.exceptionsToBeHandled.stream()
              .map(exception -> new ExceptionConsumer<T>(exception, onExceptionValueProvider))
              .collect(Collectors.toList());

      alreadyRegisteredExceptionHandlers.addAll(exceptionConsumers);
      return new Executor<>(
              this.valueProvider,
              unmodifiableList(alreadyRegisteredExceptionHandlers),
              DO_NOTHING_CONSUMER,
              this.resources);
    }

    public Executor<T> thenThrow(final Function<Throwable, ? extends Throwable> onExceptionNewExceptionProvider) {
      final List<IExceptionHandler<T>> alreadyRegisteredExceptionHandlers = new ArrayList<>(this.exceptionHandlers);
      final List<IExceptionHandler<T>> exceptionThrowers = this.exceptionsToBeHandled.stream()
              .map(exception -> new ExceptionThrower<T>(exception, onExceptionNewExceptionProvider))
              .collect(Collectors.toList());

      alreadyRegisteredExceptionHandlers.addAll(exceptionThrowers);
      return new Executor<>(
              this.valueProvider,
              unmodifiableList(alreadyRegisteredExceptionHandlers),
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
    final private Function<Throwable, ? extends T> valueProvider;

    ExceptionConsumer(final Class<? extends Throwable> throwableClass,
                      final Function<Throwable, ? extends T> valueProvider) {
      this.throwableClass = throwableClass;
      this.valueProvider = valueProvider;
    }

    public Class<? extends Throwable> getThrowableClass() {
      return this.throwableClass;
    }

    @Override
    public T handleException(Throwable exception) {
      return this.valueProvider.apply(exception);
    }
  }

  static class ExceptionThrower<T> implements IExceptionHandler<T> {
    final private Class<? extends Throwable> throwableClass;
    final Function<Throwable, ? extends Throwable> onExceptionNewExceptionProvider;

    ExceptionThrower(final Class<? extends Throwable> throwableClass,
                     final Function<Throwable, ? extends Throwable> onExceptionNewExceptionProvider) {
      this.throwableClass = throwableClass;
      this.onExceptionNewExceptionProvider = onExceptionNewExceptionProvider;
    }

    public Class<? extends Throwable> getThrowableClass() {
      return this.throwableClass;
    }

    @Override
    public T handleException(final Throwable exception) {
      throw sneakyThrow(this.onExceptionNewExceptionProvider.apply(exception));
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
    final private Supplier<T> valueProvider;
    final private List<IExceptionHandler<T>> exceptionsToBeHandled;
    final private Consumer<T> onSuccessConsumer;
    final private AutoCloseable[] resources;

    public Executor(final Supplier<T> valueProvider,
                    final List<IExceptionHandler<T>> exceptionsToBeHandled,
                    final Consumer<T> onSuccessConsumer,
                    final AutoCloseable[] resources) {

      this.valueProvider = valueProvider;
      this.exceptionsToBeHandled = exceptionsToBeHandled;
      this.onSuccessConsumer = onSuccessConsumer;
      this.resources = resources;
    }

    @Override
    public T done() {
      final T value;
      try {
        value = this.valueProvider.get();
      } catch (Throwable raisedException) {
        return handleException(raisedException);
      }
      closeResources(this.resources);
      this.onSuccessConsumer.accept(value);
      return value;
    }

    private T handleException(Throwable raisedException) {
      closeResources(this.resources);
      Optional<IExceptionHandler<T>> first = this.exceptionsToBeHandled.stream()
              .filter(exceptionHandler -> exceptionHandler.getThrowableClass().isInstance(raisedException))
              .findFirst();

      if (first.isPresent()) {
        return first.get().handleException(raisedException);
      }

      throw sneakyThrow(raisedException);
    }

    @Override
    public T finallyDone(final Callable finallyCallable) {
      final T value;
      boolean success = false;
      try {
        value = this.valueProvider.get();
        success = true;
      } catch (Throwable raisedException) {
        return handleException(raisedException);
      } finally {
        if (success) {
          closeResources(this.resources);
        }
        executeCallable(finallyCallable);
      }
      this.onSuccessConsumer.accept(value);
      return value;
    }

    @Override
    public IExecutor<T> elseCall(final Consumer<T> onSuccessConsumer) {
      return new Executor<T>(
              this.valueProvider,
              this.exceptionsToBeHandled,
              onSuccessConsumer,
              this.resources);
    }

    @SuppressWarnings("unchecked")
    public ThenHandler<T> elseIfRaises(final Class<? extends Throwable>... exceptionsToBeHandled) {
      return new ThenHandler<>(
              this.valueProvider,
              asList(exceptionsToBeHandled),
              this.exceptionsToBeHandled,
              this.resources);
    }
  }
}