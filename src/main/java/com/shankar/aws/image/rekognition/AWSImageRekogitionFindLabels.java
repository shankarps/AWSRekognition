package com.shankar.aws.image.rekognition;

import java.io.IOException;
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
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;

public class AWSImageRekogitionFindLabels {

	public void process(String filePath, String photo) {

		AWSCredentials credentials;
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (/Users/userid/.aws/credentials), and is in a valid format.", e);
		}

		// US_WEST_2 is Oregon region. Rekognition works only in a few regions.
		// check AWS documentation for other regions, if needed.
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_WEST_2)
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

		Path path = Paths.get(filePath + photo);
		ByteBuffer bytes = null;
		try {
			bytes = ByteBuffer.wrap(Files.readAllBytes(path));
		} catch (IOException e1) {
			System.out.println("****File read error. Please ensure that file name & path are correct. ");
			e1.printStackTrace();
		}

		//get labels with 75% confidence.
		DetectLabelsRequest request = new DetectLabelsRequest().withImage(new Image().withBytes(bytes))
				.withMaxLabels(10).withMinConfidence(75F);

		try {
			DetectLabelsResult result = rekognitionClient.detectLabels(request);
			List<Label> labels = result.getLabels();

			System.out.println("******** Labels for " + photo);
			for (Label label : labels) {
				System.out.println("Label: "+label.getName() + " , Confidence : " + label.getConfidence().toString());
			}
			
			System.out.println("Finished the labels");
		} catch (AmazonRekognitionException e) {
			e.printStackTrace();
		}
		
		//This is an important step. Close the client, else the Thread will not end!
		rekognitionClient.shutdown();
	}
}