import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

	public static final String IMAGE_PATH = "data/image.data";

	public static void main(String[] args) throws IOException{
		//create the features that we will use to create the feature vectors for each image
		Feature[] features = generateFeatures(50);
		//load all of our images into a list (the images include their feature vector as a field)
		ArrayList<Image> images = loadImages(IMAGE_PATH, features, 100);

		////now we have all of our feature vectors for each image
		
		-prepend the dummy feature which is always true
		
		- create 51 random weights from 0.5 to -0.5
		
		-for 150 times, call the train()
	}


	public static void train(Image[] instances, double[] weights){
		
		-for each instance:
			-find the sum of all the weights multiplied with each instance's feature vector (non activated features in the feature vector will just result in us multiplying those weights by 0)
			
			-if our sum from above is over the 0 threshold classify as true, else classify as false
			
			-compare our classification from above to the ACTUAL class that we read in from the file, if we got it wrong, adjust THE WEIGHTS OF ALL OF THE ACTIVATED FEATURES FOR THIS IMAGE BY +1 OR -1
			
			^ we adjust the weights of all of the activated features because it's like e.g., these are the features that seem to signify that we have a true class image (because we know from the file that it is a true class image AND these are the features that are activated when we have one of those) AND we just wrongly classified it as false class, so we must not be valuing these features enough. (obviously we will end up increasing the weight of some features when we don't really want to e.g. a day time picture that activates a bad feature because it happens to have a black patch), but over time we should identify which features are activated for lots of true class images and which aren't
	
	-this method should print out its classification accuracy after every epoch so taht we can see it increase
	}


















	private static Feature[] generateFeatures(int amount) {
		Feature[] features = new Feature[amount];
		//create the desired amount of features
		Random randomGenerator = new Random(5);
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











	//THIS METHOD STOLEN FROM PROVIDED CODE
	private static ArrayList<Image> loadImages(String imagePath, Feature[] features, int imageAmount) throws IOException {


		ArrayList<Image> images = new ArrayList<>();

		 try{
	      Scanner scanner = new Scanner(new File(IMAGE_PATH));
	      int count = 0;
	      while(scanner.hasNext()){
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
		 for(Image each: images){
			 System.out.println("=====================");
			 System.out.println(each.outcomeClass);
			 for(int row = 0; row < 10; row++){
				 for(int col = 0; col < 10; col++){
					 System.out.print(each.blackWhiteMap[row][col] + " ");
				 }
				 System.out.println("");
			 }
		 }
		 //pass each image to generateFeatureVector method and then fill in that image's field with that feature vector
		 for(Image eachImage: images){
			 boolean[] featureVector = new boolean[features.length];
			 for(int i = 0; i < featureVector.length; i++){
				 featureVector[i] = checkFeatureAgainstImage(eachImage, features[i]);
			 }
			 eachImage.featureVector = featureVector;
			 assert(featureVector.length == 50);
		 }





		 return images;

	}

	/**
	 * takes a feature and an image. Returns true if that feature is activated by that image and false if it is not activated.
	 * @return
	 */
	private static boolean checkFeatureAgainstImage(Image image, Feature feature){





		return true;
	}



}
