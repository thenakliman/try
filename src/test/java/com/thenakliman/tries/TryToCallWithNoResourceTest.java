package com.thenakliman.tries;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.awt.geom.IllegalPathStateException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.IllegalSelectorException;
import java.nio.charset.IllegalCharsetNameException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.IllegalFormatException;
import java.util.IllegalFormatPrecisionException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class TryToCallWithNoResourceTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void try_tryWithNoResourceToCallThenCall_thenCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(RuntimeException.class)
            .thenCall((exception) -> testHelper.throwException())
            .done();

    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).throwException();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCall_thenCalled_whenExceptionIsNotRaised_andTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(IllegalArgumentException.class, IllegalAccessError.class)
            .thenCall((exception) -> testHelper.throwException())
            .done();

    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).throwException();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCall_thenCalled_whenExceptionIsNotRaised_andThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(IllegalArgumentException.class, IllegalAccessError.class, IllegalSelectorException.class)
            .thenCall((exception) -> testHelper.throwException())
            .done();

    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).throwException();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCall_thenCalledElseCalledFinallyCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(RuntimeException.class)
            .thenCall((exception) -> testHelper.throwException())
            .elseCall(testHelper::elseCallMe)
            .done();

    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).throwException();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCall_thenCalledElseCalledFinallyCalled_whenExceptionIsNotRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(IllegalAccessError.class, IllegalArgumentException.class)
            .thenCall((exception) -> testHelper.throwException())
            .elseCall(testHelper::elseCallMe)
            .done();

    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).throwException();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCall_thenCalledElseCalledFinallyCalled_whenExceptionIsNotRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(IllegalAccessError.class, IllegalArgumentException.class, IllegalSelectorException.class)
            .thenCall((exception) -> testHelper.throwException())
            .elseCall(testHelper::elseCallMe)
            .done();

    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).throwException();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallFinallyDone_finallyDoneIsExecuted_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(RuntimeException.class)
            .thenCall((exception) -> testHelper.throwException())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).throwException();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallFinallyDone_finallyDoneIsExecuted_whenExceptionIsNotRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(IllegalArgumentException.class, IllegalMonitorStateException.class)
            .thenCall((exception) -> testHelper.throwException())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).throwException();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallFinallyDone_finallyDoneIsExecuted_whenExceptionIsNotRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(IllegalArgumentException.class, IllegalMonitorStateException.class, IllegalSelectorException.class)
            .thenCall((exception) -> testHelper.throwException())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).throwException();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_thenCallfinallyDoneElseCall_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(RuntimeException.class)
            .thenCall((exception) -> testHelper.throwException())
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
    verify(testHelper, times(0)).throwException();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_thenCallfinallyDoneElseCall_whenExceptionIsNotRaised_whenTwoExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(IllegalMonitorStateException.class, IllegalCallerException.class)
            .thenCall((exception) -> testHelper.throwException())
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
    verify(testHelper, times(0)).throwException();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_thenCallfinallyDoneElseCall_whenExceptionIsNotRaised_whenThreeExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(IllegalMonitorStateException.class, IllegalCallerException.class, IllegalSelectorException.class)
            .thenCall((exception) -> testHelper.throwException())
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
    verify(testHelper, times(0)).throwException();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCall_throwExceptionCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .done();

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCall_throwExceptionCalled_whenFirstExceptionIsRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .done();

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCall_throwExceptionCalled_whenFirstExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class, IllegalSelectorException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .done();

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCall_throwExceptionCalled_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCallerException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class, IllegalSelectorException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .done();

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCall_throwExceptionCalled_whenThirdExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCharsetNameException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class, IllegalCharsetNameException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .done();

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCall_throwExceptionCalledElseNotCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseCall(testHelper::elseCallMe)
            .done();

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCall_throwExceptionCalledElseNotCalled_whenFirstExceptionIsRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseCall(testHelper::elseCallMe)
            .done();

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCall_throwExceptionCalledElseNotCalled_whenFirstExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class, IllegalCharsetNameException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseCall(testHelper::elseCallMe)
            .done();

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCall_throwExceptionCalledElseNotCalled_whenSecondExceptionIsRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCallerException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseCall(testHelper::elseCallMe)
            .done();

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCall_throwExceptionCalledElseNotCalled_whenSecondExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCallerException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class, IllegalCharsetNameException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseCall(testHelper::elseCallMe)
            .done();

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCall_throwExceptionCalledElseNotCalled_whenThirdExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCharsetNameException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class, IllegalCharsetNameException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseCall(testHelper::elseCallMe)
            .done();

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallFinallyDone_finallyDoneIsCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallFinallyDone_finallyDoneIsCalled_whenFirstExceptionIsRaised_whenTwoExceptionAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallFinallyDone_finallyDoneIsCalled_whenFirstExceptionIsRaised_whenThreeExceptionAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class, IllegalCharsetNameException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallFinallyDone_finallyDoneIsCalled_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCallerException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallFinallyDone_finallyDoneIsCalled_whenSecondExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCallerException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class, IllegalCharsetNameException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallFinallyDone_finallyDoneIsCalled_whenThirdExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCharsetNameException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class, IllegalCharsetNameException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalled_whenExceptionIsRaised() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class)
            .thenCall((expectedException) -> {
            })
            .elseIfRaises(RuntimeException.class)
            .thenThrow((exception -> new IOException()))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalled_whenExceptionIsRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class)
            .thenCall((expectedException) -> {
            })
            .elseIfRaises(RuntimeException.class)
            .thenThrow((exception -> new RuntimeException()))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalled_whenExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class, IllegalStateException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class)
            .thenCall((expectedException) -> {
            })
            .elseIfRaises(RuntimeException.class)
            .thenThrow((exception -> new RuntimeException()))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalled_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCallerException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalStateException.class)
            .thenCall((expectedException) -> {
            })
            .elseIfRaises(RuntimeException.class)
            .thenThrow((exception -> new RuntimeException()))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalled_whenThirdExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalCallerException.class, IllegalStateException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenCall((expectedException) -> {
            })
            .elseIfRaises(RuntimeException.class)
            .thenThrow((exception -> new RuntimeException()))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalledSecondThenCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCallerException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class)
            .thenCall((expectedException) -> testHelper.thenCallMe1())
            .elseIfRaises(RuntimeException.class)
            .thenThrow((exception -> new RuntimeException()))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe1();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalledSecondThenCalled_whenFirstExceptionIsRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCallerException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalMonitorStateException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class)
            .thenCall((expectedException) -> testHelper.thenCallMe1())
            .elseIfRaises(RuntimeException.class, IllegalCharsetNameException.class)
            .thenThrow((exception -> new RuntimeException()))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe1();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalledSecondThenCalled_whenFirstExceptionIsRaised_whenThirdExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCallerException("some argument")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalMonitorStateException.class, IllegalStateException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class)
            .thenCall((expectedException) -> testHelper.thenCallMe1())
            .elseIfRaises(RuntimeException.class, IllegalCharsetNameException.class)
            .thenThrow((exception -> new RuntimeException()))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe1();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalledSecondThenCalled_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalFormatPrecisionException(1)).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalPathStateException.class, IllegalMonitorStateException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class, IllegalFormatPrecisionException.class)
            .thenCall((expectedException) -> testHelper.thenCallMe1())
            .elseIfRaises(RuntimeException.class, IllegalCharsetNameException.class)
            .thenThrow((exception -> new RuntimeException()))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe1();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalledSecondThenCalled_whenThirdExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCharsetNameException("some value")).when(testHelper).throwException();

    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalPathStateException.class, IllegalMonitorStateException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class, IllegalFormatPrecisionException.class, IllegalCharsetNameException.class)
            .thenCall((expectedException) -> testHelper.thenCallMe1())
            .elseIfRaises(RuntimeException.class, IllegalCharsetNameException.class)
            .thenThrow((exception -> new RuntimeException()))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe1();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalledRethrowException_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument")).when(testHelper).throwException();

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("new argument");
    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class)
            .thenCall((expectedException) -> testHelper.thenCallMe1())
            .elseIfRaises(IllegalStateException.class)
            .thenThrow((exception -> new RuntimeException("new argument")))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalledRethrowException_whenFirstExceptionIsRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument")).when(testHelper).throwException();

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("new argument");
    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalFormatPrecisionException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class)
            .thenCall((expectedException) -> testHelper.thenCallMe1())
            .elseIfRaises(IllegalStateException.class, IllegalBlockingModeException.class)
            .thenThrow((exception -> new RuntimeException("new argument")))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalledRethrowException_whenFirstExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument")).when(testHelper).throwException();

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("new argument");
    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalFormatPrecisionException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class)
            .thenCall((expectedException) -> testHelper.thenCallMe1())
            .elseIfRaises(IllegalStateException.class, IllegalBlockingModeException.class, IllegalCharsetNameException.class)
            .thenThrow((exception -> new RuntimeException("new argument")))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalledRethrowException_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalBlockingModeException()).when(testHelper).throwException();

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("new argument");
    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalFormatPrecisionException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class)
            .thenCall((expectedException) -> testHelper.thenCallMe1())
            .elseIfRaises(IllegalStateException.class, IllegalBlockingModeException.class)
            .thenThrow((exception -> new RuntimeException("new argument")))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseNotCalledRethrowException_whenSecondExceptionIsRaised_andThreeAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalBlockingModeException()).when(testHelper).throwException();

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("new argument");
    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IllegalArgumentException.class, IllegalFormatPrecisionException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class)
            .thenCall((expectedException) -> testHelper.thenCallMe1())
            .elseIfRaises(IllegalStateException.class, IllegalBlockingModeException.class, IllegalAccessException.class)
            .thenThrow((exception -> new RuntimeException("new argument")))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseCalled_whenExceptionIsRaised() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument")).when(testHelper).throwException();
    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(IllegalAccessError.class)
              .thenCall((exception) -> testHelper.thenCallMe())
              .elseIfRaises(IllegalCallerException.class)
              .thenCall((expectedException) -> testHelper.thenCallMe1())
              .elseIfRaises(IllegalStateException.class)
              .thenThrow((exception -> new RuntimeException("new argument")))
              .elseCall(testHelper::elseCallMe)
              .finallyDone(testHelper::finallCallMe);
      fail("expected to throw Exception exception");
    } catch (Exception exception) {
      verify(testHelper).throwException();
      verify(testHelper).finallCallMe();
    }
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseCalled_whenFirstExceptionIsRaised_andMultipleExceptionsAreHandled() throws Throwable {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException()).when(testHelper).throwException();
    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(IllegalAccessError.class, IllegalBlockingModeException.class)
              .thenCall((exception) -> testHelper.thenCallMe())
              .elseIfRaises(IllegalCallerException.class, IllegalFormatException.class)
              .thenCall((expectedException) -> testHelper.thenCallMe1())
              .elseIfRaises(IllegalStateException.class, IllegalSelectorException.class, IllegalAccessError.class)
              .thenThrow((exception -> new RuntimeException("new argument")))
              .elseCall(testHelper::elseCallMe)
              .finallyDone(testHelper::finallCallMe);
      fail("expected to throw Exception exception");
    } catch (Exception exception) {
      verify(testHelper).throwException();
      verify(testHelper).finallCallMe();
    }
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseCalled_whenSecondExceptionIsRaised() throws Throwable {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalSelectorException()).when(testHelper).throwException();
    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(IllegalAccessError.class, IllegalBlockingModeException.class)
              .thenCall((exception) -> testHelper.thenCallMe())
              .elseIfRaises(IllegalCallerException.class, IllegalFormatException.class)
              .thenCall((expectedException) -> testHelper.thenCallMe1())
              .elseIfRaises(IllegalStateException.class, IllegalSelectorException.class, IllegalAccessError.class)
              .thenThrow((exception -> new RuntimeException("new argument")))
              .elseCall(testHelper::elseCallMe)
              .finallyDone(testHelper::finallCallMe);
      fail("expected to throw Exception exception");
    } catch (Exception exception) {
      verify(testHelper).throwException();
      verify(testHelper).finallCallMe();
    }
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseCalled_whenThirdExceptionIsRaised() throws Throwable {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalMonitorStateException()).when(testHelper).throwException();
    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(IllegalAccessError.class, IllegalBlockingModeException.class)
              .thenCall((exception) -> testHelper.thenCallMe())
              .elseIfRaises(IllegalCallerException.class, IllegalFormatException.class)
              .thenCall((expectedException) -> testHelper.thenCallMe1())
              .elseIfRaises(IllegalStateException.class, IllegalSelectorException.class, IllegalMonitorStateException.class)
              .thenThrow((exception -> new RuntimeException("new argument")))
              .elseCall(testHelper::elseCallMe)
              .finallyDone(testHelper::finallCallMe);
      fail("expected to throw IllegalMonitorStateException exception");
    } catch (Exception exception) {
      verify(testHelper).throwException();
      verify(testHelper).finallCallMe();
    }
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseCalled_throwCheckedException() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument")).when(testHelper).throwException();
    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(Exception.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class)
            .thenCall((expectedException) -> testHelper.thenCallMe1())
            .elseIfRaises(IllegalStateException.class)
            .thenThrow((exception -> new Exception("new argument")))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseCalled_throwCheckedException_whenMultipleExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument")).when(testHelper).throwException();
    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(Exception.class, IllegalSelectorException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class, IllegalMonitorStateException.class)
            .thenCall((expectedException) -> testHelper.thenCallMe1())
            .elseIfRaises(IllegalStateException.class, IllegalPathStateException.class, IllegalArgumentException.class)
            .thenThrow((exception -> new Exception("new argument")))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseCalled_throwSecondException() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalPathStateException("some argument")).when(testHelper).throwException();
    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(Exception.class, IllegalSelectorException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class, IllegalMonitorStateException.class)
            .thenCall((expectedException) -> testHelper.thenCallMe1())
            .elseIfRaises(IllegalStateException.class, IllegalPathStateException.class, IllegalArgumentException.class)
            .thenThrow((exception -> new Exception("new argument")))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_tryWithNoResourceToCallThenCallElseCallFinallyDone_finallyDoneIsCalledElseCalled_throwThirdException() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();
    Try.withResources()
            .toCall(testHelper::thenCallMe)
            .ifRaises(Exception.class, IllegalSelectorException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .elseIfRaises(IllegalCallerException.class, IllegalMonitorStateException.class)
            .thenCall((expectedException) -> testHelper.thenCallMe1())
            .elseIfRaises(IllegalStateException.class, IllegalPathStateException.class, IllegalArgumentException.class)
            .thenThrow((exception -> new Exception("new argument")))
            .elseCall(testHelper::elseCallMe)
            .finallyDone(testHelper::finallCallMe);
    verify(testHelper).thenCallMe();
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_toCallThenThrow_throwException_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new RuntimeException("some argument ")).when(testHelper).throwException();

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("some argument some value");
    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(RuntimeException.class)
            .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
            .done();
  }

  @Test
  public void try_toCallThenThrow_throwException_whenFirstExceptionIsRaised_whenMultipleExceptionsArehandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new RuntimeException("some argument ")).when(testHelper).throwException();

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("some argument some value");
    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(RuntimeException.class, IllegalPathStateException.class, IllegalArgumentException.class)
            .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
            .done();
  }

  @Test
  public void try_toCallThenThrow_throwException_whenSecondExceptionIsRaised_whenMultipleExceptionsArehandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalPathStateException("some argument ")).when(testHelper).throwException();

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("some argument some value");
    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(RuntimeException.class, IllegalPathStateException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
            .done();
  }

  @Test
  public void try_toCallThenThrow_throwException_whenThirdExceptionIsRaised_whenMultipleExceptionsArehandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument ")).when(testHelper).throwException();

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("some argument some value");
    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(RuntimeException.class, IllegalPathStateException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
            .done();
  }

  @Test
  public void try_toCallThenThrowElseCall_throwExceptionElseNotCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new RuntimeException("some argument ")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(RuntimeException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .elseCall(testHelper::elseCallMe)
              .done();
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toCallThenThrowElseCall_throwExceptionElseNotCalled_whenFirstExceptionIsRaised_whenMultipleExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalMonitorStateException("some argument ")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .elseCall(testHelper::elseCallMe)
              .done();
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toCallThenThrowElseCall_throwExceptionElseNotCalled_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalPathStateException("some argument ")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .elseCall(testHelper::elseCallMe)
              .done();
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toCallThenThrowElseCall_throwExceptionElseNotCalled_whenThirdExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument ")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
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
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(RuntimeException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }
    verify(testHelper).finallCallMe();
    verify(testHelper).throwException();
  }

  @Test
  public void try_toCallThenThrowFinallyDone_throwExceptionFinallyCalled_whenFirstExceptionIsRaised_andMultipleExceptionsAreRaised() throws IllegalAccessException {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalMonitorStateException("some argument ")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(IllegalMonitorStateException.class, IllegalSelectorException.class, IllegalAccessException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }
    verify(testHelper).finallCallMe();
    verify(testHelper).throwException();
  }

  @Test
  public void try_toCallThenThrowFinallyDone_throwExceptionFinallyCalled_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCallerException("some argument")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(IllegalMonitorStateException.class, IllegalCallerException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + " some value"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }
    verify(testHelper).finallCallMe();
    verify(testHelper).throwException();
  }

  @Test
  public void try_toCallThenThrowFinallyDone_throwExceptionFinallyCalled_whenSecondExceptionIsRaised_whenMultipleExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCallerException("some argument")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(IllegalMonitorStateException.class, IllegalCallerException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + " some value"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }
    verify(testHelper).finallCallMe();
    verify(testHelper).throwException();
  }

  @Test
  public void try_toCallThenThrowFinallyDone_throwExceptionFinallyCalled_whenThirdExceptionIsRaised_whenMultipleExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(IllegalMonitorStateException.class, IllegalCallerException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + " some value"))
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
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(RuntimeException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
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
  public void try_toCallThenThrowElseCallFinallyDone_throwExceptionFinallyCalledElseNotCalled_whenFirstExceptionIsRaised_andMultipleExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalPathStateException("some argument")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(IllegalPathStateException.class, IllegalFormatException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + " some value"))
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
  public void try_toCallThenThrowElseCallFinallyDone_throwExceptionFinallyCalledElseNotCalled_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalAccessError("some argument")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(IllegalStateException.class, IllegalAccessError.class, IllegalSelectorException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + " some value"))
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
  public void try_toCallThenThrowElseCallFinallyDone_throwExceptionFinallyCalledElseNotCalled_whenThirdExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalCallerException("some argument")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(IllegalStateException.class, IllegalAccessError.class, IllegalCallerException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + " some value"))
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
    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(RuntimeException.class)
            .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
            .done();
  }

  @Test
  public void try_toCallThenThrow_throwException_whenChildOfFirstExceptionIsRaised_whenMultipleExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new ArrayIndexOutOfBoundsException("some argument ")).when(testHelper).throwException();

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("some argument some value");
    Try.withResources()
            .toCall(testHelper::throwException)
            .ifRaises(IndexOutOfBoundsException.class, RuntimeException.class, Exception.class)
            .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
            .done();
  }

  @Test
  public void try_toCallThenThrow_throwException_whenChildOfSecondExceptionIsRaised_whenMultipleExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IOException("some argument ")).when(testHelper).throwCheckedException();

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("some argument some value");
    Try.withResources()
            .toCall(testHelper::throwCheckedException)
            .ifRaises(IndexOutOfBoundsException.class, RuntimeException.class, Exception.class)
            .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
            .done();
  }

  @Test
  public void try_toCallThenThrow_throwException_whenChildOfThirdExceptionIsRaised_whenMultipleExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IOException("some argument ")).when(testHelper).throwCheckedException();

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("some argument some value");
    Try.withResources()
            .toCall(testHelper::throwCheckedException)
            .ifRaises(IndexOutOfBoundsException.class, RuntimeException.class, Exception.class)
            .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
            .done();
  }

  @Test
  public void try_toCallThenThrowThenElse_throwExceptionElseCallNotCalled_whenChildExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument ")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(RuntimeException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .elseCall(testHelper::elseCallMe)
              .done();
      fail("IllegalArgumentException expected but did not raise");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }

    verify(testHelper).throwException();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toCallThenThrowThenElse_throwExceptionElseCallNotCalled_whenChildExceptionIsRaised_andMultipleExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument ")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(RuntimeException.class, IllegalMonitorStateException.class, IOException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .elseCall(testHelper::elseCallMe)
              .done();
      fail("IllegalArgumentException expected but did not raise");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }

    verify(testHelper).throwException();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toCallThenThrowThenElse_throwExceptionElseCallNotCalled_whenChildOfSecondExceptionIsRaised() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new SQLTimeoutException("some argument ")).when(testHelper).throwCheckedException();

    try {
      Try.withResources()
              .toCall(testHelper::throwCheckedException)
              .ifRaises(RuntimeException.class, SQLException.class, IOException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .elseCall(testHelper::elseCallMe)
              .done();
      fail("IllegalArgumentException expected but did not raise");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }

    verify(testHelper).throwCheckedException();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toCallThenThrowThenElse_throwExceptionElseCallNotCalled_whenChildOfThirdExceptionIsRaised() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new FileNotFoundException("exception ")).when(testHelper).throwCheckedException();

    try {
      Try.withResources()
              .toCall(testHelper::throwCheckedException)
              .ifRaises(RuntimeException.class, SQLException.class, IOException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .elseCall(testHelper::elseCallMe)
              .done();
      fail("IllegalArgumentException expected but did not raise");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("exception some value"));
    }

    verify(testHelper).throwCheckedException();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toCallThenThrowFinallyDone_throwExceptionFinallyCalled_whenChildExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument ")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(RuntimeException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }

    verify(testHelper).throwException();
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toCallThenThrowFinallyDone_throwExceptionFinallyCalled_whenChildOfFirstExceptionIsRaised_whenMultipleExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument ")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(RuntimeException.class, SQLException.class, IOException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }

    verify(testHelper).throwException();
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toCallThenThrowFinallyDone_throwExceptionFinallyCalled_whenChildOfThirdExceptionIsRaised_whenMultipleExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new FileNotFoundException("some argument ")).when(testHelper).throwCheckedException();
    try {
      Try.withResources()
              .toCall(testHelper::throwCheckedException)
              .ifRaises(RuntimeException.class, SQLException.class, FileNotFoundException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }

    verify(testHelper).throwCheckedException();
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toCallThenThrowFinallyDone_throwExceptionFinallyCalled_whenChildOfSecondExceptionIsRaised() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IOException("some argument ")).when(testHelper).throwCheckedException();

    try {
      Try.withResources()
              .toCall(testHelper::throwCheckedException)
              .ifRaises(RuntimeException.class, Exception.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }

    verify(testHelper).throwCheckedException();
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_toCallThenThrowElseCallFinallyDone_throwExceptionFinallyCalledElseCallNotCalled_whenChildExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument ")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(RuntimeException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
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
  public void try_toCallThenThrowElseCallFinallyDone_throwExceptionFinallyCalledElseCallNotCalled_whenChildOfFirstExceptionIsRaised() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument ")).when(testHelper).throwException();

    try {
      Try.withResources()
              .toCall(testHelper::throwException)
              .ifRaises(RuntimeException.class, Exception.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
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
  public void try_toCallThenThrowElseCallFinallyDone_throwExceptionFinallyCalledElseCallNotCalled_whenChildOfSecondExceptionIsRaised() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IOException("some argument ")).when(testHelper).throwCheckedException();

    try {
      Try.withResources()
              .toCall(testHelper::throwCheckedException)
              .ifRaises(RuntimeException.class, SQLException.class, IOException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .elseCall(testHelper::elseCallMe)
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }

    verify(testHelper).throwCheckedException();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_toCallThenThrowElseCallFinallyDone_throwExceptionFinallyCalledElseCallNotCalled_whenChildOfThirdExceptionIsRaised() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new FileNotFoundException("some argument ")).when(testHelper).throwCheckedException();

    try {
      Try.withResources()
              .toCall(testHelper::throwCheckedException)
              .ifRaises(RuntimeException.class, SQLException.class, IOException.class)
              .thenThrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
              .elseCall(testHelper::elseCallMe)
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException exception) {
      assertThat(exception.getMessage(), is("some argument some value"));
    }

    verify(testHelper).throwCheckedException();
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  static class TestHelper {
    void thenCallMe() {
      System.out.println("then call me");
    }

    void thenCallMe1() {
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

    void throwCheckedException() throws Exception {
      throw new Exception("something illegal happened");
    }
  }

  public static class MyResource implements AutoCloseable {
    MyResource() {
      // Do nothing
    }

    void doSomething() throws FileNotFoundException {
      // do nothing;
    }

    @Override
    public void close() throws Exception {
      // do nothing
    }
  }
}