package com.thenakliman.tries;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.function.Supplier;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class TryTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

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

  @Test
  public void try_toCallThenThrow_doesNotThrowException_whenExceptionIsNotRaised() {
    TestHelper testHelper = mock(TestHelper.class);

    Try.toCall(testHelper::thenCallMe)
            .acceptRuntimeException(RuntimeException.class)
            .thenCall((exception) -> testHelper.throwException())
            .done();

    verify(testHelper).thenCallMe();
  }

  @Test
  public void try_toCallThenThrow_throwException_whenExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new RuntimeException("some argument ")).when(testHelper).throwException();

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("some argument some value");
    Try.toCall(testHelper::throwException)
            .acceptRuntimeException(RuntimeException.class)
            .thenRethrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
            .done();
  }

  @Test
  public void try_toCallThenThrow_throwException_whenChildExceptionIsRaised() {
    TestHelper testHelper = mock(TestHelper.class);
    doThrow(new IllegalStateException("some argument ")).when(testHelper).throwException();

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("some argument some value");
    Try.toCall(testHelper::throwException)
            .acceptRuntimeException(RuntimeException.class)
            .thenRethrow((exception) -> new IllegalArgumentException(exception.getMessage() + "some value"))
            .done();
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
  public void try_toGetThenRethrow_returnGetValue_whenExceptionIsNotRaised() {
    Integer done = Try.toGet(() -> 10)
            .acceptRuntimeException(RuntimeException.class)
            .thenRethrow((exception) -> new IllegalArgumentException(""))
            .done();
    assertThat(done, is(10));
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
    Integer done = Try.toGet(supplier)
            .acceptRuntimeException(RuntimeException.class)
            .thenRethrow((exception) -> new RuntimeException(exception.getMessage() + "run time"))
            .done();
    assertThat(done, is(20));
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