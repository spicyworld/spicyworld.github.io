import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

public class FileGenerator {
	
	public static String buildNo = "?sessionId=109";

	public static void main(String[] args) {
		String basePath = "/Volumes/Pearson/spicyworld/";
		String templatePath = basePath;
		String processor = "/Users/vghosam/Documents/workspace/test/src/FileGenerator.java";
		String recipes_template = templatePath + "template/recipes.html";
		String recipes_template_img = templatePath + "template/image-list.html";
		String tags_template = templatePath + "template/tags.html";
		String tag_data_template = templatePath + "template/tag-content.html";
		String recipes_data_front = "<table class=\"dataTable\">";
		String recipes_data = "";
		String homeJSON = "var myData = [";
		String siteMapData = "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";
		siteMapData = siteMapData + staticEntriesSiteMap();
		String rssXMLData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><rss version=\"2.0\"><channel>"
				+ "<title>Spicy World</title><link>http://www.spicyworld.in</link>"
				+ "<description>Welcome to Spicy World by Arpita Ghosh Das. Easy and Simple Recipes make your cooking faster and your food delicious.</description>";
		String recipes_data_img = "", fileData = "";
		int count = 1, perPageData = 30;
		List recipeDataList = new ArrayList();
		String tags = "";
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
					if (count % perPageData == 0) {
						recipeDataList.add(recipes_data_front + recipes_data + "</table>");
						recipes_data = "";
					}
					recipes_data = recepiData(recipes_data, eElement, "");
					recipes_data_img = getAllImages(recipes_data_img, eElement);
					createItemData(templatePath, eElement, count);
					count++;
					if ("var myData = [".equals(homeJSON)) {
						homeJSON += createHomeJS(eElement);
					} else {
						homeJSON = homeJSON + "," + createHomeJS(eElement);
					}
					siteMapData += siteMapEntry(eElement);
					rssXMLData += populateRSSData(eElement);
					try {
						tags += eElement.getElementsByTagName("tags").item(0).getTextContent() + ",";
					} catch (Exception e) {}
				}
			}
			if (!"".equals(recipes_data)) {
				recipeDataList.add(recipes_data_front + recipes_data + "</table>");
				recipes_data = "";
			}
			homeJSON += "];";
			
			for (int i=0; i<recipeDataList.size(); i++) {
				fileData = readFile(recipes_template);
				recipes_data = (String) recipeDataList.get(i);
				String pagination = getPagination(i+1, recipeDataList.size(), siteMapData);
				fileData = fileData.replace("#PAGINATION_RECIPE#", pagination);
				fileData = fileData.replace("##DATA_ENTRY##", recipes_data);
				fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
				fileData = fileData.replaceAll("##PAGE_TITLE##", "My Recipes - Page " + (i+1));
				if (i > 0) {
					saveFile(templatePath + "recipes-" + i + ".html", fileData);	
					siteMapData += "<url><loc>http://spicyworld.in/recipes-" + i + ".html</loc></url>";
				} else {
					saveFile(templatePath + "recipes.html", fileData);	
					siteMapData += "<url><loc>http://spicyworld.in/recipes.html</loc></url>";
				}
			}
			
			saveFile(templatePath + "recipes.js", homeJSON);
			saveFile(templatePath + "rss.xml", rssXMLData.replace("&", "and") + "</channel></rss>");
			
			fileData = readFile(recipes_template_img);
			fileData = fileData.replace("##DATA_ENTRY##", recipes_data_img);
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			saveFile(templatePath + "all-food-images.html", fileData);
			
			// Create tag cloud
			String ss[] = tags.split(",");
			Map tagMap = new HashMap();
			for (int i=0; i<ss.length; i++) {
				String key = ss[i];
				key = key.trim();
				if (!"".equals(key) && !",".equals(key)) {
					int value = 1;
					try {
						value = (int) tagMap.get(key);
						value = value + 1;
					} catch (Exception e) {}
					tagMap.put(key, value);
				}
			}
			Iterator iterator = tagMap.entrySet().iterator();
			String htmlTags = "", keywordTags = "";
			count = 0;
			while (iterator.hasNext()) {
				Map.Entry<String,Integer> entry = (Map.Entry<String,Integer>) iterator.next();
				String data = entry.getKey();
				keywordTags += data + " ";
				data = data.replace(" ", "-");
				htmlTags += "<span data-weight=\"" + entry.getValue() + "\"><a href=\"" + data + "-tag.html\">" + entry.getKey() + "</a></span>";
				count++;
				generateTagHTML(data, tag_data_template, nList, templatePath, count, entry.getKey());
				siteMapData += "<url><loc>http://spicyworld.in/" + data + "-tag.html</loc></url>";
			}
			fileData = readFile(tags_template);
			fileData = fileData.replace("##TAG_HTML_DATA##", htmlTags);
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##KEYWORD_DATA##", keywordTags);
			saveFile(templatePath + "tags.html", fileData);
			
			saveFile(basePath + "sitemap.xml", siteMapData + "</urlset>");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		selfCopy(templatePath + "template/FileGenerator.java", processor);
		System.out.println("Processed ...");
	}
	
	private static void generateTagHTML(String tag, String templatePath, NodeList nList, String baseTemplatePath, int count, String tagDataStr) {
		String recipes_data = "<table class=\"dataTable\">", tagData = null;
		try {
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					try {
						tagData = eElement.getElementsByTagName("tags").item(0).getTextContent();
					} catch (Exception e) {
						tagData = "";
					}
					if (tagData!=null && tagData.indexOf(tagDataStr) > -1) {
						recipes_data = recepiData(recipes_data, eElement, "");
					}
				}
			}
		} catch (Exception e) {}
		recipes_data += "</table>";
		String fileData = readFile(templatePath);
		fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
		fileData = fileData.replace("##DATA_ENTRY##", recipes_data);
		fileData = fileData.replaceAll("##PAGE_TITLE##", tagDataStr.toUpperCase());
		saveFile(baseTemplatePath + tag + "-tag.html", fileData);
		System.out.println(count + ". Creating Tag Page for " + tagDataStr);
	}
	
	private static String getPagination(int currentPage, int totalPage, String siteMapData) {
		String data = "";
		for (int i=1; i<=totalPage; i++) {
			if (i == currentPage) {
				data += "<div class=\"selected\">" + i + "</div>";
			} else {
				String pageURL = "", pageTitle = "";
				if (i == 1) {
					pageURL = "recipes.html";
					pageTitle = "My Recipes - Page 1";
				} else {
					pageURL = "recipes-" + (i-1) + ".html";
					pageTitle = "My Recipes - Page " + i;
				}
				data += "<div><a title='" + pageTitle + "' href='" + pageURL + "'>" + i + "</a></div>";
			}
		}
		return data;
	}
	
	public static String getAllImages(String recipes_data, Element eElement) {
		recipes_data += "<div><div><a class=\"group1\" href=\"" + eElement.getElementsByTagName("thumb").item(0).getTextContent() + buildNo + "\" title='" + eElement.getElementsByTagName("title").item(0).getTextContent() 
				+ "'><img style=\"width: 200px !important;\" src=\""
				+ eElement.getElementsByTagName("thumb").item(0).getTextContent() + buildNo
				+ "\"/></a></div><div style=\"clear:both;padding-left:20px;width:200px;height:70px\"><a style=\"color:white;\" href=\"http://www.spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html\">" + eElement.getElementsByTagName("title").item(0).getTextContent() + "</a></div></div>";
		return recipes_data;
	}
	
	public static String staticEntriesSiteMap() {
		return "<url><loc>http://spicyworld.in</loc></url>"
				+ "<url><loc>http://spicyworld.in/feedback.html</loc></url>"
				+ "<url><loc>http://spicyworld.in/rss.xml</loc></url>"
				+ "<url><loc>http://spicyworld.in/sitemap.xml</loc></url>"
				+ "<url><loc>http://spicyworld.in/all-food-images.html</loc></url>"
				+ "<url><loc>http://spicyworld.in/tags.html</loc></url>";
	}
	
	public static String populateRSSData(Element eElement) {
		String homeJSON = null;
		homeJSON = "<item><title>" + eElement.getElementsByTagName("title").item(0).getTextContent() + "</title><link>http://www.spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent()
		+ ".html</link><description>" + eElement.getElementsByTagName("shortDesc").item(0).getTextContent() + "</description></item>";
		return homeJSON;
	}
	
	public static String siteMapEntry(Element eElement) {
		String siteMapDataEntry = null;
		siteMapDataEntry = "<url><loc>http://spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html</loc></url>";
		return siteMapDataEntry;
	}

	public static String createHomeJS(Element eElement) {
		String homeJSON = null;
		homeJSON = "{\"title\":\"" + eElement.getElementsByTagName("title").item(0).getTextContent() + "\",\"thumb\":\"" + eElement.getElementsByTagName("pic").item(0).getTextContent() + "\",\"url\":\"" + eElement.getElementsByTagName("url").item(0).getTextContent()
		+ ".html\"}";
		return homeJSON;
	}
	
	public static void createItemData(String templatePath, Element eElement, int count) {
		String out = "";
		String type = eElement.getElementsByTagName("type").item(0)
				.getTextContent();
		String itemType = "";
		if ("nonVegItem".equals(type)) {
			itemType = "Non-Vegetarian";
		} else {
			itemType = "Vegetarian";
		}
		String title = eElement.getElementsByTagName("title").item(0).getTextContent();
		String desc = eElement.getElementsByTagName("shortDesc").item(0).getTextContent();
		String url = eElement.getElementsByTagName("url").item(0).getTextContent();
		out = "<div><div class='h2Class'><div style=\"clear:both\"><h1 style=\"font-size: 25px;display: inline;float:left\">"
				+ title
				+ "</h1><div style=\"float:left\">&nbsp;("
				+ itemType
				+ ")</div></div><div style=\"clear:both\">"
				// Pinterest Starts
				+ "<div style=\"float:left;\"><a href=\"//www.pinterest.com/pin/create/button/\" data-pin-do=\"buttonBookmark\"  data-pin-color=\"red\"><img src=\"//assets.pinterest.com/images/pidgets/pinit_fg_en_rect_red_20.png\" /></a><script type=\"text/javascript\" async defer src=\"//assets.pinterest.com/js/pinit.js\"></script>"	 
				+ "</div>"
				// Pinterest Ends
				// Twitter Starts
				+ "<div style=\"float:left;height:10px;padding-left:10px;\">"
				+ "<a href=\"https://twitter.com/share\" class=\"twitter-share-button\" data-url=\"http://spicyworld.in/" + url + ".html\" data-text=\"" + title + "\" data-via=\"amitava3g\"></a>"
				+ "<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document, 'script', 'twitter-wjs');</script>"
				+ "</div>"
				// Twitter Ends
				// Email Starts
				+ "<div style=\"float:left;padding-left:10px;\"><a  title='Send/Share via Email' title='Send/Share via Email' "
				+ "href=\"mailto:?subject=" + title + " Recipe at Spicy World&body=" + desc + "\n Visit Spicy World (http://spicyworld.in/" + url + ".html?emailFlag=Y) for detailed recipe.\">"
				+ "<img style='height:21px;' src='img/email.png' title='Send/Share via Email' title='Send/Share via Email'/></a></div>" 
				// Email Ends
				// Google Plus Starts
				+ "<div style=\"float:left;padding-left:10px;\"><a  title='Share in Google Plus' title='Share in Google Plus' "
				+ "target='_blank' href='#' onClick=\"window.open('https://plus.google.com/share?url=http://spicyworld.in/" + url + ".html', '" + title + "','resizable,height=400,width=550');return false;\">"
				+ "<img style='height:21px;' src='img/google_plus.jpg' title='Share in Google Plus' title='Share in Google Plus'/></a></div>" 
				// Google Plus Ends
				// Linked In Starts
				+ "<div style=\"float:left;padding-left:10px;\">"
				+ "<script src=\"//platform.linkedin.com/in.js\" type=\"text/javascript\"> lang: en_US</script><script type=\"IN/Share\" data-url=\"http://spicyworld.in/" + url + ".html\" data-counter=\"right\"></script>"
				+ "</div>" 
				// Linked In Ends
				// FB Starts 
				+ "<div style=\"float:left;padding-left:10px;\"><div class=\"fb-like\" data-href=\"http://spicyworld.in/"
				+ url
				+ ".html\" data-layout=\"button_count\" data-action=\"like\" data-show-faces=\"true\" data-share=\"true\"></div></div>" 
				// FB Ends
				+ "</div><p class=\"descp\" style=\"padding-top:8px;clear:both\">" + desc + "</p></div><br/>"
				+ "<div>"
				+ "<div class='div3Pos posLeft'><a class=\"group1\" title=\"" + title + "\" href=\"" + eElement.getElementsByTagName("pic").item(0).getTextContent() + buildNo + "\"><img alt='" + title 
				+ "' title='" + title + "' src='"
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
		
		try {
			String tagData = eElement.getElementsByTagName("tags").item(0).getTextContent();
			String tags = "<div class=\"tagContent\"><div class=\"heading\">Tags:</div>";
			if (tagData!=null) {
				String ss[] = tagData.split(",");
				for (int i=0; i<ss.length; i++) {
					String tg = ss[i];
					tags += "<div><a href=\"" + tg.replace(" ", "-") + "-tag.html\">" + tg + "</a></div>";
				}
			}
			tags += "</div>";
			out += tags;
		} catch (Exception e) {} 
		
		String fileData = readFile(templatePath + "template/food-item.html");
		fileData = fileData.replace("##DATA_ENTRY##", out);
		fileData = fileData.replaceAll("##TITLE_DATA##", title + " Recipe | Spicy World | Arpita's Kitchen");
		fileData = fileData.replace("##URL_DATA##", url + ".html");
		fileData = fileData.replace("##DESC_DATA##", desc);
		fileData = fileData.replace("##IMG_DATA##", eElement
				.getElementsByTagName("pic").item(0).getTextContent() + buildNo);
		fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
		/*String keyword = Utility.getKeyword(title 
				+ " " + eElement.getElementsByTagName("shortDesc").item(0).getTextContent());*/
		String ky = null;
		try {
			ky = eElement.getElementsByTagName("keywords").item(0).getTextContent();
		} catch (Exception e) {}
		String keyword = "";
		if (ky!=null && !"".equals(ky)) {
			keyword = ky + ", " + title;
		} else {
			keyword = title + ", Arpita, kitchen, Spicy World, World of Spices, Spice, Food, Recipes, " + url;
		}
		fileData = fileData.replaceAll("##KEYWORD_DATA##", keyword);
		saveFile(templatePath + url + ".html", fileData);
		System.out.println(count + ". Created HTML for " + url);
	}

	public static String recepiData(String recipes_data, Element eElement, String prefix) {
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
				+ prefix + eElement.getElementsByTagName("thumb").item(0)
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

	public static void saveHTMLFile(String outPath, String fileData) {
	try {
		Tidy tidy = new Tidy();
		tidy.setXHTML(true);
		InputStream stream = new ByteArrayInputStream(fileData.getBytes(StandardCharsets.UTF_8));
		File file = new File(outPath);
		FileOutputStream fop = new FileOutputStream(file);
		tidy.parse(stream, fop);
		fop.flush();
		fop.close();
		stream.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
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
