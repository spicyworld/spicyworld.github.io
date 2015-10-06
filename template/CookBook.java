
public class CookBook {
	public static void main(String[] args) {
		String templatePath = "/Volumes/Pearson/spicyworld/";
		String processor = "/Users/vghosam/Documents/workspace/test/src/CookBook.java";
		SiteBuilder.selfCopy(templatePath + "template/CookBook.java", processor);
	}
}