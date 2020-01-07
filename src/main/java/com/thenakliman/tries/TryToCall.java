package com.thenakliman.tries;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.thenakliman.tries.Constant.DO_NOTHING;

class TryToCall {
  final private Callable thenCallable;

  TryToCall(final Callable thenCallable) {
    this.thenCallable = thenCallable;
  }

  CodeWithRuntimeException acceptRuntimeException(final Class<? extends RuntimeException> exceptionClass) {
    return new CodeWithRuntimeException(this.thenCallable, exceptionClass);
  }

  public static class CodeWithRuntimeException {
    final private Callable thenCallable;
    final private Class<? extends RuntimeException> exceptionClass;

    public CodeWithRuntimeException(final Callable thenCallable, final Class<? extends RuntimeException> exceptionClass) {
      this.thenCallable = thenCallable;
      this.exceptionClass = exceptionClass;
    }

    public IExceptionHandler thenCall(final Consumer<RuntimeException> exceptionConsumer) {
      return new ExceptionHandler(this.thenCallable, exceptionClass, exceptionConsumer, DO_NOTHING);
    }

    public IExceptionHandler thenRethrow(final Function<RuntimeException, ? extends RuntimeException> exceptionFunction) {
      return new RethrowExceptionHandler(this.thenCallable, this.exceptionClass, exceptionFunction, DO_NOTHING);
    }
  }

  interface IExceptionHandler {
    void done();

    void finallyDone(final Callable callable);

    IExceptionHandler elseCall(final Callable elseCallable);
  }

  public static class RethrowExceptionHandler implements IExceptionHandler {
    final private Callable thenCallable;
    final private Class<? extends RuntimeException> exceptionClass;
    final private Function<RuntimeException, ? extends RuntimeException> exceptionFunction;
    final private Callable elseCallable;

    public RethrowExceptionHandler(final Callable thenCallable,
                                   final Class<? extends RuntimeException> exceptionClass,
                                   final Function<RuntimeException, ? extends RuntimeException> exceptionFunction,
                                   final Callable elseCallable) {

      this.thenCallable = thenCallable;
      this.exceptionClass = exceptionClass;
      this.exceptionFunction = exceptionFunction;
      this.elseCallable = elseCallable;
    }

    @Override
    public void done() {
      boolean success;
      try {
        this.thenCallable.call();
        success = true;
      } catch (RuntimeException exception) {
        if (this.exceptionClass.isInstance(exception)) {
          throw this.exceptionFunction.apply(exception);
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
      boolean success;
      try {
        this.thenCallable.call();
        success = true;
      } catch (RuntimeException exception) {
        if (this.exceptionClass.isInstance(exception)) {
          throw this.exceptionFunction.apply(exception);
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
              this.thenCallable,
              this.exceptionClass,
              this.exceptionFunction,
              elseCallable);
    }
  }

  public static class ExceptionHandler implements IExceptionHandler {
    final private Callable thenCallable;
    final private Class<? extends RuntimeException> exceptionClass;
    final private Consumer<RuntimeException> exceptionConsumer;
    final private Callable elseCallable;

    public ExceptionHandler(final Callable thenCallable,
                            final Class<? extends RuntimeException> exceptionClass,
                            final Consumer<RuntimeException> exceptionConsumer,
                            final Callable elseCallable) {

      this.thenCallable = thenCallable;
      this.exceptionClass = exceptionClass;
      this.exceptionConsumer = exceptionConsumer;
      this.elseCallable = elseCallable;
    }

    @Override
    public void done() {
      boolean success = false;
      try {
        this.thenCallable.call();
        success = true;
      } catch (RuntimeException exception) {
        if (this.exceptionClass.isInstance(exception)) {
          this.exceptionConsumer.accept(exception);
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
        this.thenCallable.call();
        success = true;
      } catch (RuntimeException exception) {
        if (this.exceptionClass.isInstance(exception)) {
          this.exceptionConsumer.accept(exception);
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
      return new ExceptionHandler(this.thenCallable, this.exceptionClass, this.exceptionConsumer, elseCallable);
    }
  }
}
