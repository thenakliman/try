package com.thenakliman.tries;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.thenakliman.tries.Constant.DO_NOTHING;

class TryToCall {
  final private Callable callable;

  TryToCall(final Callable callable) {
    this.callable = callable;
  }

  CodeWithRuntimeException acceptRuntimeException(final Class<? extends RuntimeException> exceptionClass) {
    return new CodeWithRuntimeException(this.callable, exceptionClass);
  }

  public static class CodeWithRuntimeException {
    final private Callable callable;
    final private Class<? extends RuntimeException> exceptionClass;

    public CodeWithRuntimeException(final Callable callable, final Class<? extends RuntimeException> exceptionClass) {
      this.callable = callable;
      this.exceptionClass = exceptionClass;
    }

    public IExceptionHandler thenCall(final Consumer<RuntimeException> thenCallable) {
      return new ExceptionHandler(this.callable, exceptionClass, thenCallable, DO_NOTHING);
    }

    public IExceptionHandler thenRethrow(final Function<RuntimeException, ? extends RuntimeException> exceptionFunction) {
      return new RethrowExceptionHandler(this.callable, this.exceptionClass, exceptionFunction, DO_NOTHING);
    }
  }

  interface IExceptionHandler {
    void done();

    void finallyDone(final Callable callable);

    IExceptionHandler elseCall(final Callable elseCallable);
  }

  public static class RethrowExceptionHandler implements IExceptionHandler {
    final private Callable callable;
    final private Class<? extends RuntimeException> exceptionClass;
    final private Function<RuntimeException, ? extends RuntimeException> thenFunction;
    final private Callable elseCallable;

    public RethrowExceptionHandler(final Callable callable,
                                   final Class<? extends RuntimeException> exceptionClass,
                                   final Function<RuntimeException, ? extends RuntimeException> thenFunction,
                                   final Callable elseCallable) {

      this.callable = callable;
      this.exceptionClass = exceptionClass;
      this.thenFunction = thenFunction;
      this.elseCallable = elseCallable;
    }

    @Override
    public void done() {
      boolean success = false;
      try {
        this.callable.call();
        success = true;
      } catch (RuntimeException exception) {
        if (this.exceptionClass.isInstance(exception)) {
          throw this.thenFunction.apply(exception);
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
      } catch (RuntimeException exception) {
        if (this.exceptionClass.isInstance(exception)) {
          throw this.thenFunction.apply(exception);
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
    public IExceptionHandler elseCall(final Callable elseCallable) {
      return new RethrowExceptionHandler(
              this.callable,
              this.exceptionClass,
              this.thenFunction,
              elseCallable);
    }
  }

  public static class ExceptionHandler implements IExceptionHandler {
    final private Callable callable;
    final private Class<? extends RuntimeException> exceptionClass;
    final private Consumer<RuntimeException> thenConsumer;
    final private Callable elseCallable;

    public ExceptionHandler(final Callable callable,
                            final Class<? extends RuntimeException> exceptionClass,
                            final Consumer<RuntimeException> thenConsumer,
                            final Callable elseCallable) {

      this.callable = callable;
      this.exceptionClass = exceptionClass;
      this.thenConsumer = thenConsumer;
      this.elseCallable = elseCallable;
    }

    @Override
    public void done() {
      boolean success = false;
      try {
        this.callable.call();
        success = true;
      } catch (RuntimeException exception) {
        if (this.exceptionClass.isInstance(exception)) {
          this.thenConsumer.accept(exception);
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
      } catch (RuntimeException exception) {
        if (this.exceptionClass.isInstance(exception)) {
          this.thenConsumer.accept(exception);
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
    public IExceptionHandler elseCall(final Callable elseCallable) {
      return new ExceptionHandler(this.callable, this.exceptionClass, this.thenConsumer, elseCallable);
    }
  }
}
