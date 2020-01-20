package com.thenakliman.tries;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.awt.*;
import java.awt.geom.IllegalPathStateException;
import java.nio.channels.IllegalSelectorException;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class TryToGetTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void try_toGetThenGet_returnGetValue_whenExceptionIsNotRaised() {
    Integer done = Try.toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(10));
  }

  @Test
  public void try_toGetThenGet_returnGetValue_whenExceptionIsNotRaised_whenTwoExceptionsAreHandled() {
    Integer done = Try.toGet(() -> 10)
            .ifRaises(IllegalArgumentException.class, IllegalSelectorException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(10));
  }

  @Test
  public void try_toGetThenGet_returnGetValue_whenExceptionIsNotRaised_whenThreeExceptionsAreHandled() {
    Integer done = Try.toGet(() -> 10)
            .ifRaises(IllegalArgumentException.class, IllegalSelectorException.class, IllegalThreadStateException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(10));
  }

  @Test
  public void try_toGetThenGetElseCall_returnGetValueElseCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer expValue = Try.toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    verify(testHelper).elseCallMe();
    assertThat(expValue, is(10));
  }

  @Test
  public void try_toGetThenGetElseCall_returnGetValueElseCalled_whenExceptionIsNotRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer expValue = Try.toGet(() -> 10)
            .ifRaises(IllegalThreadStateException.class, IllegalPathStateException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    verify(testHelper).elseCallMe();
    assertThat(expValue, is(10));
  }

  @Test
  public void try_toGetThenGetElseCall_returnGetValueElseCalled_whenExceptionIsNotRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer expValue = Try.toGet(() -> 10)
            .ifRaises(IllegalThreadStateException.class, IllegalPathStateException.class, IllegalComponentStateException.class)
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
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenGetFinallyDone_returnGetValueFinallyCalled_whenExceptionIsNotRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenGetFinallyDone_returnGetValueFinallyCalled_whenExceptionIsNotRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenGetElseCallFinallyDone_returnGetValueFinallyCalledElseCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_toGetThenGetElseCallFinallyDone_returnGetValueFinallyCalledElseCalled_whenExceptionIsNotRaised_whenTwoExceptionsAreHandled() throws IllegalAccessException {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.toGet(() -> 10)
            .ifRaises(IllegalComponentStateException.class, IllegalAccessException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_toGetThenGetElseCallFinallyDone_returnGetValueFinallyCalledElseCalled_whenExceptionIsNotRaised_whenThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.toGet(() -> 10)
            .ifRaises(IllegalComponentStateException.class, IllegalAccessException.class, IllegalSelectorException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_toGetThenGet_returnThenValue_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalArgumentException.class);
    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(20));
  }

  @Test
  public void try_toGetThenGet_returnThenValue_whenFirstExceptionIsRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalArgumentException.class);

    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(20));
  }

  @Test
  public void try_toGetThenGet_returnThenValue_whenSecondExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalStateException.class);

    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalPathStateException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(20));
  }

  @Test
  public void try_toGetThenGet_returnThenValue_whenThirdExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalPathStateException.class);

    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalPathStateException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(20));
  }

  @Test
  public void try_toGetThenGetElseCall_returnThenValue_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalArgumentException.class);

    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    assertThat(done, is(20));
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toGetThenGetElseCall_returnThenValue_whenExceptionFirstIsRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalArgumentException.class);
    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalArgumentException.class, IllegalPathStateException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    assertThat(done, is(20));
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toGetThenGetElseCall_returnThenValue_whenExceptionSecondIsRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalPathStateException.class);
    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalArgumentException.class, IllegalPathStateException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    assertThat(done, is(20));
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toGetThenGetElseCall_returnThenValue_whenExceptionSecondIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalPathStateException.class);
    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalArgumentException.class, IllegalPathStateException.class, IllegalMonitorStateException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    assertThat(done, is(20));
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toGetThenGetElseCall_returnThenValue_whenExceptionThirdIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalMonitorStateException.class);
    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalArgumentException.class, IllegalPathStateException.class, IllegalMonitorStateException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    assertThat(done, is(20));
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toGetThenGetFinallyCalled_returnThenValueFinallyCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalArgumentException.class);
    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenGetFinallyCalled_returnThenValueFinallyCalled_whenFirstExceptionIsRaised_whenTwoExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalArgumentException.class);
    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalArgumentException.class, IllegalSelectorException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenGetFinallyCalled_returnThenValueFinallyCalled_whenSecondExceptionIsRaised_whenTwoExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalArgumentException.class);
    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalArgumentException.class, IllegalSelectorException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenGetFinallyCalled_returnThenValueFinallyCalled_whenSecondExceptionIsRaised_whenThreeExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalSelectorException.class);
    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalArgumentException.class, IllegalSelectorException.class, IllegalComponentStateException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenGetElseCallFinallyCall_returnThenValueFinallyCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalArgumentException.class);
    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .elseCall(value -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toGetThenGetElseCallFinallyCall_returnThenValueFinallyCalled_whenFirstExceptionIsRaised_whenTwoExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalMonitorStateException.class);
    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalMonitorStateException.class, IllegalStateException.class)
            .thenGet((exception) -> 20)
            .elseCall(value -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toGetThenGetElseCallFinallyCall_returnThenValueFinallyCalled_whenSecondExceptionIsRaised_whenTwoExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(new IllegalStateException("illegal "));
    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalMonitorStateException.class, IllegalStateException.class)
            .thenGet((exception) -> 20)
            .elseCall(value -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toGetThenGetElseCallFinallyCall_returnThenValueFinallyCalled_whenSecondExceptionIsRaised_whenThreeExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalStateException.class);
    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalMonitorStateException.class, IllegalStateException.class, IllegalArgumentException.class)
            .thenGet((exception) -> 20)
            .elseCall(value -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toGetThenGetElseCallFinallyCall_returnThenValueFinallyCalled_whenThirdExceptionIsRaised_whenThreeExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(IllegalArgumentException.class);
    Integer done = Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalMonitorStateException.class, IllegalStateException.class, IllegalArgumentException.class)
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
            .ifRaises(RuntimeException.class)
            .thenRethrow((exception) -> new IllegalArgumentException(""))
            .done();
    assertThat(done, is(10));
  }

  @Test
  public void try_toGetThenRethrow_returnGetValue_whenExceptionIsNotRaised_whenTwoExceptionsAreHandled() {
    Integer done = Try.toGet(() -> 10)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenRethrow((exception) -> new IllegalArgumentException(""))
            .done();
    assertThat(done, is(10));
  }

  @Test
  public void try_toGetThenRethrow_returnGetValue_whenExceptionIsNotRaised_whenThreeExceptionsAreHandled() {
    Integer done = Try.toGet(() -> 10)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalPathStateException.class)
            .thenRethrow((exception) -> new IllegalArgumentException(""))
            .done();
    assertThat(done, is(10));
  }

  @Test
  public void try_toGetThenRethrowFinallyDone_returnGetValueFinallyCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenRethrow((exception) -> new IllegalArgumentException(""))
            .finallyDone(testHelper::finallCallMe);

    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenRethrowFinallyDone_returnGetValueFinallyCalled_whenExceptionIsNotRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenRethrow((exception) -> new IllegalArgumentException(""))
            .finallyDone(testHelper::finallCallMe);

    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenRethrowFinallyDone_returnGetValueFinallyCalled_whenExceptionIsNotRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenRethrow((exception) -> new IllegalArgumentException(""))
            .finallyDone(testHelper::finallCallMe);

    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenThrow_throwException_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(new IllegalArgumentException("illegal "));
    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("illegal run time");
    Try.toGet(testHelper::thenGet)
            .ifRaises(RuntimeException.class)
            .thenRethrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
            .done();
  }

  @Test
  public void try_toGetThenThrow_throwException_whenExceptionIsRaised_whenTwoExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(new IllegalArgumentException("illegal"));

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("illegal run time");
    Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenRethrow((exception) -> new RuntimeException(exception.getMessage() + " run time"))
            .done();
  }

  @Test
  public void try_toGetThenThrow_throwException_whenExceptionIsRaised_whenThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(new IllegalArgumentException("illegal"));

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("illegal run time");
    Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalAccessException.class)
            .thenRethrow((exception) -> new RuntimeException(exception.getMessage() + " run time"))
            .done();
  }

  @Test
  public void try_toGetThenThrow_throwException_whenSecondExceptionIsRaised_whenThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(new IllegalArgumentException("illegal "));

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("illegal run time");
    Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalAccessException.class)
            .thenRethrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
            .done();
  }

  @Test
  public void try_toGetThenThrow_throwException_whenThirdExceptionIsRaised_whenThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(new IllegalArgumentException("illegal "));

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("illegal run time");
    Try.toGet(testHelper::thenGet)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalMonitorStateException.class)
            .thenRethrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
            .done();
  }

  @Test
  public void try_toGetThenThrowFinallyDone_throwExceptionFinallyCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(new IllegalArgumentException("illegal "));

    try {
      Try.toGet(testHelper::thenGet)
              .ifRaises(RuntimeException.class)
              .thenRethrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected RuntimeException.");
    } catch (RuntimeException exception) {
      assertThat(exception.getMessage(), is("illegal run time"));
    }

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenThrowFinallyDone_throwExceptionFinallyCalled_whenExceptionIsRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(new IllegalArgumentException("illegal "));
    try {
      Try.toGet(testHelper::thenGet)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenRethrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected RuntimeException.");
    } catch (RuntimeException exception) {
      assertThat(exception.getMessage(), is("illegal run time"));
    }

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenThrowFinallyDone_throwExceptionFinallyCalled_whenExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(new IllegalArgumentException("illegal "));
    try {
      Try.toGet(testHelper::thenGet)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalMonitorStateException.class)
              .thenRethrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected RuntimeException.");
    } catch (RuntimeException exception) {
      assertThat(exception.getMessage(), is("illegal run time"));
    }

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenThrowFinallyDone_throwExceptionFinallyCalled_whenSecondExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(new IllegalArgumentException("illegal "));
    try {
      Try.toGet(testHelper::thenGet)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalMonitorStateException.class)
              .thenRethrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected RuntimeException.");
    } catch (RuntimeException exception) {
      assertThat(exception.getMessage(), is("illegal run time"));
    }

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toGetThenThrowFinallyDone_throwExceptionFinallyCalled_whenThirdExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet()).thenThrow(new IllegalArgumentException("illegal "));

    try {
      Try.toGet(testHelper::thenGet)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalMonitorStateException.class)
              .thenRethrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected RuntimeException.");
    } catch (RuntimeException exception) {
      assertThat(exception.getMessage(), is("illegal run time"));
    }

    verify(testHelper).finallCallMe();
  }

  static class TestHelper {
    Integer thenGet() {
      return 10;
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