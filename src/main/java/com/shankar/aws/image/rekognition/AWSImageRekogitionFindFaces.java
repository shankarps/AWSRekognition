package com.shankar.aws.image.rekognition;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import javax.imageio.ImageIO;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AgeRange;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.Attribute;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.Emotion;
import com.amazonaws.services.rekognition.model.Eyeglasses;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.Gender;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Smile;
import com.amazonaws.util.IOUtils;
public class AWSImageRekogitionFindFaces {

public void process(String filePath, String photo) throws Exception {

  //Get Rekognition client
  AWSCredentials credentials = null;
  try {
     credentials = new ProfileCredentialsProvider("default").getCredentials();
  } catch (Exception e) {
     throw new AmazonClientException("Cannot load the credentials: ", e);
  }

  AmazonRekognition amazonRekognition = AmazonRekognitionClientBuilder
     .standard()
     .withRegion(Regions.US_WEST_2)
     .withCredentials(new AWSStaticCredentialsProvider(credentials))
     .build();


  // Load image
  ByteBuffer imageBytes=null;
  BufferedImage image = null;

  try (InputStream inputStream = new FileInputStream(new File(filePath + photo))) {
     imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));

  }
  catch(Exception e)
  {
      System.out.println("Failed to load file " + photo);
      System.exit(1);
  }

  //Get image width and height
  InputStream imageBytesStream;
  imageBytesStream = new ByteArrayInputStream(imageBytes.array());

  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  image=ImageIO.read(imageBytesStream);
  //ImageIO.write(image, "jpg", baos);

  int height = image.getHeight();
  int width = image.getWidth();

  System.out.println("Image Information:");
  System.out.println(photo);
  System.out.println("Image Height: " + Integer.toString(height));
  System.out.println("Image Width: " + Integer.toString(width));

  //Call detect faces and show face age and placement

  try{
    DetectFacesRequest request = new DetectFacesRequest()
           .withImage(new Image()
              .withBytes((imageBytes)))
           .withAttributes(Attribute.ALL);


      DetectFacesResult result = amazonRekognition.detectFaces(request);
      List <FaceDetail> faceDetails = result.getFaceDetails();
      
      if(faceDetails == null || faceDetails.isEmpty()) {
    	  System.out.println("No faces detected");
      }

      System.out.println("*********   Faces found : "+faceDetails.size());
      
      for (FaceDetail face: faceDetails) {
          addBoundingBoxPositions(image, height,
                  width,
                  face.getBoundingBox(),
                  result.getOrientationCorrection());
          


          ImageIO.write(image, "jpg", new File("./images/"+photo+"_outut.jpg"));
          
          Gender gender = face.getGender();
          Smile smile = face.getSmile();
          List<Emotion> emotions = face.getEmotions();
          Eyeglasses glasses = face.getEyeglasses();
          
          AgeRange ageRange = face.getAgeRange();
          
          System.out.println("The detected face is estimated to be between "
               + ageRange.getLow().toString() + " and " + ageRange.getHigh().toString()
               + " years old. The gender is "+gender.getValue()+ ". The person is "+ (smile.isValue() ? "smiling" : "not smiling") +"."
               + " The person is "+ (glasses.isValue() ? "wearing " : "not wearing ") +"eyeglasses.");
          	System.out.print("Emotions shown ");
          	emotions.stream().filter(e -> (e.getConfidence() > 95.0F)).forEach( e -> System.out.print(e.getType() + ", "));
            System.out.println();
       }

   } catch (AmazonRekognitionException e) {
      e.printStackTrace();
   }

  amazonRekognition.shutdown();
}


public static void addBoundingBoxPositions(BufferedImage image, int imageHeight, int imageWidth, BoundingBox box, String rotation) {

  float left = 0;
  float top = 0;

  if(rotation==null){
      //System.out.println("No estimated estimated orientation. Check Exif data.");
      return;
  }
  //Calculate face position based on image orientation.
  switch (rotation) {
     case "ROTATE_0":
        left = imageWidth * box.getLeft();
        top = imageHeight * box.getTop();
        break;
     case "ROTATE_90":
        left = imageHeight * (1 - (box.getTop() + box.getHeight()));
        top = imageWidth * box.getLeft();
        break;
     case "ROTATE_180":
        left = imageWidth - (imageWidth * (box.getLeft() + box.getWidth()));
        top = imageHeight * (1 - (box.getTop() + box.getHeight()));
        break;
     case "ROTATE_270":
        left = imageHeight * box.getTop();
        top = imageWidth * (1 - box.getLeft() - box.getWidth());
        break;
     default:
        System.out.println("No estimated orientation information. Check Exif data.");
        return;
  }

  Graphics2D graph = image.createGraphics();
  Graphics2D g2d = image.createGraphics();
  g2d.setColor(Color.YELLOW);
  g2d.setStroke(new BasicStroke(3));
  g2d.drawRect(Math.round(left), Math.round(top), Math.round(imageHeight * box.getHeight()), Math.round(imageWidth * box.getWidth()));
  g2d.dispose();

  
  //Display face location information.
  /*
  System.out.println("Left: " + String.valueOf((int) left));
  System.out.println("Top: " + String.valueOf((int) top));
  System.out.println("Face Width: " + String.valueOf((int)(imageWidth * box.getWidth())));
  System.out.println("Face Height: " + String.valueOf((int)(imageHeight * box.getHeight())));
  */
  }
}
 