package com.thenakliman.tries;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.thenakliman.tries.Constant.DO_NOTHING;
import static com.thenakliman.tries.SneakyThrower.sneakyThrow;

class TryToCall {
  final private Callable callable;

  TryToCall(final Callable callable) {
    this.callable = callable;
  }

  <X extends Throwable> CodeWithThrowable acceptException(final Class<? extends X> exceptionClass) throws X {
    return new CodeWithThrowable(this.callable, exceptionClass);
  }

  public static class CodeWithThrowable {
    final private Callable callable;
    final private Class<? extends Throwable> exceptionClass;

    public CodeWithThrowable(final Callable callable, final Class<? extends Throwable> exceptionClass) {
      this.callable = callable;
      this.exceptionClass = exceptionClass;
    }

    public IExceptionHandler thenCall(final Consumer<Throwable> thenCallable) {
      return new ExceptionHandler(this.callable, exceptionClass, thenCallable, DO_NOTHING);
    }

    public IExceptionHandler thenRethrow(final Function<Throwable, ? extends Throwable> exceptionFunction) {
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
    final private Class<? extends Throwable> exceptionClass;
    final private Function<Throwable, ? extends Throwable> thenFunction;
    final private Callable elseCallable;

    public RethrowExceptionHandler(final Callable callable,
                                   final Class<? extends Throwable> exceptionClass,
                                   final Function<Throwable, ? extends Throwable> thenFunction,
                                   final Callable elseCallable) {

      this.callable = callable;
      this.exceptionClass = exceptionClass;
      this.thenFunction = thenFunction;
      this.elseCallable = elseCallable;
    }

    @Override
    public void done() {
      try {
        this.callable.call();
      } catch (Throwable exception) {
        if (this.exceptionClass.isInstance(exception)) {
          sneakyThrow(this.thenFunction.apply(exception));
        } else {
          throw exception;
        }
      }

      this.elseCallable.call();
    }

    @Override
    public void finallyDone(final Callable finallyCallable) {
      try {
        this.callable.call();
      } catch (Throwable exception) {
        if (this.exceptionClass.isInstance(exception)) {
          sneakyThrow(this.thenFunction.apply(exception));
        } else {
          throw exception;
        }
      } finally {
        finallyCallable.call();
      }

      this.elseCallable.call();
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
    final private Class<? extends Throwable> exceptionClass;
    final private Consumer<Throwable> thenConsumer;
    final private Callable elseCallable;

    public ExceptionHandler(final Callable callable,
                            final Class<? extends Throwable> exceptionClass,
                            final Consumer<Throwable> thenConsumer,
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
      } catch (Throwable exception) {
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
      } catch (Throwable exception) {
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
