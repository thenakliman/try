package com.thenakliman.tries;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.thenakliman.tries.Constant.DO_NOTHING;
import static com.thenakliman.tries.SneakyThrower.sneakyThrow;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

class TryToCall {
  final private Callable callable;

  TryToCall(final Callable callable) {
    this.callable = callable;
  }

  <X extends Throwable> ThenHandler acceptException(final Class<? extends X> exceptionClass) throws X {
    return new ThenHandler(this.callable, singletonList(exceptionClass), emptyList());
  }

  <X extends Throwable, Y extends Throwable> ThenHandler acceptException(final Class<? extends X> exceptionClass1,
                                                    final Class<? extends Y> exceptionClass2) throws X, Y {
    return new ThenHandler(this.callable, Arrays.asList(exceptionClass1, exceptionClass2), emptyList());
  }

  <X extends Throwable> ThenHandler acceptException(final Class<? extends X> exceptionClass1,
                                                    final Class<? extends X> exceptionClass2,
                                                    final Class<? extends X> exceptionClass3) throws X {

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
      final List<IExceptionHandler> exceptionHandlers = this.exceptionClasses.stream()
              .map(exceptionClass -> new ExceptionConsumer(exceptionClass, thenConsumer))
              .collect(Collectors.toList());
      exceptionHandlers.addAll(this.exceptionsHandlers);
      return new Executor(this.callable, Collections.unmodifiableList(exceptionHandlers), DO_NOTHING);
    }

    public Executor thenRethrow(final Function<Throwable, ? extends Throwable> exceptionFunction) {
      final List<IExceptionHandler> exceptionHandlers = this.exceptionClasses.stream()
              .map(exceptionClass -> new ExceptionThrower(exceptionClass, exceptionFunction))
              .collect(Collectors.toList());
      exceptionHandlers.addAll(this.exceptionsHandlers);
      return new Executor(this.callable, Collections.unmodifiableList(exceptionHandlers), DO_NOTHING);
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

  public static class Executor {
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

    public void done() {
      boolean success = false;
      try {
        this.callable.call();
        success = true;
      } catch (Throwable exception) {
        Optional<IExceptionHandler> matchedException = this.exceptions.stream()
                .filter((exceptionHandler -> exceptionHandler.getThrowableClass().isInstance(exception)))
                .findFirst();

        if (matchedException.isPresent()) {
          matchedException.get().handleException(exception);
        } else {
          throw exception;
        }
      }

      if (success) {
        this.elseCallable.call();
      }
    }

    public void finallyDone(final Callable finallyCallable) {
      boolean success = false;
      try {
        this.callable.call();
        success = true;
      } catch (Throwable exception) {
        Optional<IExceptionHandler> matchedException = this.exceptions.stream()
                .filter((exceptionHandler -> exceptionHandler.getThrowableClass().isInstance(exception)))
                .findFirst();

        if (matchedException.isPresent()) {
          matchedException.get().handleException(exception);
        } else {
          throw exception;
        }
      } finally {
        finallyCallable.call();
      }

      if (success) {
        this.elseCallable.call();
      }
    }

    public <X extends Throwable> ThenHandler acceptException(final Class<? extends X> exceptionClass) throws X {
      return new ThenHandler(this.callable, singletonList(exceptionClass), this.exceptions);
    }

    public <X extends Throwable> ThenHandler acceptException(final Class<? extends X> exceptionClass1,
                                                             final Class<? extends X> exceptionClass2,
                                                             final Class<? extends X> exceptionClass3) throws X {
      return new ThenHandler(
              this.callable,
              Arrays.asList(exceptionClass1, exceptionClass2, exceptionClass3),
              this.exceptions);
    }

    public <X extends Throwable> ThenHandler acceptException(final Class<? extends X> exceptionClass1,
                                                             final Class<? extends X> exceptionClass2) throws X {
      return new ThenHandler(this.callable, Arrays.asList(exceptionClass1, exceptionClass2), this.exceptions);
    }

    public Executor elseCall(final Callable elseCallable) {
      return new Executor(this.callable, this.exceptions, elseCallable);
    }
  }
}
