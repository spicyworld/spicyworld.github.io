import java.awt.Label;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.MultiColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;



public class CookBook {
	public static String templatePath = "/Volumes/Pearson/spicyworld/";
	public static void main(String[] args) {
		String processor = "/Users/vghosam/Documents/workspace/test/src/CookBook.java";
		SiteBuilder.selfCopy(templatePath + "template/CookBook.java", processor);
		try {
			createPdf("tmp.pdf");
			manipulatePdf("tmp.pdf", "SpicyWorld.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			File f = new File(templatePath + "tmp.pdf");
			f.delete();
		}
		
	}
	
	public static void manipulatePdf(String src, String dest) throws Exception {
	    PdfReader reader = new PdfReader(templatePath + src);
	    int n = reader.getNumberOfPages();
	    PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(templatePath + dest));
	    // text watermark
	    Font f = new Font(Font.HELVETICA, 10);
	    Phrase p = new Phrase("(c) Spicy World", f);
	    // transparency
	    PdfGState gs1 = new PdfGState();
	    gs1.setFillOpacity(0.5f);
	    // properties
	    PdfContentByte over;
	    com.lowagie.text.Rectangle pagesize;
	    float x, y;
	    // loop over every page
	    for (int i = 2; i <= n; i++) {
	        over = stamper.getOverContent(i);
	        over.saveState();
	        over.setGState(gs1);
	        ColumnText.showTextAligned(over, com.lowagie.text.Element.ALIGN_CENTER, p, 540, 10, 0);
	       
	        over.restoreState();
	    }
	    stamper.close();
	}
	
	public static void createPdf(String filename) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(templatePath + filename));
        document.open();
        /*
        Image image1 = Image.getInstance(templatePath + "recipeimages/dum-aloo.jpg");
        image1.setAbsolutePosition(10, 20);
        //image1.scalePercent(60f);
        image1.scaleAbsolute(575f, 400f);
        document.add(image1);
        */
        
        Image image2 = Image.getInstance(templatePath + "images/site-logo.png");
        image2.scaleAbsolute(215f, 63f);//431 × 126
        //image2.setAbsolutePosition(200, 700);
        document.add(image2);
        
        Paragraph heading = new Paragraph("  By Arpita Ghosh Das", new Font(Font.HELVETICA, 12f, Font.ITALIC));
        heading.setSpacingAfter(5f);
        document.add(heading);
        
        
        // Read DATA XML
        File fXmlFile = new File(templatePath + "template/data.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("element");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				createRecipePages(document, eElement, temp + 1);
			}
		}
        
        
        
        //document.add(new Paragraph("Hello World!"));
        document.close();
    }
	
	public static void createRecipePages(Document document, Element eElement, int i) {
		try {
			 String steps = eElement.getElementsByTagName("process").item(0).getTextContent();
		    if (steps.contains("recipeimages")) {
		    	return;
		    }
			document.newPage();
			
			Paragraph heading = new Paragraph(eElement.getElementsByTagName("title").item(0).getTextContent(), new Font(Font.HELVETICA, 15f, Font.BOLD));
		    heading.setSpacingAfter(5f);
		    document.add(heading);
			
			String desc = eElement.getElementsByTagName("shortDesc").item(0).getTextContent();
			desc = html2text(desc);
			document.add(new Paragraph(desc));
			
			String pic = eElement.getElementsByTagName("pic").item(0).getTextContent();
			Image image1 = Image.getInstance(templatePath + pic);
	        image1.scaleAbsolute(520f, 293f);
	        document.add(image1);
	        
	        heading = new Paragraph("Ingredients", new Font(Font.HELVETICA, 13f, Font.BOLD));
		    heading.setSpacingAfter(4f);
		    document.add(heading);
		    
		    String ing = eElement.getElementsByTagName("ingrediants").item(0).getTextContent();
		    MultiColumnText columns = new MultiColumnText();
		    //float left, float right, float gutterwidth, int numcolumns
		    columns.addRegularColumns(36f, document.getPageSize().width(), 24f, 2);
		    
		    HTMLWorker htmlWorker = new HTMLWorker(document);
		    htmlWorker.parse(new StringReader(ing));
		    heading = new Paragraph("Steps", new Font(Font.HELVETICA, 13f, Font.BOLD));
		    heading.setSpacingAfter(4f);
		    document.add(heading);
		    htmlWorker.parse(new StringReader(steps));
		    
		    BaseFont bf_courier = BaseFont.createFont(BaseFont.COURIER, "Cp1252", false);
		    HeaderFooter footer = new HeaderFooter(new Phrase("Page ", new Font(bf_courier)), true);
	        footer.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
	        footer.setAlignment(com.lowagie.text.Element.ALIGN_LEFT);
	        document.setFooter(footer);
	        
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static String html2text(String html) {
	    return Jsoup.parse(html).text();
	}
}