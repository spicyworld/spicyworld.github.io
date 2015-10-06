import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.*;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;


public class CookBook {
	public static void main(String[] args) {
		String templatePath = "/Volumes/Pearson/spicyworld/";
		String processor = "/Users/vghosam/Documents/workspace/test/src/CookBook.java";
		SiteBuilder.selfCopy(templatePath + "template/CookBook.java", processor);
		try {
			createPdf("SpicyWorld-CookBook.pdf", templatePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void createPdf(String filename, String path) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(path + filename));
        document.open();
        
        Image image1 = Image.getInstance(path + "recipeimages/dum-aloo.jpg");
        image1.setAbsolutePosition(10, 20);
        //image1.scalePercent(60f);
        image1.scaleAbsolute(575f, 400f);
        document.add(image1);
        
        //document.add(new Paragraph("Hello World!"));
        document.close();
    }
}