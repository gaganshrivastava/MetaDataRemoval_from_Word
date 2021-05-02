# MetaDataRemoval_from_Word
 Remove meta data from word file

Sanitize Metadata from Word Files
I used the Aspose tool to remove the metadata from the word document file. Lambda function take the Input in JSON format, validate the input JSON then download the file from S3 bucket into local repository (‘/tmp/’) folder, remove the metaData properties from the file and upload it on S3 bucket(‘sanitize bucket’). 

Implementation
Coding Language  : Java
Library/Tools used : Eclipse,Aspose, AWS S3, AWS Lambda

Aspose is an open source tool which is used to perform multiple tasks on the words and PDF files. We can use DotNet, Java, C# for the implementation. I have used Java to implement metaData removal functionality.

Create a Lambda function, add the required roles to it from the Permission setting inside Configuration. Role likeAmazonS3FullAccess. 

After creating the lambda function setup the code in local. Steps to use the Aspose Library in java are:
One way to use this Tool is import the library in our project. Steps are :
 Open Eclipse > Project Explorer > (right click) New > Java Project (project) 
Download the jar of Aspose from https://downloads.aspose.com/words/java/new-releases/aspose.words-for-java-21.4/
Open the project which you created > Reference Library > (right click) Build Path>Configure Build Path> Add external Jars >”get the jar from the downloaded location” > Apply 


	2. TO Use the Apsoe tool using Maven Project and in Lambda function:
Open Eclipse > Project Explorer > (right click) New > Maven Project 

Get the latest version of Aspose from:https://downloads.aspose.com/words/java
In pom.xml add the dependencies.


Add the AWS lambda  dependencies.

Reference to set up the code and use Java in Lambda Function.
For Aspose
https://repository.aspose.com/webapp/#/artifacts/browse/tree/General/repo/com/aspose/aspose-words/21.3

https://apireference.aspose.com/words/java/com.aspose.words/DocumentPropertyCollection
https://apireference.aspose.com/words/java/com.aspose.words/BuiltInDocumentProperties


To add the aspose library use :: have to add the repository and dependencies in pom.xml
https://docs.aspose.com/total/java/configuration-and-using-aspose-total-java-for-maven/

https://stackoverflow.com/questions/36463310/how-to-read-a-file-in-aws-lambda-function-written-in-java

To download the file and read from the S3 bucket
https://docs.aws.amazon.com/lambda/latest/dg/with-s3-example-deployment-pkg.html#with-s3-example-deployment-pkg-java

Making the JAVA Lambda function
https://www.youtube.com/watch?v=JeJ46YlpPqw



To upload the code on Lambda, we have to zip all the libraries and code together. Sep is :
Right click on the project > Run As > Maven build 
Check the console logs and see the build is successful. 
Copy the jar location , take the jar which has a bigger size.
In AWS lambda, upload the jar.


  
Input
{
  "files": [
    {
      "displayName": "CS351_HW1.pdf",
      "key": "a0aa0f2c-da69-4420-b5ea-bcd06b93ffb1/1617646540564/a0aa0f2c-da69-4420-b5ea-bcd06b93ffb116176465405641.vnd.openxmlformats-officedocument.wordprocessingml.document",
      "location": "https://unsanitized-bucket.s3.amazonaws.com//bb41fc75-4e28-4c9b-862e-fe79393aa8a2/1617215163866/bb41fc75-4e28-4c9b-862e-fe79393aa8a216172151638661.pdf"
    }
  ],
  "selectedOptions": {
    "stripMetaData": 1,
    "textExtract": 0,
    "virusScan": 0,
    "imageRecognition": 0,
    "convertDocument": 1,
    "documentClassification": 0,
    "redactPII": 0
  },
  "userID": "userID",
  "submitTime": "epsilonTime"
}


Output
{
  "statusCode": 200,
  "files": [
    {
      "displayName": "CS351_HW1.pdf",
      "key": "a0aa0f2c-da69-4420-b5ea-bcd06b93ffb1/1617646540564/a0aa0f2c-da69-4420-b5ea-bcd06b93ffb116176465405641.vnd.openxmlformats-officedocument.wordprocessingml.document",
      "location": "https://unsanitized-bucket.s3.amazonaws.com//bb41fc75-4e28-4c9b-862e-fe79393aa8a2/1617215163866/bb41fc75-4e28-4c9b-862e-fe79393aa8a216172151638661.pdf"
    }
  ],
  "selectedOptions": {
    "stripMetaData": 0,
    "textExtract": 0,
    "virusScan": 0,
    "imageRecognition": 0,
    "convertDocument": 0,
    "documentClassification": 0,
    "redactPII": 0
  }
}


