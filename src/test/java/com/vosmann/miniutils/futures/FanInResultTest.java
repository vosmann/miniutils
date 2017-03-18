package com.vosmann.miniutils.futures;

import org.junit.Test;

import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

public class FanInResultTest {

    @Test
    public void testBuildAllSuccessful() {
        FanInResult<String> result = new FanInResult.Builder<String>().add("ok")
                                                                      .add("good")
                                                                      .build();

        assertThat(result.getSuccessful(), containsInAnyOrder("good", "ok"));
        assertThat(result.getSuccessful(), hasSize(2));

        assertThat(result.getThrowables(), empty());
    }

    @Test
    public void testBuildAllFailed() {
        FanInResult<String> result = new FanInResult.Builder<String>().add(new TestException("bad"))
                                                                      .add(new TestException("no"))
                                                                      .build();

        assertThat(result.getSuccessful(), empty());

        assertThat(result.getThrowables(), hasSize(2));
        assertThat(result.getThrowables(), containsInAnyOrder(new TestException("no"), new TestException("bad")));
    }

    @Test
    public void testBuildMixed() {

        FanInResult<String> result = new FanInResult.Builder<String>().add("ok")
                                                                      .add("great")
                                                                      .add(new TestException("bad"))
                                                                      .add(new TestException("no"))
                                                                      .build();

        assertThat(result.getSuccessful(), hasSize(2));
        assertThat(result.getSuccessful(), containsInAnyOrder("great", "ok"));

        assertThat(result.getThrowables(), hasSize(2));
        assertThat(result.getThrowables(), containsInAnyOrder(new TestException("no"), new TestException("bad")));
    }

    @Test
    public void testConcatFullWithFull() {
        FanInResult<String> result1 = new FanInResult.Builder<String>().add("ok1")
                                                                       .add(new TestException("bad1"))
                                                                       .build();

        FanInResult<String> result2 = new FanInResult.Builder<String>().add("ok2")
                                                                       .add(new TestException("bad2"))
                                                                       .build();

        FanInResult<String> concatenated = result1.concat(result2);

        assertThat(concatenated.getSuccessful(), hasSize(2));
        assertThat(concatenated.getSuccessful(), containsInAnyOrder("ok2", "ok1"));

        assertThat(concatenated.getThrowables(), hasSize(2));
        assertThat(concatenated.getThrowables(), containsInAnyOrder(new TestException("bad1"),
                                                                    new TestException("bad2")));

    }

    @Test
    public void testConcatEmptyWithFull() {

        FanInResult<String> empty = new FanInResult.Builder<String>().build();
        FanInResult<String> full = new FanInResult.Builder<String>().add("ok")
                                                                    .add(new TestException("bad"))
                                                                    .build();

        FanInResult<String> concatenated = empty.concat(full);

        assertThat(concatenated.getSuccessful(), hasSize(1));
        assertThat(concatenated.getSuccessful(), containsInAnyOrder("ok"));

        assertThat(concatenated.getThrowables(), hasSize(1));
        assertThat(concatenated.getThrowables(), containsInAnyOrder(new TestException("bad")));
    }

    @Test
    public void testConcatFullWithEmpty() {
        FanInResult<String> empty = new FanInResult.Builder<String>().build();
        FanInResult<String> full = new FanInResult.Builder<String>().add("ok")
                                                                    .add(new TestException("bad"))
                                                                    .build();

        FanInResult<String> concatenated = full.concat(empty);

        assertThat(concatenated.getSuccessful(), hasSize(1));
        assertThat(concatenated.getSuccessful(), containsInAnyOrder("ok"));

        assertThat(concatenated.getThrowables(), hasSize(1));
        assertThat(concatenated.getThrowables(), containsInAnyOrder(new TestException("bad")));
    }

    public static class TestException extends Exception {

        private String name;

        public TestException(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TestException that = (TestException) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "TestException{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

}