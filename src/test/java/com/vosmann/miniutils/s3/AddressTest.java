package com.vosmann.miniutils.s3;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AddressTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullBucket() {
        new Address.Builder().bucket(null).keyPart("key").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyBucket() {
        new Address.Builder().bucket("").keyPart("key").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsetBucket() {
        new Address.Builder().keyPart("key").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullKey() {
        new Address.Builder().bucket("buck").keyPart(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyKey() {
        new Address.Builder().bucket("buck").keyPart("").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsetKey() {
        new Address.Builder().bucket("buck").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullRegion() {
        new Address.Builder().bucket("buck").keyPart("key").region(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyRegion() {
        new Address.Builder().bucket("buck").keyPart("key").region("").build();
    }

    @Test
    public void testUnsetRegion() {
        new Address.Builder().bucket("buck").keyPart("key").build();
    }

    @Test
    public void testGetBucket() throws Exception {
        assertThat(new Address.Builder().bucket("b1").keyPart("k1").build().getBucket(), is("b1"));
    }

    @Test
    public void testGetKey() throws Exception {
        assertThat(new Address.Builder().bucket("b1").keyPart("k1").build().getKey(), is("k1"));
        assertThat(new Address.Builder().bucket("b1").keyPart("k1").keyPart("k2").build().getKey(), is("k1/k2"));
    }

    @Test
    public void testGetHttpUrl() throws Exception {
        assertThat(new Address.Builder().bucket("b1").keyPart("k1").keyPart("k2").build().getHttpUrl(),
                is("http://s3.amazonaws.com/b1/k1/k2"));
    }

    @Test
    public void testGetHttpUrlWithRegion() throws Exception {
        assertThat(new Address.Builder().bucket("b1").keyPart("k1").region("eu-central-1").build().getHttpUrl(),
                is("http://s3-eu-central-1.amazonaws.com/b1/k1"));
    }

}