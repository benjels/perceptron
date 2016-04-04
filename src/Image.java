//pojo representation of an image. has a
public class Image {
	public final boolean[][] blackWhiteMap;
	public final boolean outcomeClass;

	public boolean[] featureVector;


	public Image(boolean[][] blackWhiteMap, boolean outcomeClass) {
		super();
		this.blackWhiteMap = blackWhiteMap;
		this.outcomeClass = outcomeClass;
	}



}
