package com.thenakliman.tries;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.thenakliman.tries.Constant.DO_NOTHING;
import static com.thenakliman.tries.SneakyThrower.sneakyThrow;
import static com.thenakliman.tries.Utils.executeCallable;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

class TryToCall {
  final private Callable callable;

  TryToCall(final Callable callable) {
    this.callable = callable;
  }

  <X extends Throwable> ThenHandler ifRaises(final Class<? extends X> exceptionClass) throws X {
    return new ThenHandler(this.callable, singletonList(exceptionClass), emptyList());
  }

  <X extends Throwable, Y extends Throwable> ThenHandler ifRaises(final Class<? extends X> exceptionClass1,
                                                                  final Class<? extends Y> exceptionClass2) throws X, Y {
    return new ThenHandler(this.callable, Arrays.asList(exceptionClass1, exceptionClass2), emptyList());
  }

  <X extends Throwable, Y extends Throwable, Z extends Throwable> ThenHandler ifRaises(final Class<? extends X> exceptionClass1,
                                                                                       final Class<? extends Y> exceptionClass2,
                                                                                       final Class<? extends Z> exceptionClass3) throws X, Y, Z {

    return new ThenHandler(this.callable, Arrays.asList(exceptionClass1, exceptionClass2, exceptionClass3), emptyList());
  }

  public static class ThenHandler {
    final private Callable callable;
    final private List<Class<? extends Throwable>> exceptionClasses;
    final private List<IExceptionHandler> exceptionsHandlers;

    public ThenHandler(final Callable callable,
                       final List<Class<? extends Throwable>> exceptionClasses,
                       final List<IExceptionHandler> exceptionsHandlers) {
      this.callable = callable;
      this.exceptionClasses = exceptionClasses;
      this.exceptionsHandlers = exceptionsHandlers;
    }

    public Executor thenCall(final Consumer<Throwable> thenConsumer) {
      final ArrayList<IExceptionHandler> registeredExceptionHandlers = new ArrayList<>(this.exceptionsHandlers);
      final List<IExceptionHandler> exceptionHandlers = this.exceptionClasses.stream()
              .map(exceptionClass -> new ExceptionConsumer(exceptionClass, thenConsumer))
              .collect(Collectors.toList());
      registeredExceptionHandlers.addAll(exceptionHandlers);
      return new Executor(this.callable, Collections.unmodifiableList(registeredExceptionHandlers), DO_NOTHING);
    }

    public Executor thenRethrow(final Function<Throwable, ? extends Throwable> exceptionFunction) {
      final ArrayList<IExceptionHandler> registeredExceptionHandlers = new ArrayList<>(this.exceptionsHandlers);
      final List<IExceptionHandler> exceptionHandlers = this.exceptionClasses.stream()
              .map(exceptionClass -> new ExceptionThrower(exceptionClass, exceptionFunction))
              .collect(Collectors.toList());
      registeredExceptionHandlers.addAll(exceptionHandlers);
      return new Executor(this.callable, Collections.unmodifiableList(registeredExceptionHandlers), DO_NOTHING);
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

    public Executor(final Callable callable,
                    final List<IExceptionHandler> exceptions,
                    final Callable doNothing) {
      this.callable = callable;
      this.exceptions = exceptions;
      this.elseCallable = doNothing;
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
        executeCallable(this.elseCallable);
      }
    }

    private void handleException(Throwable exception) {
      Optional<IExceptionHandler> matchedException = this.exceptions.stream()
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
        executeCallable(finallyCallable);
      }

      if (success) {
        executeCallable(this.elseCallable);
      }
    }

    public <X extends Throwable> ThenHandler elseIfRaises(final Class<? extends X> exceptionClass) throws X {
      return new ThenHandler(this.callable, singletonList(exceptionClass), this.exceptions);
    }

    public <X extends Throwable> ThenHandler elseIfRaises(final Class<? extends X> exceptionClass1,
                                                          final Class<? extends X> exceptionClass2,
                                                          final Class<? extends X> exceptionClass3) throws X {
      return new ThenHandler(
              this.callable,
              Arrays.asList(exceptionClass1, exceptionClass2, exceptionClass3),
              this.exceptions);
    }

    public <X extends Throwable> ThenHandler elseIfRaises(final Class<? extends X> exceptionClass1,
                                                          final Class<? extends X> exceptionClass2) throws X {
      return new ThenHandler(this.callable, Arrays.asList(exceptionClass1, exceptionClass2), this.exceptions);
    }

    @Override
    public IExecutor elseCall(final Callable elseCallable) {
      return new Executor(this.callable, this.exceptions, elseCallable);
    }
  }
}
