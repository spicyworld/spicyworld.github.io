import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FileGenerator {
	
	public static String buildNo = "?sessionId=108";

	public static void main(String[] args) {
		String basePath = "/Volumes/Pearson/spicyworld/";
		String templatePath = basePath;
		String processor = "/Users/vghosam/Documents/workspace/test/src/FileGenerator.java";
		String recipes_template = templatePath + "template/recipes.html";
		String recipes_data = "<table class=\"dataTable\">";
		String homeJSON = "var myData = [";
		String siteMapData = "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";
		siteMapData = siteMapData + staticEntriesSiteMap();
		String rssXMLData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><rss version=\"2.0\"><channel>"
				+ "<title>Spicy World</title><link>http://www.spicyworld.in</link>"
				+ "<description>Welcome to Spicy World by Arpita Ghosh Das. Easy and Simple Recipes make your cooking faster and your food delicious.</description>";
		try {

			File fXmlFile = new File(templatePath + "template/data.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("element");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					recipes_data = recepiData(recipes_data, eElement);
					createItemData(templatePath, eElement);
					if ("var myData = [".equals(homeJSON)) {
						homeJSON += createHomeJS(eElement);
					} else {
						homeJSON = homeJSON + "," + createHomeJS(eElement);
					}
					siteMapData += siteMapEntry(eElement);
					rssXMLData += populateRSSData(eElement);
				}
			}
			siteMapData = siteMapData + "</urlset>";
			homeJSON += "];";
			recipes_data += "</table>";
			String fileData = readFile(recipes_template);
			fileData = fileData.replace("##DATA_ENTRY##", recipes_data);
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			saveFile(templatePath + "recipes.html", fileData);
			saveFile(templatePath + "recipes.js", homeJSON);
			saveFile(basePath + "sitemap.xml", siteMapData);
			saveFile(templatePath + "rss.xml", rssXMLData.replace("&", "and") + "</channel></rss>");
		} catch (Exception e) {
			e.printStackTrace();
		}
		selfCopy(templatePath + "template/FileGenerator.java", processor);
		System.out.println("Processed ...");
	}
	
	public static String staticEntriesSiteMap() {
		return "<url><loc>http://www.spicyworld.in</loc></url>"
				+ "<url><loc>http://www.spicyworld.in/recipes.html</loc></url>"
				+ "<url><loc>http://www.spicyworld.in/feedback.html</loc></url>"
				+ "<url><loc>http://www.spicyworld.in/rss.xml</loc></url>"
				+ "<url><loc>http://amitava3g.github.io/sitemap.xml</loc></url>";
	}
	
	public static String populateRSSData(Element eElement) {
		String homeJSON = null;
		homeJSON = "<item><title>" + eElement.getElementsByTagName("title").item(0).getTextContent() + "</title><link>http://www.spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent()
		+ ".html</link><description>" + eElement.getElementsByTagName("shortDesc").item(0).getTextContent() + "</description></item>";
		return homeJSON;
	}
	
	public static String siteMapEntry(Element eElement) {
		String siteMapDataEntry = null;
		siteMapDataEntry = "<url><loc>http://www.spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html</loc></url>";
		return siteMapDataEntry;
	}

	public static String createHomeJS(Element eElement) {
		String homeJSON = null;
		homeJSON = "{\"title\":\"" + eElement.getElementsByTagName("title").item(0).getTextContent() + "\",\"thumb\":\"" + eElement.getElementsByTagName("pic").item(0).getTextContent() + "\",\"url\":\"" + eElement.getElementsByTagName("url").item(0).getTextContent()
		+ ".html\"}";
		return homeJSON;
	}
	
	public static void createItemData(String templatePath, Element eElement) {
		String out = "";
		String type = eElement.getElementsByTagName("type").item(0)
				.getTextContent();
		String itemType = "";
		if ("nonVegItem".equals(type)) {
			itemType = "Non-Vegetarian";
		} else {
			itemType = "Vegetarian";
		}
		
		out = "<div><div class='h2Class'><h1 style=\"font-size: 25px;display: inline;\">"
				+ eElement.getElementsByTagName("title").item(0).getTextContent()
				+ "</h1><span>("
				+ itemType
				+ ")</span><div class=\"fb-share-button\" data-href=\"http://spicyworld.in/"
				+ eElement.getElementsByTagName("url").item(0).getTextContent()
				+ ".html\" data-layout=\"button_count\"></div>"
				+ "<p class=\"descp\">" + eElement.getElementsByTagName("shortDesc").item(0).getTextContent() + "</p></div><br/>"
				+ "<div>"
				+ "<div class='div3Pos posLeft'><a class=\"group1\" title=\"" + eElement.getElementsByTagName("title").item(0).getTextContent() + "\" href=\"" + eElement.getElementsByTagName("pic").item(0).getTextContent() + buildNo + "\"><img alt='" + eElement.getElementsByTagName("title").item(0).getTextContent() 
				+ "' title='" + eElement.getElementsByTagName("title").item(0).getTextContent() + "' src='"
				+ eElement.getElementsByTagName("pic").item(0).getTextContent() + buildNo
				+ "' width='100%'/></a><br/><br/><div><h2 style=\"font-size: 23px; font-weight: normal; margin-bottom: 0px !important;padding: 0px !important;\">Ingredients</h2></div><br/>"
				+ eElement.getElementsByTagName("ingrediants").item(0)
						.getTextContent()
				+ "</div>"
				+ "<div class='div3Pos'><div><h2 style=\"font-size: 23px; font-weight: normal; margin-bottom: 0px !important;padding: 0px !important;\">Steps</h2></div><br/>"
				+ eElement.getElementsByTagName("process").item(0)
						.getTextContent()
				+ "<br/><div class='complete'>"
				+ eElement.getElementsByTagName("completionStatement").item(0)
						.getTextContent()
				+ "</div>"
				+ "<div class='garnishment'>"
				+ eElement.getElementsByTagName("garnishment").item(0)
						.getTextContent() + "</div></div>" + "</div></div>";
		String fileData = readFile(templatePath + "template/food-item.html");
		fileData = fileData.replace("##DATA_ENTRY##", out);
		fileData = fileData.replaceAll("##TITLE_DATA##", eElement
				.getElementsByTagName("title").item(0).getTextContent()
				+ " | Spicy World | Arpita's Kitchen");
		fileData = fileData.replace("##URL_DATA##", eElement
				.getElementsByTagName("url").item(0).getTextContent()
				+ ".html");
		fileData = fileData.replace("##DESC_DATA##", eElement
				.getElementsByTagName("shortDesc").item(0).getTextContent());
		fileData = fileData.replace("##IMG_DATA##", eElement
				.getElementsByTagName("pic").item(0).getTextContent() + buildNo);
		fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
		/*String keyword = Utility.getKeyword(eElement.getElementsByTagName("title").item(0).getTextContent() 
				+ " " + eElement.getElementsByTagName("shortDesc").item(0).getTextContent());*/
		String keyword = eElement.getElementsByTagName("title").item(0).getTextContent() + ", Arpita, kitchen, Spicy World, World of Spices, Spice, Food, Recipes, " + eElement.getElementsByTagName("url").item(0).getTextContent();
		fileData = fileData.replaceAll("##KEYWORD_DATA##", keyword);
		saveFile(templatePath
				+ eElement.getElementsByTagName("url").item(0).getTextContent()
				+ ".html", fileData);
		/*System.out
				.println("Created HTML for "
						+ eElement.getElementsByTagName("url").item(0)
								.getTextContent());*/
	}

	public static String recepiData(String recipes_data, Element eElement) {
		String type = eElement.getElementsByTagName("type").item(0)
				.getTextContent();
		String itemTypeClass = "";
		if ("nonVegItem".equals(type)) {
			itemTypeClass = "nonVegItem-1";
		} else {
			itemTypeClass = "vegItem-1";
		}
		recipes_data += "<tr class=\"" + itemTypeClass + "\"><td>";
		recipes_data += "<div style='clear:both;width:750px'><div class='leftitem' style=\"padding-right: 20px;float:left;width: 220px\">"
				+ "<a alt=\""+ eElement.getElementsByTagName("title").item(0).getTextContent() +"\" "
						+ "title=\"" + eElement.getElementsByTagName("title").item(0).getTextContent() + "\" href=\""
				+ eElement.getElementsByTagName("url").item(0).getTextContent()
				+ ".html\"><img title='" + eElement.getElementsByTagName("title").item(0).getTextContent() 
				+ "' alt='" + eElement.getElementsByTagName("title").item(0).getTextContent() + "' src=\""
				+ eElement.getElementsByTagName("thumb").item(0)
						.getTextContent() + buildNo
				+ "\"/></a></div><div style=\"float:left;width:480px\">"
				+ "<a alt=\"" + eElement.getElementsByTagName("title").item(0).getTextContent() + "\" "
						+ "title=\"" + eElement.getElementsByTagName("title").item(0).getTextContent() + "\" class='noStyle' href=\""
				+ eElement.getElementsByTagName("url").item(0).getTextContent()
				+ ".html\"><div class=\"title\"><div style=\"float:left;\" class=\""
				+ eElement.getElementsByTagName("type").item(0)
						.getTextContent()
				+ "\">&nbsp;</div><div style=\"float:left;width:90%\">"
				+ eElement.getElementsByTagName("title").item(0).getTextContent()
				+ "</div></div><div class=\"desc\">"
				+ eElement.getElementsByTagName("shortDesc").item(0)
						.getTextContent() + "</div></a></div></div></td>";
		recipes_data += "</tr><tr class=\"blankTR " + itemTypeClass + "\"></tr>";
		return recipes_data;
	}

	public static String readFile(String fileName) {
		String line = null;
		String fileData = "";
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(fileName);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				fileData += line;
			}

			// Always close files.
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
		}
		return fileData;
	}

	public static void saveFile(String outPath, String fileData) {
		try {
			File newTextFile = new File(outPath);
			FileWriter fw = new FileWriter(newTextFile);
			fw.write(fileData);
			fw.close();
		} catch (IOException iox) {
			iox.printStackTrace();
		}
	}

	public static void selfCopy(String dest, String processor) {
		File srcf = new File(processor);
		File destf = new File(dest);
		destf.delete();
		try {
			Files.copy(srcf.toPath(), destf.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
