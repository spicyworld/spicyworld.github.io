import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lowagie.text.Anchor;
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
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfPageLabels;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;



public class CookBook {
	public static String templatePath = "/Volumes/FS/spicyworld/";
	public static String srcImgePath = "/Volumes/FS/amitava/recipeimages";
	public static List pageMap = new ArrayList();
	public static int waterMarkStart = 3;
	public static int insertPageStart = 2;
	
	public static void main(String[] args) {
		String processor = "/Users/aghosh/Documents/workspace/test/src/CookBook.java";
		SiteBuilder.selfCopy(templatePath + "template/CookBook.java", processor);
		
		try {
			createPdf("tmp.pdf");
			generateTOC("tmp.pdf", "tmp1.pdf", 0);
			waterMarkPDF("tmp1.pdf", "Spicy-World-Cook-Book.pdf");
			//embedJS("sp.pdf", "Spicy-World-Cook-Book.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			File f = new File(templatePath + "tmp.pdf");
			f.delete();
			f = new File(templatePath + "tmp1.pdf");
			f.delete();
		}
	}
	
	public static void embedJS (String src, String dest) throws Exception{
		
	}
	
	public static void generateTOC(String src, String dest, int start) throws Exception{
		boolean breakFlag = true;
		try {
		      PdfReader reader = new PdfReader(templatePath + src);
		      PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(templatePath + dest));

		      PdfContentByte over;
		      BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);

		      stamp.insertPage(insertPageStart, PageSize.A4);
		      over = stamp.getOverContent(insertPageStart);
		      over.beginText();
		      over.setFontAndSize(bf, 11);
		      int counter = 2;
		      over.showTextAligned(com.lowagie.text.Element.ALIGN_MIDDLE, "Table of Contents", 265, 800, 0);
		      for(int i=start;i<pageMap.size(); i++){
		    	  String[] data = ((String) pageMap.get(i)).split("##");
		    	  over.showTextAligned(com.lowagie.text.Element.ALIGN_LEFT, data[1], 40, 800 - (counter*20), 0);
				  
				  //over.setAction(new PdfAction("#" + data[2]), 800 - (counter*20), 40, 100, 20);
		    	  
				  over.showTextAligned(com.lowagie.text.Element.ALIGN_RIGHT, data[0], 540, 800 - (counter*20), 0);
		    	  if (counter == 39 && i < pageMap.size()) {
		    		  breakFlag = false;
			    	  over.endText();
			      	  stamp.close();
			      	  moveFile(templatePath + dest, templatePath + src);
			          waterMarkStart = waterMarkStart + 1;
			    	  insertPageStart = insertPageStart + 1;
		    		  generateTOC(src, dest, i+1);
		    		  break;
		    	  }
		    	  counter ++;
			  }
		      if (breakFlag) {
		    	  over.endText();
		      	  stamp.close();
		      }
		    } catch (Exception de) {
		      de.printStackTrace();
		    }
    }
	
	private static void moveFile (String src, String dest) {
		File afile =new File(src);
		afile.renameTo(new File(dest));
	}
	
	public static void waterMarkPDF(String src, String dest) throws Exception {
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
		int i = waterMarkStart;
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
			lineText = "Page " + (i - waterMarkStart);
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
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(templatePath + filename));
        document.open();
        
        /*
        Image image1 = Image.getInstance("/Volumes/FS/sp-graphics/test.jpg");
        image1.setAbsolutePosition(0, 770);
        document.add(image1);
        */
        
        Image image1 = Image.getInstance("/Volumes/FS/sp-graphics/cover.jpg");
        image1.setAbsolutePosition(0, 0);
        image1.scaleAbsolute(595f, 950f);
        document.add(image1);
        
        
        Font f = new Font(Font.COURIER, 32.0f, Font.NORMAL, Color.WHITE);
        Paragraph heading = new Paragraph(com.lowagie.text.Element.ALIGN_RIGHT, "Spicy World", f);
        heading.setAlignment(2);
        heading.setSpacingAfter(15f);
        heading.setSpacingBefore(20f);
        document.add(heading);
        
        f = new Font(Font.COURIER, 10.0f, Font.HELVETICA, Color.WHITE);
        heading = new Paragraph(com.lowagie.text.Element.ALIGN_RIGHT, "From Arpita's Kitchen", f);
        heading.setAlignment(2);
        heading.setSpacingAfter(5f);
        document.add(heading);
        
        PdfContentByte cb = pdfWriter.getDirectContent(); 

        cb.setLineWidth(2.0f);	// Make a bit thicker than 1.0 default 
        cb.setGrayStroke(0.95f); // 1 = black, 0 = white 
        float x = 0f; 
        float y = 770f; 
        cb.moveTo(x, y); 
        cb.lineTo(660, y); 
        cb.stroke(); 
        
        // Read DATA XML
        File fXmlFile = new File(templatePath + "template/data.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("element");
		//nList.getLength()
		for (int temp = 0; temp < 8; temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				createRecipePages(document, eElement, temp + 1, pdfWriter);
			}
		}
        
        document.close();
    }
	
	public static void createRecipePages(Document document, Element eElement, int i, PdfWriter pdfWriter) {
		try {
			Paragraph heading = null;
			String title = eElement.getElementsByTagName("title").item(0).getTextContent();
			String steps = eElement.getElementsByTagName("process").item(0).getTextContent();
			String pic = eElement.getElementsByTagName("pic").item(0).getTextContent();
			String url = eElement.getElementsByTagName("url").item(0).getTextContent();
			String img = pic.replace("recipeimages", srcImgePath);
			BufferedImage bimg = ImageIO.read(new File(img));
			int width          = bimg.getWidth();
			int height         = bimg.getHeight();
			
		    if ((width < height)) {
		    	return;
		    }
		    
		    if (steps.contains("recipeimages")) {
		    	steps = steps.replace("recipeimages", srcImgePath);
		    	steps = removeImage(steps);
		    }
			document.newPage();
			pageMap.add(pdfWriter.getPageNumber() - 1 + "##" + title + "##" + url);
			
			heading = new Paragraph("", new Font(Font.HELVETICA, 15f, Font.BOLD));
			document.add(heading);

			
			Anchor source_point = new Anchor(title, new Font(Font.HELVETICA, 15f, Font.BOLD));
			source_point.setName(url);
			document.add(source_point);
			
			String desc = eElement.getElementsByTagName("shortDesc").item(0).getTextContent();
			desc = html2text(desc);
			heading = new Paragraph(desc);
			heading.setSpacingBefore(5f);
			document.add(heading);
			
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
			
	        heading = new Paragraph("", new Font(Font.HELVETICA, 15f, Font.BOLD));
	        heading.setSpacingAfter(10f);
	        document.add(heading);
	        
	        multiColumnData(document, eElement, steps);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void multiColumnData(Document document, Element eElement, String steps) {
		try {
			MultiColumnText mct = new MultiColumnText();
			mct.setColumnsRightToLeft(false);
			mct.addRegularColumns(document.left(), document.right(), 80f, 2);
			
			mct.addElement(new Paragraph("Ingredients", new Font(Font.HELVETICA, 13f, Font.BOLD)));
			String ing = eElement.getElementsByTagName("ingrediants").item(0).getTextContent();
			mct.addElement(new Paragraph(html2textList(ing), new Font(Font.HELVETICA, 11f, Font.NORMAL)));
			
			mct.addElement(new Paragraph("Steps", new Font(Font.HELVETICA, 13f, Font.BOLD)));
			mct.addElement(new Paragraph(html2textList(steps), new Font(Font.HELVETICA, 11f, Font.NORMAL)));
			
			document.add(mct);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static String html2text(String html) {
	    return Jsoup.parse(html).text();
	}
	
	public static String html2textList(String html) {
		html = html.replace("> <", "><");
		html = html.replace(" class='internalLI'", "");
		html = html.replace("&#8457;", "F");
		html = html.replace("<li>", "");
		html = html.replace("</li>", "\n");
		html = html.replace("<div>", "");
		html = html.replace("</div>", "\n");
		html = html.replace("<ul>", "").replace("</ul>", "");
		return html.trim();
	}
	
	public static String removeImage(String content) {
		if(content != null) {
			content = "<ul>" + content + "</ul>";
			org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(content);
		    document.select("img").remove();
		    content = document.toString();
		    content = content.replace("div>", "li>");
		    content = content.replace("span>", "li>");
		    content = content.substring(content.indexOf("<ul>"), content.indexOf("</ul>") + 5);
		    content = content.replace("\n", "").replace("    ", "").replace("   ", "");
		}
		return content;
	}
}
