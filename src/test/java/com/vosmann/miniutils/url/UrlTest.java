package com.vosmann.miniutils.url;

import org.junit.Test;

import static com.google.common.collect.ImmutableList.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UrlTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullScheme() {
        new Url.Builder().scheme(null).host("a.com").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyScheme() {
        new Url.Builder().scheme("").host("a.com").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownScheme() {
        new Url.Builder().scheme("ftp").host("a.com").build();
    }

    @Test
    public void testDefaultScheme() {
        assertThat(new Url.Builder().host("a.com").build().getFullUrl(), is("http://a.com"));
    }

    @Test
    public void testHttpsScheme() {
        Url url = new Url.Builder().scheme("https").host("a.com").build();

        assertThat(url.getFullUrl(), is("https://a.com"));
        assertThat(url.getScheme(), is("https"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullHost() {
        new Url.Builder().host(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyHost() {
        new Url.Builder().host("").build();
    }

    @Test
    public void testOkHost() {
        Url url = new Url.Builder().host("abc").build();

        assertThat(url.getFullUrl(), is("http://abc"));
        assertThat(url.getHost(), is("abc"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPortTooSmall() {
        new Url.Builder().host("a.com").port(-1).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPortTooBig() {
        new Url.Builder().host("a.com").port(65536).build();
    }

    @Test
    public void testPortJustRight() {
        Url url = new Url.Builder().host("a.com").port(1024).build();

        assertThat(url.getFullUrl(), is("http://a.com:1024"));
        assertThat(url.getPort(), is(1024));
    }

    // path elements
    @Test(expected = NullPointerException.class)
    public void testNullPathElement() {
        new Url.Builder().host("a.com").pathElement(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPathElement() {
        new Url.Builder().host("a.com").pathElement("").build();
    }

    @Test
    public void testOkPathElements() {
        Url url = new Url.Builder().host("a.com").pathElement("one").pathElement("two").build();

        assertThat(url.getFullUrl(), is("http://a.com/one/two"));
        assertThat(url.getPathElements(), is(of("one", "two")));
    }

    @Test
    public void testOkPathElementsWithPort() {
        Url url = new Url.Builder().host("a.com").port(300).pathElement("one").pathElement("two").build();

        assertThat(url.getFullUrl(), is("http://a.com:300/one/two"));
        assertThat(url.getPathElements(), is(of("one", "two")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyParamName() {
        new Url.Builder().host("a.com").queryParameter("", "some value").build();
    }

    @Test(expected = NullPointerException.class)
    public void testNullParamName() {
        new Url.Builder().host("a.com").queryParameter(null, "some value").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyParamValue() {
        new Url.Builder().host("a.com").queryParameter("some name", "").build();
    }

    @Test(expected = NullPointerException.class)
    public void testNullParamValue() {
        new Url.Builder().host("a.com").queryParameter("some name", null).build();
    }

    @Test
    public void testOkOneParam() {
        Url url = new Url.Builder().host("a.com").queryParameter("one", "two").build();

        assertThat(url.getFullUrl(), is("http://a.com?one=two"));
        assertThat(url.getQueryParameter("one"), is("two"));
    }

    @Test
    public void testOkTwoParams() {
        Url url = new Url.Builder().host("a.com").queryParameter("one", "two").queryParameter("three", "four").build();

        assertThat(url.getFullUrl(), is("http://a.com?one=two&three=four"));
        assertThat(url.getQueryParameter("one"), is("two"));
        assertThat(url.getQueryParameter("three"), is("four"));
    }

    @Test
    public void testOkParamsWithPath() {
        Url url = new Url.Builder().host("a.com")
                                   .pathElement("bat")
                                   .pathElement("man")
                                   .queryParameter("one", "two")
                                   .queryParameter("three", "four")
                                   .build();

        assertThat(url.getFullUrl(), is("http://a.com/bat/man?one=two&three=four"));
        assertThat(url.getQueryParameter("one"), is("two"));
        assertThat(url.getQueryParameter("three"), is("four"));
    }

}

