package com.thenakliman.tries;

import org.junit.Test;

import static org.mockito.Mockito.*;

public class TryTest {

  @Test
  public void tryToCall_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.toCall(testHelper::thenCallMe)
            .acceptRuntimeException(RuntimeException.class)
            .thenCall((exception) -> testHelper.throwException())
            .done();

    verify(testHelper).thenCallMe();
    verify(testHelper, times(0)).throwException();
  }

  @Test
  public void tryToCall_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalArgumentException("some argument")).when(testHelper).throwException();

    Try.toCall(testHelper::throwException)
            .acceptRuntimeException(IllegalArgumentException.class)
            .thenCall((exception) -> testHelper.thenCallMe())
            .done();

    verify(testHelper).throwException();
    verify(testHelper).thenCallMe();
  }

  static class TestHelper {
    void thenCallMe() {
      System.out.println("then call me");
    }

    void throwException() {
      throw new IllegalArgumentException("something illegal happened");
    }
  }
}