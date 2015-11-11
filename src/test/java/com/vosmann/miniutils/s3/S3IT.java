package com.vosmann.miniutils.s3;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.vosmann.miniutils.Data;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore
public class S3IT {

    private static final String BUCKET = "integration-tests-throwaway-bucket-123";

    private AmazonS3Client client;
    private Lister lister;

    @Before
    public void setUp() {
        client = createClient();
        lister = new Lister(client);
        client.createBucket(BUCKET);
    }

    @After
    public void tearDown() {
        client.deleteBucket(BUCKET);
    }

    @Test
    public void testUploadAndDownloadString() {

        final String original = "hello there";

        final Address address = new Address.Builder().bucket(BUCKET).keyPart("test-string").build();

        final Data data = Data.from(original);
        new Upload(address, data.getSize(), data.toInputStream(), client).run();

        final StringDownload download = new StringDownload(address, data.getSize(), client);
        download.run();

        assertTrue("String not downloaded.", download.getResult().isPresent());
        assertThat(download.getResult().get(), is(original));

        new Deletion(lister.at(address), client).run();
    }

    @Test
    public void testStringDownloadWhenMultipleObjects() {

        final String prefix = "prefix-";
        new Upload(Address.of(BUCKET, prefix + "1"), 4, Data.from("test").toInputStream(), client).run();
        new Upload(Address.of(BUCKET, prefix + "2"), 4, Data.from("test").toInputStream(), client).run();

        final StringDownload download = new StringDownload(Address.of(BUCKET, prefix), 4, client);
        download.run();

        assertFalse("Couldn't have downloaded a string if there were multiple objects.", download.getResult().isPresent());

        new Deletion(lister.at(Address.of(BUCKET, prefix)), client).run();
    }

    @Test
    public void testUploadListingAndDeletion() {

        final Address catchall = Address.of(BUCKET, "deleteme-");

        new Upload(Address.of(BUCKET, "deleteme-1"), 4, Data.from("test").toInputStream(), client).run();
        new Upload(Address.of(BUCKET, "deleteme-2"), 4, Data.from("test").toInputStream(), client).run();
        final Listing afterUpload = lister.at(catchall);
        assertThat("The two uploaded files were not found.", afterUpload.get().size(), is(2));

        new Deletion(afterUpload, client).run();
        final Listing afterDeletion = lister.at(catchall);
        assertThat("The two uploaded files were not deleted.", afterDeletion.get().size(), is(0));
        assertThat("Unexpected error message from deleting two files.", afterDeletion.getErrorMessage().isPresent(),
                is(false));
    }

    private AmazonS3Client createClient() {
        final AmazonS3Client client = new AmazonS3Client(new BasicAWSCredentials("", ""));
        client.configureRegion(Regions.EU_WEST_1);
        return client;
    }

}