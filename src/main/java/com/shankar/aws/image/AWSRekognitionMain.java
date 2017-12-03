package com.shankar.aws.image;

import java.io.IOException;

import com.shankar.aws.image.rekognition.AWSImageRekogitionFindLabels;

public class AWSRekognitionMain {
	
	public static final String filePath = "./images/";

	public static void main(String[] args) throws Exception {
		
		AWSImageRekogitionFindLabels awsImageRekogitionFindLabels = new AWSImageRekogitionFindLabels();
		
		//check if name is provided in the arguments.
		String photo = "audi.jpg";
		
		if(args.length > 0) {
			photo = args[0];
		}
		
		awsImageRekogitionFindLabels.process(filePath, photo);
		
	}

}
