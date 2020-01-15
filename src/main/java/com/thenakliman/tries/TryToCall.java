package com.thenakliman.tries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.thenakliman.tries.Constant.DO_NOTHING;
import static com.thenakliman.tries.SneakyThrower.sneakyThrow;
import static java.util.Collections.emptyList;

class TryToCall {
  final private Callable callable;

  TryToCall(final Callable callable) {
    this.callable = callable;
  }

  <X extends Throwable> ThenHandler acceptException(final Class<? extends X> exceptionClass) throws X {
    return new ThenHandler(this.callable, exceptionClass, emptyList());
  }

  public static class ThenHandler {
    final private Callable callable;
    final private Class<? extends Throwable> exceptionClass;
    final private List<IExceptionHandler> exceptionsHandler;

    public ThenHandler(final Callable callable,
                       final Class<? extends Throwable> exceptionClass,
                       final List<IExceptionHandler> exceptionsHandler) {
      this.callable = callable;
      this.exceptionClass = exceptionClass;
      this.exceptionsHandler = exceptionsHandler;
    }

    public ITerminator thenCall(final Consumer<Throwable> thenConsumer) {
      final ArrayList<IExceptionHandler> exceptionHandlers = new ArrayList<>(this.exceptionsHandler);
      exceptionHandlers.add(new ExceptionConsumer(this.exceptionClass, thenConsumer));
      return new Terminator(this.callable, Collections.unmodifiableList(exceptionHandlers), DO_NOTHING);
    }

    public ITerminator thenRethrow(final Function<Throwable, ? extends Throwable> exceptionFunction) {
      final ArrayList<IExceptionHandler> IExceptionHandlers = new ArrayList<>(this.exceptionsHandler);
      IExceptionHandlers.add(new ExceptionThrower(this.exceptionClass, exceptionFunction));
      return new Terminator(this.callable, Collections.unmodifiableList(IExceptionHandlers), DO_NOTHING);
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

  interface ITerminator {
    void done();

    void finallyDone(final Callable callable);

    <X extends Throwable> ThenHandler acceptException(final Class<? extends X> exceptionClass) throws X;

    ITerminator elseCall(final Callable elseCallable);
  }

  public static class Terminator implements ITerminator {
    final private Callable callable;
    final private List<IExceptionHandler> exceptions;
    final private Callable elseCallable;

    public Terminator(final Callable callable,
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

    @Override
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

    @Override
    public <X extends Throwable> ThenHandler acceptException(final Class<? extends X> exceptionClass) throws X {
      return new ThenHandler(this.callable, exceptionClass, this.exceptions);
    }

    @Override
    public ITerminator elseCall(final Callable elseCallable) {
      return new Terminator(this.callable, this.exceptions, elseCallable);
    }
  }
}
