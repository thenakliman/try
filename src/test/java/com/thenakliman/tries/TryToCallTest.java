package com.thenakliman.tries;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.function.Supplier;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class TryToCallTest {

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