# AmazonS3-DeleteBucket
Simple one class Java application to prove how to delete objects in an AWS S3 bucket. 
Not only does the application delete the bucket but all the objects in the bucket before 
actually deleting the bucket since buckets containing objects cannot be deleted. The app
takes into account that the list of objects returned from the listObject method on the 
AmazonS3 object may be truncated, therefore it checks to see if it has been truncated, if
so the AmazonS3 object is asked to list the next batch of objects.

This code sample can be found in AWS documentation. AWS SDK for Java is quite powerful!
