import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

	public static String IMAGE_PATH = "data/image.data";
	public static String TEST_PATH = "data/SamCostiganTestData.txt";
	public static double WEIGHT_RATE =  0.004;
	public static final boolean verbose = false;


	public static void main(String[] args) throws IOException{

		//check if we are running without args from the commandline
		if(args.length == 0){
			runAlgorithm(true);
			return;
		}
		//else we are running from commandline
		boolean testPhase;
		if(args.length == 2){
			testPhase = true;
			IMAGE_PATH = args[0];
			TEST_PATH = args[1];
		}else{
			testPhase = false;
			IMAGE_PATH = args[0];
		}
		runAlgorithm(testPhase);
		if(!testPhase){
			System.out.println("no path to test file provided, so skipped the testing step.");
		}






	}


	/**
	 * manages a full run through of the algorithm, including getting the data from file, creating features, training weights and then testing the weights and features.
	 * @param testPhase
	 * @throws IOException
	 */
	private static void runAlgorithm(boolean testPhase) throws IOException {

		//create the features that we will use to create the feature vectors for each image
		Feature[] features = generateFeatures(50);

		//load all of our images into a list (the images include their feature vector as a field)
		ArrayList<Image> images = loadImages(IMAGE_PATH, features, 100);

		//create our list of 51 weights that we will train
		double[] weights = generateWeights();


		//now we have our random weights and a feature vector for every image, we should train our rates by running them over the instances many times
		boolean perfectAccuracy = false;
		for(int i = 0; i <  1000; i++){
			if(train(images, weights, verbose)){
				System.out.println("\n" + "PRINTING TRAINING INFORMATION: \n");
				System.out.println("\n" + i + " iterations were necessary to get perfect accuracy with the weight adjustment rate: " + WEIGHT_RATE);
				perfectAccuracy = true;
				break;
			}
		}
		if(!perfectAccuracy){
			System.out.println("\n" + "PRINTING TRAINING INFORMATION: \n");
			System.out.println("\n" + "we completed our 1000 training iterations, but we failed to attain perfect accuracy. Try running the program again");
		}

		//print out the features and their weights as stipulated in task
		System.out.println("\n" + "note that when the weight adjustment rate is very low (I am using 0.004), it might not appear that there is much difference in the weights. They ARE very different relative to one another though.");
		System.out.println("\n" + "printing feature/weight row|col information after training (dummy feature information omitted) : \n");
		for(int i = 0; i < features.length; i++){
			//System.out.println("the feature is like this (pixel coordinates given in the form: row|col):");
			System.out.println("Pixel positions: " + features[i].row[0] + "|" + features[i].col[0] + ", " + features[i].row[1] + "|" + features[i].col[1] + ", " + features[i].row[2] + "|" + features[i].col[2] + ", " + features[i].row[3] + "|" + features[i].col[3] + "    Signs: " +  features[i].sign[0] + ", "+ features[i].sign[1] + ", "+ features[i].sign[2] + ", "+ features[i].sign[3] + "    Weight: " + weights[i + 1] + "\n\n");
		}

		if(testPhase){
			//so now we should apply our weights and features to the test data
			System.out.println("\n" + "PRINTING TESTING INFORMATION: \n");
			ArrayList<Image> testImages = loadImages(TEST_PATH, features, 10);
			assert(testImages.size() == 10): testImages.size();
			test(testImages, weights);
		}




	}


	/**
	 * This is used once we have trained our weights for a certain list of features to test those weights/features out on an unseen data set
	 * @param testImages the new, unseen testing images (with feature vectors generated from the features that we used on the training data)
	 * @param weights the trained weights
	 */
	private static void test(ArrayList<Image> testImages, double[] weights) {
		assert(testImages.size() == 10);
		int correctClassificationCount = 0;
		for(Image eachInstance: testImages){
			//first, find the score of this instance based upon its activated features and the weights
			double sumScore = 0;
			assert(weights.length == 51);
			for(int i = 0; i < weights.length; i ++){
				int featureValue;
				if(eachInstance.featureVector[i]){
					featureValue = 1;
				}else{
					assert(!eachInstance.featureVector[i]);
					featureValue = 0;
				}
				sumScore += (featureValue * weights[i]);
			}

			//if our score is greater than 0, then we are classifying it as the "yes"/true/X class. Else it is the "other"/false/O class
			boolean predictedClass;
			if(sumScore > 0){
				predictedClass = true;
			}else{
				assert(sumScore != 0);
				predictedClass = false;
			}

			//if we predicted the class of this instance correctly, increment our correctly guessed count
			if(predictedClass == eachInstance.outcomeClass){
				correctClassificationCount++;
				System.out.println("\n :) :D :-) correctly classified the following instance from the test data: \n ");
				drawImages(eachInstance);
			}else{
				System.out.println("\n :( :[ :'( misclassified the following instance from the test data: \n ");
				drawImages(eachInstance);
			}

		}

			System.out.println("running the features and weights that were generated in the training step over some new, 'unseen' instances, gives us an accuracy of " + correctClassificationCount + "/" +  testImages.size());



	}



	public static boolean train(ArrayList<Image> instances, double[] weights, boolean verbose){

		int correctClassificationCount = 0;
		for(Image eachInstance: instances){
			//first, find the score of this instance based upon its activated features and the current weights
			double sumScore = 0;
			assert(weights.length == 51);
			for(int i = 0; i < weights.length; i ++){
				int featureValue;
				if(eachInstance.featureVector[i]){
					featureValue = 1;
				}else{
					assert(!eachInstance.featureVector[i]);
					featureValue = 0;
				}
				sumScore += (featureValue * weights[i]);
			}

			//if our score is greater than 0, then we are classifying it as the "yes"/true/X class. Else it is the "other"/false/O class
			boolean predictedClass;
			if(sumScore > 0){
				predictedClass = true;
			}else{
				assert(sumScore != 0);
				predictedClass = false;
			}

			//if we predicted the class of this instance correctly, do nothing. Else: alter the weights for the features that are activated for this instance
			if(predictedClass == eachInstance.outcomeClass){
				correctClassificationCount++;
			}else{
				//if we predicted wrong and the actual class is true, then we need to increase our weights
				if(eachInstance.outcomeClass){
					for(int i = 0; i < weights.length; i++){
						double featureValue;
						if(eachInstance.featureVector[i]){
							featureValue = WEIGHT_RATE;
						}else{
							featureValue = 0;
						}
						weights[i] += featureValue;
					}
				}else{//if we predicted wrong and the actual class is false, then we need to decrease our weights
					for(int i = 0; i < weights.length; i++){
						double featureValue;
						if(eachInstance.featureVector[i]){
							featureValue = WEIGHT_RATE;
						}else{
							featureValue = 0;
						}
						weights[i] -= featureValue;
					}
				}
			}

		}

	if(verbose){
		System.out.println("during that epoch, we classified the following amount correctly: " + correctClassificationCount);
	}

		if(correctClassificationCount == instances.size()){
			return true;
		}
		return false;
	}








	public static double[] generateWeights() {
		double[] weights = new double[51];
		Random randomGenerator = new Random(1);
		for(int i = 0; i < weights.length; i++){
			weights[i] = randomGenerator.nextDouble() - 0.5;
		}
		return weights;
	}









	private static Feature[] generateFeatures(int amount) {
		Feature[] features = new Feature[amount];
		//create the desired amount of features
		Random randomGenerator = new Random(1);
		int pixels = 4;
		for(int i =0; i < amount; i++){
			//choose rows
			int[] rows = {randomGenerator.nextInt(10), randomGenerator.nextInt(10), randomGenerator.nextInt(10), randomGenerator.nextInt(10)};
			//choose cols
			int[] cols = {randomGenerator.nextInt(10), randomGenerator.nextInt(10), randomGenerator.nextInt(10), randomGenerator.nextInt(10)};
			//choose signs
			boolean[] signs = {randomGenerator.nextBoolean(), randomGenerator.nextBoolean(), randomGenerator.nextBoolean(), randomGenerator.nextBoolean()};

			features[i] = new Feature(rows, cols, signs);
		}
		assert(features.length == 50);
		return features;
	}











	//THIS METHOD MOSTLY STOLEN FROM PROVIDED CODE
	private static ArrayList<Image> loadImages(String imagePath, Feature[] features, int imageAmount) throws IOException {


		ArrayList<Image> images = new ArrayList<>();

		 try{
	      Scanner scanner = new Scanner(new File(imagePath));
	      int count = 0;
	      while(scanner.hasNext() && images.size() < imageAmount){
		    boolean[][] newImage = null;
		    boolean outcomeClass;

		     int rows = 10;
		    int cols = 10;

		      java.util.regex.Pattern bit = java.util.regex.Pattern.compile("[01]");

		      if (!scanner.next().equals("P1")) System.out.println("Not a P1 PBM file" );
		      String category = scanner.next().substring(1);
		      if (category.equals("Yes")){
		    	  outcomeClass = true;
		      }else{
		    	  outcomeClass = false;
		      }
		      rows = scanner.nextInt();
		      cols = scanner.nextInt();

		      newImage = new boolean[rows][cols];





		      for (int r=0; r<rows; r++){
			for (int c=0; c<cols; c++){
			  newImage[r][c] = (scanner.findWithinHorizon(bit,0).equals("1"));
			}
		      }

		      images.add(new Image(newImage, outcomeClass));


		    }


	      scanner.close();
	      } catch(IOException e){System.out.println("Load from file failed"); }
		 //so now we should have all of the images in a list and we just need to create their feature vectors...
		/* for(Image each: images){
			 drawImages(each);
		 }*/

		 //pass each image to generateFeatureVector method and then fill in that image's field with that feature vector
		 for(Image eachImage: images){
			 boolean[] featureVector = new boolean[features.length + 1];
			 assert(featureVector.length  > 50);
			 //add in the dummy feature here
			 featureVector[0] = true;
			 //go over every image and fill our its feature vector
			 for(int i = 1; i < featureVector.length; i++){
		//		 System.out.println(i);
				 featureVector[i] = checkFeatureAgainstImage(eachImage, features[i - 1]);
			 }
			 int debug = 0;
			 for(boolean each: featureVector){
				 if(each){
					 debug++;
				 }
			 }
	//		 System.out.println(debug * 2 + "%");
			 assert(featureVector.length == 51);
			 eachImage.featureVector = featureVector;
			 assert(featureVector.length == 51);
		 }





		 return images;

	}


/**
 * draws a simple asci
 * @param images
 */
	private static void drawImages(Image image) {


			 for(int row = 0; row < 10; row++){
				 for(int col = 0; col < 10; col++){
					 if(image.blackWhiteMap[row][col]){
						 System.out.print("@");
					 }else{
						 System.out.print(" ");
					 }
				 }
				 System.out.println("");
			 }

	}

	/**
	 * takes a feature and an image. Returns true if that feature is activated by that image and false if it is not activated.
	 * @return
	 */
	private static boolean checkFeatureAgainstImage(Image image, Feature feature){

		int pixelSum = 0;

		for(int i = 0; i < 4; i++){
			if(image.blackWhiteMap[feature.row[i]][feature.col[i]] == feature.sign[i]){
				//System.out.println("activated the feature: " + feature.row[i] + feature.col[i] + feature.sign[i]);
				pixelSum++;
			}
		}


		if(pixelSum >= 3){
			return true;
		}else{
			return false;
		}
	}



}
