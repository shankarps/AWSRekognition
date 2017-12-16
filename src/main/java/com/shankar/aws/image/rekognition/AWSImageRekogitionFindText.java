package com.shankar.aws.image.rekognition;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.TextDetection;


public class AWSImageRekogitionFindText {

   public void process(String filePath, String photo) throws Exception {
      
  
      AWSCredentials credentials;
      try {
          credentials = new ProfileCredentialsProvider("default").getCredentials();
      } catch(Exception e) {
         throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
          + "Please make sure that your credentials file is at the correct "
          + "location (/Users/userid/.aws/credentials), and is in a valid format.", e);
      }


      AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder
              .standard()
              .withRegion(Regions.US_EAST_1)
              .withCredentials(new AWSStaticCredentialsProvider(credentials))
              .build();
      
      Path path = Paths.get(filePath + photo);
      ByteBuffer bytes = ByteBuffer.wrap(Files.readAllBytes(path));

      
      DetectTextRequest request = new DetectTextRequest()
              .withImage(new Image().withBytes(bytes));
    

      try {
         DetectTextResult result = rekognitionClient.detectText(request);
         List<TextDetection> textDetections = result.getTextDetections();

         System.out.println("Detected lines and words for " + photo);
         for (TextDetection text: textDetections) {
        	 
        	 //print parent words only. These have parent id == null.
        	 //non-parent words are part of other sentences already detected.
        	 if(text.getParentId() == null) {
                 System.out.print(" Word: " + text.getDetectedText());
                 System.out.print(", Confidence: " + text.getConfidence().toString());
                 System.out.print(", Id : " + text.getId());
                 System.out.print(", Parent Id: " + text.getParentId());
                 System.out.print(", Type: " + text.getType());
                 System.out.println();
        	 }
         }
      } catch(AmazonRekognitionException e) {
         e.printStackTrace();
      }
      
      rekognitionClient.shutdown();
   }
}
