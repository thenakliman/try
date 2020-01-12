package com.thenakliman.tries;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.function.Supplier;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class TryTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void try_tryToCallThenCall_thenCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.toCall(testHelper::thenCallMe)
            .acceptException(RuntimeException.class)
            .thenCall((exception) -> testHelper.throwException())
            .done();

    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).throwException();
  }

  @Test
  public void try_tryToCallThenCallElseCall_thenCalledElseCalledFinallyCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.toCall(testHelper::thenCallMe)
            .acceptException(RuntimeException.class)
            .thenCall((exception) -> testHelper.throwException())
            .elseCall(testHelper::elseCallMe)
            .done();

    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).throwException();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_tryToCallThenCallFinallyDone_finallyDoneIsExecuted_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.toCall(testHelper::thenCallMe)
            .acceptException(RuntimeException.class)
            .thenCall((exception) -> testHelper.throwException())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).throwException();
  }

  @Test
  public void try_tryToCallThenCallElseCallFinallyDone_thenCallfinallyDoneElseCall_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.toCall(testHelper::thenCallMe)
            .acceptException(RuntimeException.class)
            .thenCall((exception) -> testHelper.throwException())
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
    verify(testHelper, times(0)).throwException();
  }

  @Test
  public void try_tryToCallThenCall_throwExceptionCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.toCall(testHelper::throwException)
            .acceptException(IllegalArgumentException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .done();

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
  }

  @Test
  public void try_tryToCallThenCallElseCall_throwExceptionCalledElseNotCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.toCall(testHelper::throwException)
            .acceptException(IllegalArgumentException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseCall(testHelper::elseCallMe)
            .done();

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryToCallThenCallFinallyDone_finallyDoneIsCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.toCall(testHelper::throwException)
            .acceptException(IllegalArgumentException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_tryToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.toCall(testHelper::throwException)
            .acceptException(IllegalArgumentException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toCallThenThrow_throwException_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new RuntimeException("some argument ")).when(testHelper).throwException();

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("some argument some value");
    Try.toCall(testHelper::throwException)
            .acceptException(RuntimeException.class)
            .thenRethrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
            .done();
  }

  @Test
  public void try_toCallThenThrowElseCall_throwExceptionElseNotCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new RuntimeException("some argument ")).when(testHelper).throwException();

    try {
      Try.toCall(testHelper::throwException)
              .acceptException(RuntimeException.class)
              .thenRethrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .elseCall(testHelper::elseCallMe)
              .done();
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toCallThenThrowFinallyDone_throwExceptionFinallyCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new RuntimeException("some argument ")).when(testHelper).throwException();

    try {
      Try.toCall(testHelper::throwException)
              .acceptException(RuntimeException.class)
              .thenRethrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }
    verify(testHelper).finallCallMe();
    verify(testHelper).throwException();
  }

  @Test
  public void try_toCallThenThrowElseCallFinallyDone_throwExceptionFinallyCalledElseNotCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new RuntimeException("some argument ")).when(testHelper).throwException();

    try {
      Try.toCall(testHelper::throwException)
              .acceptException(RuntimeException.class)
              .thenRethrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .elseCall(testHelper::elseCallMe)
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }
    verify(testHelper).finallCallMe();
    verify(testHelper).throwException();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toCallThenThrow_throwException_whenChildExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument ")).when(testHelper).throwException();

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("some argument some value");
    Try.toCall(testHelper::throwException)
            .acceptException(RuntimeException.class)
            .thenRethrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
            .done();
  }

  @Test
  public void try_toCallThenThrowThenElse_throwExceptionElseCallNotCalled_whenChildExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument ")).when(testHelper).throwException();

    try {
      Try.toCall(testHelper::throwException)
              .acceptException(RuntimeException.class)
              .thenRethrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .elseCall(testHelper::elseCallMe)
              .done();
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }

    verify(testHelper).throwException();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toCallThenThrowFinallyDone_throwExceptionFinallyCalled_whenChildExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument ")).when(testHelper).throwException();

    try {
      Try.toCall(testHelper::throwException)
              .acceptException(RuntimeException.class)
              .thenRethrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }

    verify(testHelper).throwException();
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toCallThenThrowElseCallFinallyDone_throwExceptionFinallyCalledElseCallNotCalled_whenChildExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument ")).when(testHelper).throwException();

    try {
      Try.toCall(testHelper::throwException)
              .acceptException(RuntimeException.class)
              .thenRethrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .elseCall(testHelper::elseCallMe)
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }

    verify(testHelper).throwException();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toGetThenGet_returnGetValue_whenExceptionIsNotRaised() {
    Integer done = Try.toGet(() -> 10)
            .acceptRuntimeException(RuntimeException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(10));
  }

  @Test
  public void try_toGetThenGetElseCall_returnGetValueElseCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer expValue = Try.toGet(() -> 10)
            .acceptRuntimeException(RuntimeException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    verify(testHelper).elseCallMe();
    assertThat(expValue, is(10));
  }

  @Test
  public void try_toGetThenGetFinallyDone_returnGetValueFinallyCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.toGet(() -> 10)
            .acceptRuntimeException(RuntimeException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenGetElseCallFinallyDone_returnGetValueFinallyCalledElseCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.toGet(() -> 10)
            .acceptRuntimeException(RuntimeException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_toGetThenGet_returnThenValue_whenExceptionIsRaised() {
    Supplier<Integer> supplier = () -> {
      if (true) {
        throw new IllegalArgumentException("");
      }
      return 10;
    };

    Integer done = Try.toGet(supplier)
            .acceptRuntimeException(RuntimeException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(20));
  }

  @Test
  public void try_toGetThenGetElseCall_returnThenValue_whenExceptionIsRaised() {
    Supplier<Integer> supplier = () -> {
      if (true) {
        throw new IllegalArgumentException("");
      }
      return 10;
    };

    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.toGet(supplier)
            .acceptRuntimeException(RuntimeException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    assertThat(done, is(20));
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toGetThenGetFinallyCalled_returnThenValueFinallyCalled_whenExceptionIsRaised() {
    Supplier<Integer> supplier = () -> {
      if (true) {
        throw new IllegalArgumentException("");
      }
      return 10;
    };

    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.toGet(supplier)
            .acceptRuntimeException(RuntimeException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenGetElseCallFinallyCall_returnThenValueFinallyCalled_whenExceptionIsRaised() {
    Supplier<Integer> supplier = () -> {
      if (true) {
        throw new IllegalArgumentException("");
      }
      return 10;
    };

    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.toGet(supplier)
            .acceptRuntimeException(RuntimeException.class)
            .thenGet((exception) -> 20)
            .elseCall(value -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toGetThenRethrow_returnGetValue_whenExceptionIsNotRaised() {
    Integer done = Try.toGet(() -> 10)
            .acceptRuntimeException(RuntimeException.class)
            .thenRethrow((exception) -> new IllegalArgumentException(""))
            .done();
    assertThat(done, is(10));
  }

  @Test
  public void try_toGetThenRethrowFinallyDone_returnGetValueFinallyCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.toGet(() -> 10)
            .acceptRuntimeException(RuntimeException.class)
            .thenRethrow((exception) -> new IllegalArgumentException(""))
            .finallyDone(testHelper::finallCallMe);

    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenThrow_throwException_whenExceptionIsRaised() {
    Supplier<Integer> supplier = () -> {
      if (true) {
        throw new IllegalArgumentException("illegal ");
      }
      return 10;
    };

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("illegal run time");
    Try.toGet(supplier)
            .acceptRuntimeException(RuntimeException.class)
            .thenRethrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
            .done();
  }

  @Test
  public void try_toGetThenThrowFinallyDone_throwExceptionFinallyCalled_whenExceptionIsRaised() {
    Supplier<Integer> supplier = () -> {
      if (true) {
        throw new IllegalArgumentException("illegal ");
      }
      return 10;
    };

    TestHelper testHelper = mock(TestHelper.class);
    try {
      Try.toGet(supplier)
              .acceptRuntimeException(RuntimeException.class)
              .thenRethrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected RuntimeException.");
    } catch (RuntimeException exception) {
      assertThat(exception.getMessage(), is("illegal run time"));
    }

    verify(testHelper).finallCallMe();
  }

  static class TestHelper {
    void thenCallMe() {
      System.out.println("then call me");
    }

    void elseCallMe() {
      System.out.println("then call me");
    }

    void finallCallMe() {
      System.out.println("else call me");
    }

    void throwException() {
      throw new IllegalArgumentException("something illegal happened");
    }
  }
}