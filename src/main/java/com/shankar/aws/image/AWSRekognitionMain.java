package com.shankar.aws.image;

import com.shankar.aws.image.rekognition.AWSImageRekogitionFindFaces;
import com.shankar.aws.image.rekognition.AWSImageRekogitionFindLabels;
import com.shankar.aws.image.rekognition.AWSImageRekogitionFindText;

public class AWSRekognitionMain {
	
	public static final String filePath = "./images/";

	public static void main(String[] args) throws Exception {
		
		AWSImageRekogitionFindLabels awsImageRekogitionFindLabels = new AWSImageRekogitionFindLabels();
		AWSImageRekogitionFindText awsImageRekogitionFindText = new AWSImageRekogitionFindText(); 
		AWSImageRekogitionFindFaces awsImageRekogitionFindFaces = new AWSImageRekogitionFindFaces();
		//check if name is provided in the arguments.
		String photo = "audi.jpg";
		
		if(args.length > 0) {
			photo = args[0];
		}
		
		//awsImageRekogitionFindLabels.process(filePath, photo);
		//awsImageRekogitionFindText.process(filePath, photo);
		awsImageRekogitionFindFaces.process(filePath, photo);
		
	}

}
