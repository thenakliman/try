package com.thenakliman.tries;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.thenakliman.tries.Constant.DO_NOTHING;
import static com.thenakliman.tries.SneakyThrower.sneakyThrow;
import static com.thenakliman.tries.Utils.closeResources;
import static com.thenakliman.tries.Utils.executeCallable;
import static java.util.Collections.emptyList;

class TryToCall {
  final private Callable callable;
  final private AutoCloseable[] resources;

  TryToCall(final Callable callable) {
    this.callable = callable;
    this.resources = new AutoCloseable[0];
  }

  TryToCall(final Callable callable, final AutoCloseable[] resources) {
    this.callable = callable;
    this.resources = resources;
  }

  @SuppressWarnings("unchecked")
  ThenHandler ifRaises(final Class<? extends Throwable>... exceptionClasses) {
    return new ThenHandler(
            this.callable,
            Arrays.stream(exceptionClasses).collect(Collectors.toList()),
            emptyList(),
            this.resources);
  }

  public static class ThenHandler {
    final private Callable callable;
    final private List<Class<? extends Throwable>> exceptionClasses;
    final private List<IExceptionHandler> exceptionsHandlers;
    final private AutoCloseable[] resources;

    public ThenHandler(final Callable callable,
                       final List<Class<? extends Throwable>> exceptionClasses,
                       final List<IExceptionHandler> exceptionsHandlers,
                       final AutoCloseable[] resources) {
      this.callable = callable;
      this.exceptionClasses = exceptionClasses;
      this.exceptionsHandlers = exceptionsHandlers;
      this.resources = resources;
    }

    public Executor thenCall(final Consumer<Throwable> thenConsumer) {
      final ArrayList<IExceptionHandler> registeredExceptionHandlers = new ArrayList<>(this.exceptionsHandlers);
      final List<IExceptionHandler> exceptionHandlers = this.exceptionClasses.stream()
              .map(exceptionClass -> new ExceptionConsumer(exceptionClass, thenConsumer))
              .collect(Collectors.toList());
      registeredExceptionHandlers.addAll(exceptionHandlers);
      return new Executor(
              this.callable,
              Collections.unmodifiableList(registeredExceptionHandlers),
              DO_NOTHING,
              this.resources);
    }

    public <E extends Throwable> Executor thenThrow(final Function<Throwable, ? extends E> exceptionFunction) throws E {
      final ArrayList<IExceptionHandler> registeredExceptionHandlers = new ArrayList<>(this.exceptionsHandlers);
      final List<IExceptionHandler> exceptionHandlers = this.exceptionClasses.stream()
              .map(exceptionClass -> new ExceptionThrower(exceptionClass, exceptionFunction))
              .collect(Collectors.toList());
      registeredExceptionHandlers.addAll(exceptionHandlers);
      return new Executor(
              this.callable,
              Collections.unmodifiableList(registeredExceptionHandlers),
              DO_NOTHING,
              this.resources);
    }
  }

  interface IExceptionHandler {
    void handleException(Throwable exception);

    Class<? extends Throwable> getThrowableClass();
  }

  static class ExceptionConsumer implements IExceptionHandler {
    final private Class<? extends Throwable> throwableClass;
    final private Consumer<Throwable> consumer;

    ExceptionConsumer(Class<? extends Throwable> throwableClass, Consumer<Throwable> consumer) {
      this.throwableClass = throwableClass;
      this.consumer = consumer;
    }

    public Class<? extends Throwable> getThrowableClass() {
      return throwableClass;
    }

    @Override
    public void handleException(Throwable exception) {
      this.consumer.accept(exception);
    }
  }

  static class ExceptionThrower implements IExceptionHandler {
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
    public void handleException(final Throwable exception) {
      sneakyThrow(exceptionFunction.apply(exception));
    }
  }

  interface IElseCall {
    IExecutor elseCall(final Callable callable);
  }

  interface IExecutor {
    void done();

    void finallyDone(final Callable callable);
  }

  public static class Executor implements IElseCall, IExecutor {
    final private Callable callable;
    final private List<IExceptionHandler> exceptions;
    final private Callable elseCallable;
    final private AutoCloseable[] resources;

    public Executor(final Callable callable,
                    final List<IExceptionHandler> exceptions,
                    final Callable doNothing,
                    final AutoCloseable[] resources) {
      this.callable = callable;
      this.exceptions = exceptions;
      this.elseCallable = doNothing;
      this.resources = resources;
    }

    @Override
    public void done() {
      boolean success = false;
      try {
        this.callable.call();
        success = true;
      } catch (Throwable exception) {
        handleException(exception);
      }

      if (success) {
        closeResources(this.resources);
        executeCallable(this.elseCallable);
      }
    }

    private void handleException(final Throwable exception) {
      closeResources(this.resources);
      final Optional<IExceptionHandler> matchedException = this.exceptions.stream()
              .filter((exceptionHandler -> exceptionHandler.getThrowableClass().isInstance(exception)))
              .findFirst();
      if (matchedException.isPresent()) {
        matchedException.get().handleException(exception);
      } else {
        throw sneakyThrow(exception);
      }
    }

    @Override
    public void finallyDone(final Callable finallyCallable) {
      boolean success = false;
      try {
        this.callable.call();
        success = true;
      } catch (Throwable exception) {
        handleException(exception);
      } finally {
        if (success) {
          closeResources(this.resources);
        }
        executeCallable(finallyCallable);
      }

      if (success) {
        executeCallable(this.elseCallable);
      }
    }

    @SuppressWarnings("unchecked")
    public ThenHandler elseIfRaises(final Class<? extends Throwable>... exceptionClasses) {
      return new ThenHandler(
              this.callable,
              Arrays.stream(exceptionClasses).collect(Collectors.toList()),
              this.exceptions,
              this.resources);
    }

    @Override
    public IExecutor elseCall(final Callable elseCallable) {
      return new Executor(this.callable, this.exceptions, elseCallable, this.resources);
    }
  }
}
