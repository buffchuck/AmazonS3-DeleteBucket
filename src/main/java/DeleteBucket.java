import java.util.Iterator;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;


public class DeleteBucket {

    public static void main(String[] args) {

        String clientRegion = "us-east-1";
        String bucketName = "com-rob-epps-arnold-test-bucket-2";

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .withCredentials(new ProfileCredentialsProvider())
                    .build();

            /*  Delete all objects from the bucket. This is sufficient
                for unversioned buckets. For versioned buckets, when
                attempting to delete objects, Amazon s3 inserts
                delete markers for all objects, but doesn't delete the object versions before deleting
            */

            /*  To delete objects from versioned buckets, delete all the object versions before
                deleting the bucket
            */

            ObjectListing objectListing = s3Client.listObjects(bucketName);
            while (true) {
                Iterator<S3ObjectSummary> objIter = objectListing.getObjectSummaries().iterator();

                while(objIter.hasNext()) {
                    s3Client.deleteObject(bucketName, objIter.next().getKey());
                }

                /*  If the bucket contains many objects, the listObjects() call
                    might not return all of the objects in the first listing. Check
                    to see whether the listing was truncated. If so, retrieve the
                    next page of objects and delete them.
                */

                if (objectListing.isTruncated()) {
                    objectListing = s3Client.listNextBatchOfObjects(objectListing);
                } else {
                    break;
                }
            }

            /* Delete all object versions (required for versioned buckets).*/

            VersionListing versionList = s3Client.listVersions(new ListVersionsRequest().withBucketName(bucketName));
            while (true) {
                Iterator<S3VersionSummary> versionIter = versionList.getVersionSummaries().iterator();
                while(versionIter.hasNext()) {
                    S3VersionSummary vs = versionIter.next();
                    s3Client.deleteVersion(bucketName, vs.getKey(), vs.getVersionId());
                }
                if (versionList.isTruncated()) {
                    versionList = s3Client.listNextBatchOfVersions(versionList);
                } else {
                    break;
                }
            }
            /* After all objects and object versions are deleted, delete the bucket. */

            s3Client.deleteBucket(bucketName);

        } catch (AmazonServiceException e) {

            /*  The call was transmitted successfully but Amazon S3 couldn't process
                it, so it returned an error response.
             */
             e.printStackTrace();
        } catch (SdkClientException e) {

            /* Amazon S3 couldn't be contacted for a response, or the client couldn't
                parse the repsonse from Amazon S3.
             */
            e.printStackTrace();
        }

    }
}
