import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.BaseFont;
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
			manipulatePdf("tmp.pdf", "sp.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			File f = new File(templatePath + "tmp.pdf");
			f.delete();
		}
		
	}
	
	public static void manipulatePdf(String src, String dest) throws Exception {
		String filePath = templatePath + src;
		String tempFilePath = templatePath + dest;
		PdfReader reader = new PdfReader(filePath);
		int n = reader.getNumberOfPages();
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(
				tempFilePath));
		// watermarkText = watermarkText.toUpperCase();
		PdfContentByte under;
		PdfGState gstate = new PdfGState();
		gstate.setFillOpacity(0.35f);
		gstate.setStrokeOpacity(0.35f);
		BaseFont font = BaseFont.createFont(BaseFont.COURIER,
				BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
		int fontSize = 12;
		com.lowagie.text.Rectangle size = reader.getPageSizeWithRotation(1);
		int i = 2;
		while (i < n + 1) {
			under = stamper.getOverContent(i);
			i++;
			under.beginText();

			under.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
			under.setLineWidth(0.35f);
			
			under.setLineDash(0.4f, 0.2f, 0.2f);
			under.setFontAndSize(font, fontSize);
			String lineText = "© Spicy World";
			under.showTextAlignedKerned(com.lowagie.text.Element.ALIGN_BOTTOM, lineText, 470, 20, 0);
			//Page number
			lineText = "Page " + (i - 2);
			under.showTextAlignedKerned(com.lowagie.text.Element.ALIGN_BOTTOM, lineText, 30, 20, 0);
			
			under.setTextRenderingMode(PdfContentByte.LINE_JOIN_ROUND);
			under.setFontAndSize(font, 40);
			under.showTextAlignedKerned(com.lowagie.text.Element.ALIGN_BOTTOM, "http://spicyworld.in", 150, 220, 50);
			under.endText();
		}
		stamper.close();
	}
	
	public static void createPdf(String filename) throws Exception {
        Document document = new Document(PageSize.A4, 30, 30, 20, 70);
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
			String title = eElement.getElementsByTagName("title").item(0).getTextContent();
			String steps = eElement.getElementsByTagName("process").item(0).getTextContent();
			String pic = eElement.getElementsByTagName("pic").item(0).getTextContent();
			String img = templatePath + pic;
			BufferedImage bimg = ImageIO.read(new File(img));
			int width          = bimg.getWidth();
			int height         = bimg.getHeight();
			
		    if (steps.contains("recipeimages") || (width < height)) {
		    	return;
		    }
			document.newPage();
			
			Paragraph heading = new Paragraph(title, new Font(Font.HELVETICA, 15f, Font.BOLD));
		    heading.setSpacingAfter(5f);
		    document.add(heading);
			
			String desc = eElement.getElementsByTagName("shortDesc").item(0).getTextContent();
			desc = html2text(desc);
			document.add(new Paragraph(desc));
			
			float w = 520f;
			float h = 0.0f;
			if (w > width) {
				w = width;
			}
			h = (height*w) / width;
			Image image1 = Image.getInstance(img);
			image1.setAlt(title);
			
			image1.scaleAbsolute(w, h);
	        document.add(image1);
			
			
	        
	        /*heading = new Paragraph("Ingredients", new Font(Font.HELVETICA, 13f, Font.BOLD));
		    heading.setSpacingAfter(4f);
		    document.add(heading);
		    
		    String ing = eElement.getElementsByTagName("ingrediants").item(0).getTextContent();
		   
		    HTMLWorker htmlWorker = new HTMLWorker(document);
		    htmlWorker.parse(new StringReader(ing));
		    heading = new Paragraph("Steps", new Font(Font.HELVETICA, 13f, Font.BOLD));
		    heading.setSpacingAfter(4f);
		    document.add(heading);
		    htmlWorker.parse(new StringReader(steps));*/
	        multiColumnData(document, eElement);
		    
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void multiColumnData(Document document, Element eElement) {
		try {
			MultiColumnText mct = new MultiColumnText();
			mct.setColumnsRightToLeft(false);
			mct.addRegularColumns(document.left(), document.right(), 80f, 2);
			
			StyleSheet styles=new StyleSheet();
		    styles.loadTagStyle("ul","li","10,0");
			
			mct.addElement(new Paragraph("Ingredients", new Font(Font.HELVETICA, 13f, Font.BOLD)));
			String ing = eElement.getElementsByTagName("ingrediants").item(0).getTextContent();
			StringReader strReader = new StringReader(ing);
			ArrayList arrList = HTMLWorker.parseToList(strReader, styles);
			Paragraph para = new Paragraph(); 
			para.setFont(FontFactory.getFont("Courier",10,Font.NORMAL));
			for (int k = 0; k < arrList.size(); ++k) {                   
			    para.add((com.lowagie.text.Element)arrList.get(k)); 
			}
			mct.addElement(para);
			
			
		    
			mct.addElement(new Paragraph("Steps", new Font(Font.HELVETICA, 13f, Font.BOLD)));
			String steps = eElement.getElementsByTagName("process").item(0).getTextContent();
			strReader = new StringReader(steps);
			arrList = HTMLWorker.parseToList(strReader, styles);
			para = new Paragraph(); 
			para.setFont(FontFactory.getFont("Courier",10,Font.NORMAL));
			for (int k = 0; k < arrList.size(); ++k) { 
				para.add((com.lowagie.text.Element)arrList.get(k)); 
			}
			mct.addElement(para);
			
			document.add(mct);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static String html2text(String html) {
	    return Jsoup.parse(html).text();
	}
}