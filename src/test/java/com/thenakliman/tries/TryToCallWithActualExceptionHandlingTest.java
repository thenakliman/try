package com.thenakliman.tries;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class TryToCallWithActualExceptionHandlingTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void try_tryToCallThenCall_thenCalled_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.toCall(testHelper::thenCallMe)
            .done();

    verify(testHelper).thenCallMe();
  }

  @Test
  public void try_tryToCallThenCall_throwRuntimeException_whenExceptionIsNotRaised() {
    TestHelper testHelper = new TestHelper();

    expectedException.expect(IllegalArgumentException.class);
    Try.toCall(testHelper::throwException)
            .done();
  }

  @Test
  public void try_tryToCallThenCall_throwCheckedException_whenExceptionIsRaised() {
    TestHelper testHelper = new TestHelper();

    expectedException.expect(Exception.class);
    Try.toCall(testHelper::throwCheckedException)
            .done();
  }

  @Test
  public void try_tryToCallThenCallFinally_whenExceptionIsNotRaised() {
    TestHelper testHelper1 = mock(TestHelper.class);
    TestHelper testHelper2 = mock(TestHelper.class);

    doNothing().when(testHelper1).thenCallMe();
    doNothing().when(testHelper2).finallyCallMe();

    Try.toCall(testHelper1::thenCallMe)
            .finallyDone(testHelper2::finallyCallMe);

    InOrder inOrder = inOrder(testHelper1, testHelper2);

    inOrder.verify(testHelper1).thenCallMe();
    inOrder.verify(testHelper2).finallyCallMe();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void try_tryToCallThenCallFinally_throwRuntimeException_whenExceptionIsRaised() {
    TestHelper testHelper1 = mock(TestHelper.class);
    TestHelper testHelper2 = mock(TestHelper.class);

    doThrow(new IllegalArgumentException("something")).when(testHelper1).throwException();
    doNothing().when(testHelper2).finallyCallMe();

    try {
      Try.toCall(testHelper1::throwException)
              .finallyDone(testHelper2::finallyCallMe);
      fail("Expected to raise IllegalArgumentException");
    } catch (IllegalArgumentException exception) {
      InOrder inOrder = inOrder(testHelper1, testHelper2);
      inOrder.verify(testHelper1).throwException();
      inOrder.verify(testHelper2).finallyCallMe();
      inOrder.verifyNoMoreInteractions();
    }
  }

  @Test
  public void try_tryToCallThenCallFinally_throwCheckedException_whenExceptionIsRaised() throws Exception {
    TestHelper testHelper1 = mock(TestHelper.class);
    TestHelper testHelper2 = mock(TestHelper.class);

    doThrow(new Exception("something")).when(testHelper1).throwCheckedException();
    doNothing().when(testHelper2).finallyCallMe();

    try {
      Try.toCall(testHelper1::throwCheckedException)
              .finallyDone(testHelper2::finallyCallMe);
      fail("Expected to raise Exception");
    } catch (Exception exception) {
      InOrder inOrder = inOrder(testHelper1, testHelper2);
      inOrder.verify(testHelper1).throwCheckedException();
      inOrder.verify(testHelper2).finallyCallMe();
      inOrder.verifyNoMoreInteractions();
    }
  }

  static class TestHelper {
    void thenCallMe() {
      System.out.println("then call me");
    }

    void finallyCallMe() {
      System.out.println("else call me");
    }

    void throwException() {
      throw new IllegalArgumentException("something illegal happened");
    }

    void throwCheckedException() throws Exception {
      throw new Exception("something illegal happened");
    }
  }
}