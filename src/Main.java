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

		////now create the 2d array of 1 feature vector per image
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
		 //so now we should have all of the images in a list and we just need to create their feature vectors
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







		 return images;

	}

	/**
	 * takes a feature and an image. Returns true if that feature is activated by that image and false if it is not activated.
	 * @return
	 */
	private static boolean checkFeature(Image image, Feature feature){





		return true;
	}



}
