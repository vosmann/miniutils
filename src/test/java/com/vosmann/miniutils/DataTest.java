package com.vosmann.miniutils;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DataTest {

    @Test
    public void testInputStreamOfEmpty() {
        assertThat(Data.empty().toString(), is(""));
    }

    @Test
    public void testStringOfEmpty() throws IOException {
        assertThat(Data.empty().toInputStream().read(), is(-1));
    }

    @Test
    public void testSize() {
        assertThat(Data.empty().getSize(), is(0));
        assertThat(Data.from("abcde").getSize(), is(5));
    }

    @Test
    public void testEquals() {
        Data abc = Data.from("abc");
        Data abd = Data.from("abd");
        Data ab = Data.from("ab");

        assertTrue(abc.equals(Data.from("abc")));

        assertFalse(abc.equals(abd));
        assertFalse(abc.equals(ab));
    }

    @Test
    public void testLoad() {
        final Data fromStream = Data.from(4, new ByteArrayInputStream("abcd".getBytes()));
        final Data fromString = Data.from("abcd");
        assertThat(fromStream, is(fromString));
    }

    @Test
    public void testStreamShorterThanExpected() {
        final Data fromStream = Data.from(50, new ByteArrayInputStream("abcd".getBytes()));
        assertThat(fromStream.getSize(), is(4));
        assertThat(fromStream, is(Data.from("abcd")));
    }

    @Test
    public void testStreamLongerThanExpected() {
        final Data fromStream = Data.from(2, new ByteArrayInputStream("abcdefgh".getBytes()));
        assertThat(fromStream.getSize(), is(2));
        assertThat(fromStream, is(Data.from("ab")));
    }

    @Test
    public void testBadStream() throws IOException {
        final InputStream badStream = Mockito.mock(InputStream.class);
        Mockito.when(badStream.read(Matchers.<byte[]>any())).thenThrow(new IOException("This stream is bad."));

        final Data badStreamData = Data.from(5, badStream);

        assertThat(badStreamData.getSize(), is(0));
        assertThat(badStreamData.toString(), is(""));
        assertThat(badStreamData.toInputStream().read(), is(-1));
        assertThat(badStreamData, is(Data.empty()));
    }

    @Test
    public void testToInputStream() throws IOException {
        final Data data = Data.from("1234");
        final InputStream stream = data.toInputStream();
        assertThat((char) stream.read(), is('1'));
        assertThat((char) stream.read(), is('2'));
        assertThat((char) stream.read(), is('3'));
        assertThat((char) stream.read(), is('4'));
        assertThat(stream.read(), is(-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadLongSize() {
        final InputStream fakeStream = null;
        Data.from(Integer.MAX_VALUE + 1L, fakeStream);
    }

    @Test
    public void testStreamAvailable() throws IOException {
        final InputStream stream = new ByteArrayInputStream("ab".getBytes());
        assertThat(stream.available(), is(2));
        stream.read();
        assertThat(stream.available(), is(1));
        stream.read();
        assertThat(stream.available(), is(0));
    }

}
