package com.thenakliman.tries;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.awt.*;
import java.awt.geom.IllegalPathStateException;
import java.io.IOException;
import java.nio.channels.IllegalSelectorException;
import java.nio.charset.IllegalCharsetNameException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class TryToGetWithNoResourcesTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void try_withResourcesRToGetThenGet_returnGetValue_whenExceptionIsNotRaised() {
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(10));
  }

  @Test
  public void try_withResourcesRToGetThenGet_returnGetValue_whenExceptionIsNotRaised_whenTwoExceptionsAreHandled() {
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(IllegalArgumentException.class, IllegalSelectorException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(10));
  }

  @Test
  public void try_withResourcesRToGetThenGet_returnGetValue_whenExceptionIsNotRaised_whenThreeExceptionsAreHandled() {
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(IllegalArgumentException.class, IllegalSelectorException.class, IllegalThreadStateException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(10));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCall_returnGetValueElseCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer expValue = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    verify(testHelper).elseCallMe();
    assertThat(expValue, is(10));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCall_returnGetValueElseCalled_whenExceptionIsNotRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer expValue = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(IllegalThreadStateException.class, IllegalPathStateException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    verify(testHelper).elseCallMe();
    assertThat(expValue, is(10));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCall_returnGetValueElseCalled_whenExceptionIsNotRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer expValue = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(IllegalThreadStateException.class, IllegalPathStateException.class, IllegalComponentStateException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    verify(testHelper).elseCallMe();
    assertThat(expValue, is(10));
  }

  @Test
  public void try_withResourcesRToGetThenGetFinallyDone_returnGetValueFinallyCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetFinallyDone_returnGetValueFinallyCalled_whenExceptionIsNotRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetFinallyDone_returnGetValueFinallyCalled_whenExceptionIsNotRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCallFinallyDone_returnGetValueFinallyCalledElseCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCallFinallyDone_returnGetValueFinallyCalledElseCalled_whenExceptionIsNotRaised_whenTwoExceptionsAreHandled() throws IllegalAccessException {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(IllegalComponentStateException.class, IllegalAccessException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCallFinallyDone_returnGetValueFinallyCalledElseCalled_whenExceptionIsNotRaised_whenThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(IllegalComponentStateException.class, IllegalAccessException.class, IllegalSelectorException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGet_returnThenValue_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalArgumentException.class);
    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(20));
  }

  @Test
  public void try_withResourcesRToGetThenGet_returnThenValue_whenFirstExceptionIsRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalArgumentException.class);

    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(20));
  }

  @Test
  public void try_withResourcesRToGetThenGet_returnThenValue_whenSecondExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalStateException.class);

    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalPathStateException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(20));
  }

  @Test
  public void try_withResourcesRToGetThenGet_returnThenValue_whenThirdExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalPathStateException.class);

    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalPathStateException.class)
            .thenGet((exception) -> 20)
            .done();
    assertThat(done, is(20));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCall_returnThenValue_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalArgumentException.class);

    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    assertThat(done, is(20));
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCall_returnThenValue_whenExceptionFirstIsRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalArgumentException.class);
    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalPathStateException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    assertThat(done, is(20));
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCall_returnThenValue_whenExceptionSecondIsRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalPathStateException.class);
    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalPathStateException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    assertThat(done, is(20));
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCall_returnThenValue_whenExceptionSecondIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalPathStateException.class);
    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalPathStateException.class, IllegalMonitorStateException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    assertThat(done, is(20));
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCall_returnThenValue_whenExceptionThirdIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalMonitorStateException.class);
    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalPathStateException.class, IllegalMonitorStateException.class)
            .thenGet((exception) -> 20)
            .elseCall((value) -> testHelper.elseCallMe())
            .done();
    assertThat(done, is(20));
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetFinallyCalled_returnThenValueFinallyCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalArgumentException.class);
    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetFinallyCalled_returnThenValueFinallyCalled_whenFirstExceptionIsRaised_whenTwoExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalArgumentException.class);
    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalSelectorException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetFinallyCalled_returnThenValueFinallyCalled_whenSecondExceptionIsRaised_whenTwoExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalArgumentException.class);
    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalSelectorException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetFinallyCalled_returnThenValueFinallyCalled_whenSecondExceptionIsRaised_whenThreeExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalSelectorException.class);
    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalSelectorException.class, IllegalComponentStateException.class)
            .thenGet((exception) -> 20)
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCallFinallyCall_returnThenValueFinallyCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalArgumentException.class);
    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(RuntimeException.class)
            .thenGet((exception) -> 20)
            .elseCall(value -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCallFinallyCall_returnThenValueFinallyCalled_whenFirstExceptionIsRaised_whenTwoExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalMonitorStateException.class);
    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalMonitorStateException.class, IllegalStateException.class)
            .thenGet((exception) -> 20)
            .elseCall(value -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCallFinallyCall_returnThenValueFinallyCalled_whenSecondExceptionIsRaised_whenTwoExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("illegal "));
    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalMonitorStateException.class, IllegalStateException.class)
            .thenGet((exception) -> 20)
            .elseCall(value -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCallFinallyCall_returnThenValueFinallyCalled_whenSecondExceptionIsRaised_whenThreeExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalStateException.class);
    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalMonitorStateException.class, IllegalStateException.class, IllegalArgumentException.class)
            .thenGet((exception) -> 20)
            .elseCall(value -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseCallFinallyCall_returnThenValueFinallyCalled_whenThirdExceptionIsRaised_whenThreeExceptionsAreRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(IllegalArgumentException.class);
    Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalMonitorStateException.class, IllegalStateException.class, IllegalArgumentException.class)
            .thenGet((exception) -> 20)
            .elseCall(value -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);
    assertThat(done, is(20));
    verify(testHelper).finallCallMe();
    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenRethrow_returnGetValue_whenExceptionIsNotRaised() {
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenThrow((exception) -> new IllegalArgumentException(""))
            .done();
    assertThat(done, is(10));
  }

  @Test
  public void try_withResourcesRToGetThenRethrow_returnGetValue_whenExceptionIsNotRaised_whenTwoExceptionsAreHandled() {
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalArgumentException(""))
            .done();
    assertThat(done, is(10));
  }

  @Test
  public void try_withResourcesRToGetThenRethrow_returnGetValue_whenExceptionIsNotRaised_whenThreeExceptionsAreHandled() {
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalPathStateException.class)
            .thenThrow((exception) -> new IllegalArgumentException(""))
            .done();
    assertThat(done, is(10));
  }

  @Test
  public void try_withResourcesRToGetThenRethrowFinallyDone_returnGetValueFinallyCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenThrow((exception) -> new IllegalArgumentException(""))
            .finallyDone(testHelper::finallCallMe);

    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenRethrowFinallyDone_returnGetValueFinallyCalled_whenExceptionIsNotRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenThrow((exception) -> new IllegalArgumentException(""))
            .finallyDone(testHelper::finallCallMe);

    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenRethrowFinallyDone_returnGetValueFinallyCalled_whenExceptionIsNotRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    Integer done = Try.withResources()
            .toGet(() -> 10)
            .ifRaises(RuntimeException.class)
            .thenThrow((exception) -> new IllegalArgumentException(""))
            .finallyDone(testHelper::finallCallMe);

    assertThat(done, is(10));
    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrow_throwException_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("illegal "));
    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("illegal run time");
    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(RuntimeException.class)
            .thenThrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenThrow_throwException_whenExceptionIsRaised_whenTwoExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("illegal"));

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("illegal run time");
    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new RuntimeException(exception.getMessage() + " run time"))
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenThrow_throwException_whenExceptionIsRaised_whenThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("illegal"));

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("illegal run time");
    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalAccessException.class)
            .thenThrow((exception) -> new RuntimeException(exception.getMessage() + " run time"))
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenThrow_throwException_whenSecondExceptionIsRaised_whenThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("illegal "));

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("illegal run time");
    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalAccessException.class)
            .thenThrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenThrow_throwException_whenThirdExceptionIsRaised_whenThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("illegal "));

    expectedException.expect(RuntimeException.class);
    expectedException.expectMessage("illegal run time");
    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalMonitorStateException.class)
            .thenThrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenThrowFinallyDone_throwExceptionFinallyCalled_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("illegal "));

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(RuntimeException.class)
              .thenThrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected RuntimeException.");
    } catch (RuntimeException exception) {
      assertThat(exception.getMessage(), is("illegal run time"));
    }

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrowFinallyDone_throwExceptionFinallyCalled_whenExceptionIsRaised_whenTwoExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("illegal "));
    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected RuntimeException.");
    } catch (RuntimeException exception) {
      assertThat(exception.getMessage(), is("illegal run time"));
    }

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrowFinallyDone_throwExceptionFinallyCalled_whenExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("illegal "));
    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalMonitorStateException.class)
              .thenThrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected RuntimeException.");
    } catch (RuntimeException exception) {
      assertThat(exception.getMessage(), is("illegal run time"));
    }

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrowFinallyDone_throwExceptionFinallyCalled_whenSecondExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("illegal "));
    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalMonitorStateException.class)
              .thenThrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected RuntimeException.");
    } catch (RuntimeException exception) {
      assertThat(exception.getMessage(), is("illegal run time"));
    }

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrowFinallyDone_throwExceptionFinallyCalled_whenThirdExceptionIsRaised_whenThreeExceptionsAreHandled() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("illegal "));

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class, IllegalMonitorStateException.class)
              .thenThrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected RuntimeException.");
    } catch (RuntimeException exception) {
      assertThat(exception.getMessage(), is("illegal run time"));
    }

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_returnGetValue_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenReturn(10);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    assertThat(done, is(10));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_calledElseCall_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenReturn(10);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .elseCall((exception) -> testHelper.elseCallMe())
            .done();

    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_calledFinallyDone_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenReturn(10);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .elseCall((exception) -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_returnFirstThenValue_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    assertThat(done, is(20));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_calledFinallyDone_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_notCalledElseCall_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .elseCall((exception) -> testHelper.elseCallMe())
            .done();

    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_thenGetOfExceptionHandlingAreNotCalled_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    verify(testHelper, times(0)).thenGet3();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_returnFirstThenGet_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    assertThat(done, is(20));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_calledFinallyDone_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_notCalledElseCall_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .elseCall(exception -> testHelper.elseCallMe())
            .done();

    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_ThenGetOfElseIfRaisesIsNotCalled_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    verify(testHelper, times(0)).thenGet3();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_returnSecondThenGet_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalMonitorStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    assertThat(done, is(30));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_calledFinallyDone_whenFirstExceptionOfElseIfRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalMonitorStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_notCalledElseCall_whenElseIfRaisedExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalMonitorStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .elseCall((exception) -> testHelper.elseCallMe())
            .done();

    assertThat(done, is(30));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_ThenGetOfIfRaisesIsNotCalled_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalMonitorStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    verify(testHelper, times(0)).thenGet2();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_returnSecondThenGet_whenSecondExceptionIsRaised_andThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalPathStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalAccessException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    assertThat(done, is(30));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_finallyDoneCalled_whenSecondExceptionIsRaised_andThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalPathStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalAccessException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_notCalledElseCall_whenSecondExceptionIsRaised_andThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalPathStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalAccessException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .elseCall((exception) -> testHelper.elseCallMe())
            .done();

    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_ThenGetOfIfRaisesIsNotCalled_whenSecondExceptionIsRaised_andThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalPathStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalAccessException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    verify(testHelper, times(0)).thenGet2();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_returnSecondThenGet_whenThirdExceptionIsRaised_andThreeExceptionsAreHandled() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalAccessError());
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class, IOException.class, IllegalAccessError.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    assertThat(done, is(30));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_calledFinallyDone_whenThirdExceptionIsRaised_andThreeExceptionsAreHandled() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalAccessError());
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class, IOException.class, IllegalAccessError.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_notCalledElseCall_whenThirdExceptionIsRaised_andThreeExceptionsAreHandled() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalAccessError());
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class, IOException.class, IllegalAccessError.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .elseCall(exception -> testHelper.elseCallMe())
            .done();

    assertThat(done, is(30));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenGet_ThenGetOfIfRaisesIsNotCalled_whenThirdExceptionIsRaised_andThreeExceptionsAreHandled() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalAccessError());
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class, IOException.class, IllegalAccessError.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    verify(testHelper, times(0)).thenGet2();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_returnGetValue_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenReturn(10);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .done();

    assertThat(done, is(10));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_calledElseCall_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenReturn(10);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .elseCall((exception) -> testHelper.elseCallMe())
            .done();

    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_calledFinallyDone_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenReturn(10);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .elseCall((exception) -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_returnFirstThenGet_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .done();

    assertThat(done, is(20));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_notCalledElseCall_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .elseCall((exception) -> testHelper.elseCallMe())
            .done();

    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_calledFinallyDone_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .elseCall((exception) -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_thenGetOfExceptionHandlingAreNotCalled_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .done();

    verify(testHelper, times(0)).thenGet3();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_returnFirstThenGet_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .done();

    assertThat(done, is(20));
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_notCalledElseCall_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .elseCall(exception -> testHelper.elseCallMe())
            .done();

    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrowFinallyDone_calledFinallyDone_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_throwThenThrow_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalMonitorStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    expectedException.expect(IllegalThreadStateException.class);
    expectedException.expectMessage("some exception");
    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_calledfinallyDone_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalMonitorStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenGet((exception) -> testHelper.thenGet2())
              .elseIfRaises(IllegalMonitorStateException.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected IllegalThreadStateException");
    } catch (IllegalThreadStateException exception) {
      verify(testHelper).finallCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_notCalledElseCall_whenElseIfRaisedExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalMonitorStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenGet((exception) -> testHelper.thenGet2())
              .elseIfRaises(IllegalMonitorStateException.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .elseCall((exception) -> testHelper.elseCallMe())
              .done();
      fail("expected to raise IllegalThreadStateException");
    } catch (IllegalThreadStateException exception) {
      verify(testHelper, times(0)).elseCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_ThenGetOfIfRaisesIsNotCalled_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalMonitorStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenGet((exception) -> testHelper.thenGet2())
              .elseIfRaises(IllegalMonitorStateException.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .done();
      fail("expected to raise IllegalThreadStateException");
    } catch (IllegalThreadStateException exception) {
      verify(testHelper, times(0)).thenGet2();
    }
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_throwThenThrow_whenSecondExceptionIsRaised_andThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalPathStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    expectedException.expectMessage("some exception");
    expectedException.expect(IllegalThreadStateException.class);
    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalAccessException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_notCalledElseCall_whenSecondExceptionIsRaised_andThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalPathStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenGet((exception) -> testHelper.thenGet2())
              .elseIfRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalAccessException.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .elseCall((exception) -> testHelper.elseCallMe())
              .done();
      fail("Expected to throw IllegalThreadStateException");
    } catch (IllegalThreadStateException exception) {
      verify(testHelper, times(0)).elseCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_ThenGetOfIfRaisesIsNotCalled_whenSecondExceptionIsRaised_andThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalPathStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenGet((exception) -> testHelper.thenGet2())
              .elseIfRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalAccessException.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .done();
      fail("Expected to throw IllegalThreadStateException");
    } catch (IllegalThreadStateException exception) {
      verify(testHelper, times(0)).thenGet2();
    }
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_returnSecondThenGet_whenThirdExceptionIsRaised_andThreeExceptionsAreHandled() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalAccessError());
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    expectedException.expectMessage("some exception");
    expectedException.expect(IllegalThreadStateException.class);
    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class, IOException.class, IllegalAccessError.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_calledFinallyDone_whenThirdExceptionIsRaised_andThreeExceptionsAreHandled() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalAccessError());
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    try {
      final Integer done = Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenGet((exception) -> testHelper.thenGet2())
              .elseIfRaises(IllegalMonitorStateException.class, IOException.class, IllegalAccessError.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .finallyDone(testHelper::finallCallMe);
      fail("Expected exception IllegalThreadStateException");
    } catch (IllegalThreadStateException exception) {
      verify(testHelper).finallCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_notCalledElseCall_whenThirdExceptionIsRaised_andThreeExceptionsAreHandled() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalAccessError());
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenGet((exception) -> testHelper.thenGet2())
              .elseIfRaises(IllegalMonitorStateException.class, IOException.class, IllegalAccessError.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .elseCall(exception -> testHelper.elseCallMe())
              .done();

      fail("Expected to raise IllegalThreadStateException");
    } catch (IllegalThreadStateException exception) {
      verify(testHelper, times(0)).elseCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenGetElseIfRaisesThenThrow_ThenGetOfIfRaisesIsNotCalled_whenThirdExceptionIsRaised_andThreeExceptionsAreHandled() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalAccessError());
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    expectedException.expectMessage("some exception");
    expectedException.expect(IllegalThreadStateException.class);
    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenGet((exception) -> testHelper.thenGet2())
            .elseIfRaises(IllegalMonitorStateException.class, IOException.class, IllegalAccessError.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .done();

    verify(testHelper, times(0)).thenGet2();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_calledElseCall_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenReturn(10);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IOException("some exception"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .elseCall((exception) -> testHelper.elseCallMe())
            .done();

    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_callFinallyDone_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenReturn(10);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IOException("some exception"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .elseCall((exception) -> testHelper.elseCallMe())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_returnFirstThenValue_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    expectedException.expectMessage("someday");
    expectedException.expect(IllegalCharsetNameException.class);
    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_notCalledElseCall_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class)
              .thenGet((exception) -> testHelper.thenGet3())
              .elseCall((exception) -> testHelper.elseCallMe())
              .done();
      fail("Expected exception ");
    } catch (IllegalArgumentException exception) {
      verify(testHelper, times(0)).elseCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_thenGetOfExceptionHandlingAreNotCalled_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class)
              .thenGet((exception) -> testHelper.thenGet3())
              .done();
      fail("Expected IllegalCharsetNameException");
    } catch (IllegalCharsetNameException exception) {
      verify(testHelper, times(0)).thenGet3();
    }
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_callFinallyDone_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class)
              .thenGet((exception) -> testHelper.thenGet3())
              .finallyDone(testHelper::finallCallMe);
      fail("Expected exception IllegalMonitorStateException");
    } catch (IllegalCharsetNameException exception) {
      verify(testHelper).finallCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_throwSecondException_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    expectedException.expect(IllegalCharsetNameException.class);
    expectedException.expectMessage("someday");
    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_callFinallyDone_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    expectedException.expect(IllegalCharsetNameException.class);
    expectedException.expectMessage("someday");
    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_notCalledElseCall_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class)
              .thenGet((exception) -> testHelper.thenGet3())
              .elseCall(exception -> testHelper.elseCallMe())
              .done();
    } catch (IllegalCharsetNameException exception) {
      verify(testHelper, times(0)).elseCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_ThenGetOfElseIfRaisesIsNotCalled_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class)
              .thenGet((exception) -> testHelper.thenGet3())
              .done();
      fail("Expected IllegalCharsetNameException");
    } catch (IllegalCharsetNameException exception) {
      verify(testHelper, times(0)).thenGet3();
    }
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_returnSecondThenGet_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalMonitorStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    assertThat(done, is(30));
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_notCallElseCallMe_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalMonitorStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .elseCall((e) -> testHelper.elseCallMe())
            .done();

    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_callFinallyDone_whenFirstExceptionOfElseIfRaiseIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalMonitorStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_returnSecondThenGet_whenSecondExceptionIsRaised_andThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalPathStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalAccessException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    assertThat(done, is(30));
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_callFinallyDone_whenSecondExceptionIsRaised_andThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalPathStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalAccessException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_returnGetValue_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenReturn(10);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    assertThat(done, is(10));
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_callElseCall_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenReturn(10);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .elseCall((e) -> testHelper.elseCallMe())
            .done();
    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_notCalledElseCall_whenSecondExceptionIsRaised_andThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalPathStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalAccessException.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .elseCall((exception) -> testHelper.elseCallMe())
            .done();

    verify(testHelper, times(0)).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_returnSecondThenGet_whenThirdExceptionIsRaised_andThreeExceptionsAreHandled() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalAccessError());
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class, IOException.class, IllegalAccessError.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .done();

    assertThat(done, is(30));
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_callFinallyDone_whenThirdExceptionIsRaised_andThreeExceptionsAreHandled() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalAccessError());
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class, IOException.class, IllegalAccessError.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenGet_notCalledElseCall_whenThirdExceptionIsRaised_andThreeExceptionsAreHandled() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalAccessError());
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class, IOException.class, IllegalAccessError.class)
            .thenGet((exception) -> testHelper.thenGet3())
            .elseCall(exception -> testHelper.elseCallMe())
            .done();

    assertThat(done, is(30));
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_returnGetValue_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenReturn(10);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .done();

    assertThat(done, is(10));
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_callFinallyDone_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenReturn(10);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .finallyDone(testHelper::finallCallMe);

    verify(testHelper).finallCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_calledElseCall_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenReturn(10);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .elseCall((exception) -> testHelper.elseCallMe())
            .done();

    verify(testHelper).elseCallMe();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_throwIfRaiseException_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    expectedException.expectMessage("someday");
    expectedException.expect(IllegalCharsetNameException.class);
    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_notCalledElseCall_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .elseCall((exception) -> testHelper.elseCallMe())
              .done();
    } catch (IllegalCharsetNameException exception) {
      verify(testHelper, times(0)).elseCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_callFinallyDone_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    when(testHelper.thenGet1()).thenThrow(new IllegalArgumentException("invalid argument"));

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .elseCall((exception) -> testHelper.elseCallMe())
              .finallyDone(testHelper::finallCallMe);
    } catch (IllegalCharsetNameException exception) {
      verify(testHelper).finallCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_throwException_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    expectedException.expect(IllegalCharsetNameException.class);
    expectedException.expectMessage("someday");
    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_notCalledElseCall_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .elseCall(exception -> testHelper.elseCallMe())
              .done();
      fail("expected exceptin is IllegalThreadStateException");
    } catch (IllegalCharsetNameException exception) {
      verify(testHelper, times(0)).elseCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_callFinallyDone_whenSecondExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .elseCall(exception -> testHelper.elseCallMe())
              .finallyDone(testHelper::finallCallMe);
      fail("expected exceptin is IllegalThreadStateException");
    } catch (IllegalCharsetNameException exception) {
      verify(testHelper).finallCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_throwElseIfRaiseException_whenFirstExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalMonitorStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    expectedException.expect(IllegalThreadStateException.class);
    expectedException.expectMessage("some exception");
    final Integer done = Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_notCalledElseCall_whenElseIfRaisedExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalMonitorStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    try {
      final Integer done = Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .elseCall((exception) -> testHelper.elseCallMe())
              .done();
      fail("expected to raise IllegalThreadStateException");
    } catch (IllegalThreadStateException exception) {
      verify(testHelper, times(0)).elseCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_calledFinallyDone_whenElseIfRaisedExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalMonitorStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .elseCall((exception) -> testHelper.elseCallMe())
              .finallyDone(testHelper::finallCallMe);
      fail("expected to raise IllegalThreadStateException");
    } catch (IllegalThreadStateException exception) {
      verify(testHelper).finallCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_notCalledElseCall_whenSecondExceptionIsRaised_andThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalPathStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalAccessException.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .elseCall((exception) -> testHelper.elseCallMe())
              .done();
      fail("Expected to throw IllegalThreadStateException");
    } catch (IllegalThreadStateException exception) {
      verify(testHelper, times(0)).elseCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_callFinallyDone_whenSecondExceptionIsRaised_andThreeExceptionsAreHandled() throws Exception {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalPathStateException("invalid state"));
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class, IllegalPathStateException.class, IllegalAccessException.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .elseCall((exception) -> testHelper.elseCallMe())
              .finallyDone(testHelper::finallCallMe);
      fail("Expected to throw IllegalThreadStateException");
    } catch (IllegalThreadStateException exception) {
      verify(testHelper).finallCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_throwElseIfRaiseThrow_whenThirdExceptionIsRaised_andThreeExceptionsAreHandled() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalAccessError());
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);

    expectedException.expectMessage("some exception");
    expectedException.expect(IllegalThreadStateException.class);
    Try.withResources()
            .toGet(testHelper::thenGet1)
            .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
            .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
            .elseIfRaises(IllegalMonitorStateException.class, IOException.class, IllegalAccessError.class)
            .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
            .done();
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_notCalledElseCall_whenThirdExceptionIsRaised_andThreeExceptionsAreHandled() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalAccessError());
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class, IOException.class, IllegalAccessError.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .elseCall(exception -> testHelper.elseCallMe())
              .done();

      fail("Expected to raise IllegalThreadStateException");
    } catch (IllegalThreadStateException exception) {
      verify(testHelper, times(0)).elseCallMe();
    }
  }

  @Test
  public void try_withResourcesRToGetThenThrowElseIfRaisesThenThrow_calledFinallyDone_whenThirdExceptionIsRaised_andThreeExceptionsAreHandled() throws IOException {
    TestHelper testHelper = mock(TestHelper.class);
    when(testHelper.thenGet1()).thenThrow(new IllegalAccessError());
    when(testHelper.thenGet2()).thenReturn(20);
    when(testHelper.thenGet3()).thenReturn(30);
    try {
      Try.withResources()
              .toGet(testHelper::thenGet1)
              .ifRaises(IllegalArgumentException.class, IllegalStateException.class)
              .thenThrow((exception) -> new IllegalCharsetNameException("someday"))
              .elseIfRaises(IllegalMonitorStateException.class, IOException.class, IllegalAccessError.class)
              .thenThrow((exception) -> new IllegalThreadStateException("some exception"))
              .elseCall(exception -> testHelper.elseCallMe())
              .finallyDone(testHelper::finallCallMe);

      fail("Expected to raise IllegalThreadStateException");
    } catch (IllegalThreadStateException exception) {
      verify(testHelper).finallCallMe();
    }
  }

  static class TestHelper {
    Integer thenGet1() {
      return 10;
    }

    Integer thenGet2() {
      return 20;
    }

    Integer thenGet3() {
      return 30;
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