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

  <X extends Throwable> CodeWithThrowable acceptException(final Class<? extends X> exceptionClass) throws X {
    return new CodeWithThrowable(this.callable, exceptionClass, emptyList());
  }

  public static class CodeWithThrowable {
    final private Callable callable;
    final private Class<? extends Throwable> exceptionClass;
    final private List<Keeper> exceptionKeepers;

    public CodeWithThrowable(final Callable callable, final Class<? extends Throwable> exceptionClass, List<Keeper> exceptionKeepers) {
      this.callable = callable;
      this.exceptionClass = exceptionClass;
      this.exceptionKeepers = exceptionKeepers;
    }

    public IExceptionHandler thenCall(final Consumer<Throwable> thenConsumer) {
      ArrayList<Keeper> exceptionKeepers = new ArrayList<>(this.exceptionKeepers);
      exceptionKeepers.add(new ExceptionKeeper(this.exceptionClass, thenConsumer));
      return new ExceptionHandler(this.callable, Collections.unmodifiableList(exceptionKeepers), DO_NOTHING);
    }

    public IExceptionHandler thenRethrow(final Function<Throwable, ? extends Throwable> exceptionFunction) {
      ArrayList<Keeper> exceptionFKeepers = new ArrayList<>(this.exceptionKeepers);
      exceptionFKeepers.add(new ExceptionFKeeper(this.exceptionClass, exceptionFunction));
      return new RethrowExceptionHandler(this.callable, Collections.unmodifiableList(exceptionFKeepers), DO_NOTHING);
    }
  }

  static interface Keeper {
    String getType();

    public Class<? extends Throwable> getThrowableClass();
  }

  static class ExceptionKeeper implements Keeper {
    final private Class<? extends Throwable> throwableClass;
    final private Consumer<Throwable> consumer;

    ExceptionKeeper(Class<? extends Throwable> throwableClass, Consumer<Throwable> consumer) {
      this.throwableClass = throwableClass;
      this.consumer = consumer;
    }

    public Class<? extends Throwable> getThrowableClass() {
      return throwableClass;
    }

    public Consumer<Throwable> getConsumer() {
      return consumer;
    }

    @Override
    public String getType() {
      return "consumer";
    }
  }

  static class ExceptionFKeeper implements Keeper {
    final private Class<? extends Throwable> throwableClass;
    final Function<Throwable, ? extends Throwable> exceptionFunction;

    ExceptionFKeeper(final Class<? extends Throwable> throwableClass, final Function<Throwable, ? extends Throwable> exceptionFunction) {
      this.throwableClass = throwableClass;
      this.exceptionFunction = exceptionFunction;
    }

    public Class<? extends Throwable> getThrowableClass() {
      return throwableClass;
    }

    public Function<Throwable, ? extends Throwable> getExceptionFunction() {
      return this.exceptionFunction;
    }

    @Override
    public String getType() {
      return "thrower";
    }
  }

  interface IExceptionHandler {
    void done();

    void finallyDone(final Callable callable);

    <X extends Throwable> CodeWithThrowable acceptException(final Class<? extends X> exceptionClass) throws X;

    IExceptionHandler elseCall(final Callable elseCallable);
  }

  public static class RethrowExceptionHandler implements IExceptionHandler {
    final private Callable callable;
    final private Callable elseCallable;
    final private List<Keeper> exceptionKeeper;

    public RethrowExceptionHandler(final Callable callable, List<Keeper> exceptionKeeper, final Callable elseCallable) {
      this.callable = callable;
      this.elseCallable = elseCallable;
      this.exceptionKeeper = exceptionKeeper;
    }

    @Override
    public void done() {
      try {
        this.callable.call();
      } catch (Throwable exception) {
        Optional<Keeper> exceptionKeeperOptional = this.exceptionKeeper.stream()
                .filter((exceptionKeeper -> exceptionKeeper.getThrowableClass().isInstance(exception)))
                .findFirst();
        if (exceptionKeeperOptional.isPresent() && "thrower".equals(exceptionKeeperOptional.get().getType())) {
          sneakyThrow(((ExceptionFKeeper) exceptionKeeperOptional.get()).exceptionFunction.apply(exception));
        } else if (exceptionKeeperOptional.isPresent() && "consumer".equals(exceptionKeeperOptional.get().getType())) {
          ((ExceptionKeeper) exceptionKeeperOptional.get()).getConsumer().accept(exception);
        } else {
          throw exception;
        }
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
        Optional<Keeper> exceptionKeeperOptional = this.exceptionKeeper.stream()
                .filter((exceptionKeeper -> exceptionKeeper.getThrowableClass().isInstance(exception)))
                .findFirst();
        if (exceptionKeeperOptional.isPresent() && "thrower".equals(exceptionKeeperOptional.get().getType())) {
          sneakyThrow(((ExceptionFKeeper) exceptionKeeperOptional.get()).exceptionFunction.apply(exception));
        } else if (exceptionKeeperOptional.isPresent() && "consumer".equals(exceptionKeeperOptional.get().getType())) {
          ((ExceptionKeeper) exceptionKeeperOptional.get()).getConsumer().accept(exception);
        } else {
          throw exception;
        }
      } finally {
        finallyCallable.call();
      }
      if(success) {
        this.elseCallable.call();
      }
    }

    @Override
    public <X extends Throwable> CodeWithThrowable acceptException(Class<? extends X> exceptionClass) throws X {
      return new CodeWithThrowable(this.callable, exceptionClass, this.exceptionKeeper);
    }

    @Override
    public IExceptionHandler elseCall(final Callable elseCallable) {
      return new RethrowExceptionHandler(this.callable, this.exceptionKeeper, elseCallable);
    }
  }

  public static class ExceptionHandler implements IExceptionHandler {
    final private Callable callable;
    final private List<Keeper> exceptionKeeper;
    final private Callable elseCallable;

    public ExceptionHandler(final Callable callable, final List<Keeper> exceptionKeeper, final Callable doNothing) {
      this.callable = callable;
      this.exceptionKeeper = exceptionKeeper;
      this.elseCallable = doNothing;
    }

    @Override
    public void done() {
      boolean success = false;
      try {
        this.callable.call();
        success = true;
      } catch (Throwable exception) {
        Optional<Keeper> exceptionKeeperOptional = this.exceptionKeeper.stream()
                .filter((exceptionKeeper -> exceptionKeeper.getThrowableClass().isInstance(exception)))
                .findFirst();
        if (exceptionKeeperOptional.isPresent() && "thrower".equals(exceptionKeeperOptional.get().getType())) {
          ((ExceptionFKeeper) exceptionKeeperOptional.get()).exceptionFunction.apply(exception);
        } else {
          if (exceptionKeeperOptional.isPresent() && "consumer".equals(exceptionKeeperOptional.get().getType())) {
            ((ExceptionKeeper) exceptionKeeperOptional.get()).getConsumer().accept(exception);
          } else {
            throw exception;
          }
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
        Optional<Keeper> exceptionKeeperOptional = this.exceptionKeeper.stream()
                .filter((exceptionKeeper -> exceptionKeeper.getThrowableClass().isInstance(exception)))
                .findFirst();
        if (exceptionKeeperOptional.isPresent() && "thrower".equals(exceptionKeeperOptional.get().getType())) {
          ((ExceptionFKeeper) exceptionKeeperOptional.get()).exceptionFunction.apply(exception);
        } else {
          if (exceptionKeeperOptional.isPresent() && "consumer".equals(exceptionKeeperOptional.get().getType())) {
            ((ExceptionKeeper) exceptionKeeperOptional.get()).getConsumer().accept(exception);
          } else {
            throw exception;
          }
        }
      } finally {
        finallyCallable.call();
      }

      if (success) {
        this.elseCallable.call();
      }
    }

    @Override
    public <X extends Throwable> CodeWithThrowable acceptException(Class<? extends X> exceptionClass) throws X {
      return new CodeWithThrowable(this.callable, exceptionClass, this.exceptionKeeper);
    }

    @Override
    public IExceptionHandler elseCall(final Callable elseCallable) {
      return new ExceptionHandler(this.callable, this.exceptionKeeper, elseCallable);
    }
  }
}
