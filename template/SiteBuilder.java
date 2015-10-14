import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.imgscalr.Scalr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

public class SiteBuilder {
	
	public static String buildNo = "?sessionId=151";
	public static String pinterestData = "<script async data-pin-color=\"red\" data-pin-hover=\"true\" defer src=\"//assets.pinterest.com/js/pinit.js\"></script>";
	public static String aboutPageData = "Hello Friends, <br/><br/>Arpita is a daughter and homemaker from two lovely Bengali families. At present she lives in Austin, Texas with her husband Amitava.<br/><br/>They both are originally from greater Kolkata and real food lovers.<br/><br/>Cooking, learning about new recipes, listening and singing old songs in lonely afternoons are her hobbies. Arpita is also a big fan and follower of authentic bengali cooking and very much all kinds of indian street foods. Everyday as a self taught cook she paints her food with spices, colors, love and care. Behind everything Amitava is her real inspiration. After marriage, getting compliments from husband about cooking is a great achievment.<br/><br/>So, she heartily invites you all to take a colorful journey through her little \"Spicy World\" ...";
	
	public static void main(String[] args) {
		String img = "bhatura";
		/*createImage("/Volumes/Pearson/spicyworld/template/originals/" + img + ".jpg", "/Volumes/Pearson/spicyworld/recipeimages/" + img + ".jpg", 1000);
		createImage("/Volumes/Pearson/spicyworld/template/originals/" + img + ".jpg", "/Volumes/Pearson/spicyworld/recipeimages/thumb/" + img + ".jpg", 330);
		for (int i=1;i<=13; i++) {
			String limg = img + "-" + i;
			createImage("/Volumes/Pearson/spicyworld/template/originals/" + limg + ".jpg", "/Volumes/Pearson/spicyworld/recipeimages/" + limg + ".jpg", 1000);
		}*/
		//System.exit(1);
		String basePath = "/Volumes/Pearson/spicyworld/";
		String templatePath = basePath;
		String processor = "/Users/vghosam/Documents/workspace/test/src/SiteBuilder.java";
		String tag_data_template = templatePath + "template/template.html";
		String recipes_data_front = "<table class=\"dataTable\">";
		String recipes_data = "";
		String siteMapData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\">";
		siteMapData = siteMapData + staticEntriesSiteMap();
		String rssXMLData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><rss version=\"2.0\"><channel>"
				+ "<title>Spicy World</title><link>http://www.spicyworld.in</link>"
				+ "<description>Welcome to Spicy World by Arpita Ghosh Das. Easy and Simple Recipes make your cooking faster and your food delicious.</description>";
		String recipes_data_img = "", fileData = "";
		int count = 1, perPageData = 10;
		List recipeDataList = new ArrayList();
		String tags = "";
		String carosalImg = "";
		String notice = "<div class='cookbook'>Our recipes are now available in form of CookBook. "
				+ "<a class='download' title='Spicy World : Cook Book'  alt='Spicy World : Cook Book' href='Spicy-World-Cook-Book.pdf' target='_blank' onclick=\"ga('send', 'event', 'PDF', 'download', this.href);\">Download CookBook</a>"
				+ "&nbsp;&nbsp;To get the full CookBook, please follow our <a target='_blank' href='https://www.facebook.com/spicyworldrecipes' onclick=\"ga('send', 'event', 'Facebook', 'Website Cook Book', this.href);\">Facebook Page</a>.</div>";
		String latest3DataForHomePage = "<div class=\"middleTop\"><div class=\"left\">"
				+ "<div class=\"data\"><p>Easy and Simple Recipes make your cooking faster and your food delicious. Check out our recipes.</p>"
				+ "<a href=\"http://spicyworld.in/recipes.html\">Recipes</a></div></div>"
				+ "<div class=\"middle\">&nbsp;</div><div class=\"right\">"
				+ "<div id=\"slider1_container\" style=\"position: relative; top: 0px; left: 0px; height: 300px;\">"
				+ "<div id='internalID' u=\"slides\" style=\"cursor: move; position: absolute; overflow: hidden; left: 0px; top: 0px;  height: 300px;\">##HOME_IMAGE_TOP##</div>"
				+ "</div></div></div>" + notice + "<div class=\"middleBottom\">";
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
					String classToApply = null;
					if (count == 1) {
						classToApply = "left";
					} else if (count == 2) {
						classToApply = "middle";
					} else if (count == 3) {
						classToApply = "right";
					} else {
						classToApply = null;
					}
					if (classToApply!=null) {
						String title = eElement.getElementsByTagName("title").item(0).getTextContent();
						String url = eElement.getElementsByTagName("url").item(0).getTextContent();
						latest3DataForHomePage += "<div class=\"" + classToApply + "\"><a title='" + title + "' alt='" + title + "' href=\"" + url + 
								".html\"><img title='" + title + "' alt='" + title + "' src=\"" + eElement.getElementsByTagName("pic").item(0).getTextContent() + "\"/>"
										+ "</a><div class=\"title\"><a title='" + title + "' alt='" + title + "' href=\"" + url + ".html\">" + title + "</a></div></div>";
					}
					if (count > 3 && count < 10) {
						String title = eElement.getElementsByTagName("title").item(0).getTextContent();
						carosalImg += "<div><img title='" + title + "' alt='" + title + "' class=\"show\" u=\"image\" src=\"" + eElement.getElementsByTagName("pic").item(0).getTextContent() + "\" /></div>";
					}
					count++;
					siteMapData += siteMapEntry(eElement);
					rssXMLData += populateRSSData(eElement);
					try {
						tags += eElement.getElementsByTagName("tags").item(0).getTextContent() + ",";
					} catch (Exception e) {}
				}
			}
			latest3DataForHomePage += "</div>";
			latest3DataForHomePage = latest3DataForHomePage.replace("##HOME_IMAGE_TOP##", carosalImg);
			if (!"".equals(recipes_data)) {
				recipeDataList.add(recipes_data_front + recipes_data + "</table>");
				recipes_data = "";
			}
			
			for (int i=0; i<recipeDataList.size(); i++) {
				fileData = readFile(basePath + "template/template.html");
				recipes_data = (String) recipeDataList.get(i);
				String pagination = getPagination(i+1, recipeDataList.size(), siteMapData);
				
				fileData = fileData.replace("##TITLE_DATA##", "Our Recipes - Page " + (i+1) + " | Spicy World by Arpita");
				fileData = fileData.replace("##MIDDLE_DATA##", "<div class='paginationHeader'><h1 class='headerFont'>Our Recipes - Page " + (i+1) + "</h1><div class='topPaginationData'>" + pagination + "</div></div><div class='recipePage'>" + recipes_data + "</div><br/><div class='topPaginationData'>" + pagination + "</div><div class='clear'>&nbsp;</div>");
				fileData = fileData.replace("##recipes_sel##", "selected");
				fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
				fileData = fileData.replaceAll("##KEYWORD_DATA##", "Recipes in Spicy World");
				fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita Ghosh Das. Here are our recipes (page number " + (i+1) + ") that you might like.");
				fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home1.jpg");
				fileData = fileData.replaceAll("##ONLOAD_CALL##", "enableAd();");
				fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
				
				
				if (i > 0) {
					fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<link rel='alternate' media='only screen and (max-width: 640px)' href='http://spicyworld.in/mobile/recipes-" + i + ".html' >");
					fileData = fileData.replaceAll("##URL_DATA##", "/recipes-" + i + ".html");
					saveFile(templatePath + "recipes-" + i + ".html", fileData);	
					siteMapData += "<url><loc>http://spicyworld.in/recipes-" + i + ".html</loc>"
							+ "<xhtml:link rel=\"alternate\" media=\"only screen and (max-width: 640px)\" href=\"http://spicyworld.in/mobile/recipes-" + i + ".html\" />"
							+ "</url>";
				} else {
					fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<link rel='alternate' media='only screen and (max-width: 640px)' href='http://spicyworld.in/mobile/recipes.html' >");
					fileData = fileData.replaceAll("##URL_DATA##", "/recipes.html");
					saveFile(templatePath + "recipes.html", fileData);	
					siteMapData += "<url><loc>http://spicyworld.in/recipes.html</loc>"
							+ "<xhtml:link rel=\"alternate\" media=\"only screen and (max-width: 640px)\" href=\"http://spicyworld.in/mobile/recipes.html\" />"
							+ "</url>";
				}
			}
			
			saveFile(templatePath + "rss.xml", rssXMLData.replace("&", "and") + "</channel></rss>");
			
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Our Food Images | Spicy World by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<h1 class='headerFont'>Our Food Images</h1><div role='main'><div id='' class='wordcloudImg'>" + recipes_data_img + "</div></div><div class=\"clear\">&nbsp;</div>");
			fileData = fileData.replace("##all-food-images_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "All images, images in Spicy World, Our food images");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##DESC_DATA##", "You can find all images of our Recipes in Spicy World. You can now also visit the recipe details from here by clicking the links below the images.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home1.jpg");
			fileData = fileData.replaceAll("##URL_DATA##", "/all-food-images.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "initFancyAll();enableAd();");
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
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
				String h1Tag = "<h1 class='headerFont'>Recipes on <i><b>'" + entry.getKey() + "'</b></i></h1>";
				generateTagHTML(data, tag_data_template, nList, templatePath, count, entry.getKey(), h1Tag);
				siteMapData += "<url><loc>http://spicyworld.in/" + data + "-tag.html</loc>"
						+ "<xhtml:link rel=\"alternate\" media=\"only screen and (max-width: 640px)\" href=\"http://spicyworld.in/mobile/" + data + "-tag.html\" />"
						+ "</url>";
			}
			
			// Save Tags
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Recipe Tags | Spicy World by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div role='main'><div id='wordcloud1' class='wordcloud'>" + htmlTags + "</div></div>");
			fileData = fileData.replace("##tags_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##KEYWORD_DATA##", keywordTags);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<script type=\"text/javascript\" src=\"js/jquery.awesomeCloud-0.2.min.js\"></script>"
					+ "<link rel='alternate' media='only screen and (max-width: 640px)' href='http://spicyworld.in/mobile/tags.html' >");
			fileData = fileData.replaceAll("##DESC_DATA##", "Tag cloud is an easy way to link multiple content and you can easily choose the content you are looking for from various tags.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home2.jpg");
			fileData = fileData.replaceAll("##URL_DATA##", "/tags.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "startTagPage();");
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(templatePath + "tags.html", fileData);
			
			saveFile(basePath + "sitemap.xml", siteMapData + "</urlset>");
			
			//Save HomePage
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Welcome to Spicy World by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", latest3DataForHomePage);
			fileData = fileData.replace("##index_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<script type=\"text/javascript\" src=\"js/jssor.slider.mini.js\"></script>"
					+ "<link rel='alternate' media='only screen and (max-width: 640px)' href='http://spicyworld.in/mobile/index.html' >");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Spicy World, Arpita's Kitchen, Food Recipes, All Spicy Foods.");
			fileData = fileData.replaceAll("##DESC_DATA##", "Easy and Simple Recipes make your cooking faster and your food delicious. Check out all available recipes.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home1.jpg");
			fileData = fileData.replaceAll("##URL_DATA##", "");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "loadSlideShow();");
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(basePath + "index.html", fileData);
			
			
			//Save Feedback Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Feedback or Comment | Spicy World by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div style=\"clear:both;width:95%;min-height: 500px;padding:20px;\"><div id=\"disqus_thread\"></div><script type=\"text/javascript\"> var disqus_shortname = 'spicyworld';  (function() {var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true; dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js';(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);  })();</script></div>");
			fileData = fileData.replace("##feedback_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Spicy World, Arpita's Kitchen, Feedback to Spicy World, Leave your comment.");
			fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita Ghosh Das. Please provide your feedback or your comments about our recipes.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home3.jpg");
			fileData = fileData.replaceAll("##URL_DATA##", "/feedback.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(basePath + "feedback.html", fileData);
			
			
			//Save 404 Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Page Not Found (404) | Spicy World by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div style='min-height: 550px; text-align:center;padding: 100px;'><div style='font-size:50px;'>We are Sorry !!</div>"
					+ "<div style='background-color: lightgrey;padding: 20px;font-size: 21px;'>This is a custom 404 (Page Not found) page.<br/><br/>Please click Home or any other tabs from the header menu to navigate within the website.</div>"
					+ "</div>");
			fileData = fileData.replace("##indexss_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Spicy World, Arpita's Kitchen, This is a custom 404 Page by Spicy World.");
			fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita Ghosh Das. This is a custom 404 page (Page Not found). Please click Home or any other tabs from header menu to navigate within the website.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home3.jpg");
			fileData = fileData.replaceAll("##URL_DATA##", "/404.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(basePath + "404.html", fileData);
			
			
			//Save Search Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Search Recipes | Spicy World by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div id='searchMdl'><div style='float:left'><img width='40px' src=\"images/loading.gif\"/></div><div class='searchTxt'>Searching for related recipes, please wait ...</div>"
					+ "</div>");
			fileData = fileData.replace("##recipes_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Spicy World, Arpita's Kitchen, Search recipes, recipe search, specific recipes, search foods");
			fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita Ghosh Das. Search recipes from our library of all recipes.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home3.jpg");
			fileData = fileData.replaceAll("##URL_DATA##", "/search.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "enableAd();searchForRecipe();");
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(basePath + "search.html", fileData);
			
			
			//About Me Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "About Me | Spicy World by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div><img src='images/about.jpg' width='100%'/>"
					+ "<div id='aboutData'>" + aboutPageData + "</div></div>");
			fileData = fileData.replace("##about_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "About me, About Spicy World");
			fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita Ghosh Das. Here are the cast and crew of Spicy World.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home3.jpg");
			fileData = fileData.replaceAll("##URL_DATA##", "/about-me.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", "");
			saveFile(basePath + "about-me.html", fileData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		selfCopy(templatePath + "template/SiteBuilder.java", processor);
		System.out.println("Processed ...");
		//getAllImages(basePath + "recipeimages");
		MobileSiteBuilder.buildNo = buildNo;
		MobileSiteBuilder.mobileSiteBuilder();
	}
	
	private static void generateTagHTML(String tag, String templatePath, NodeList nList, String baseTemplatePath, int count, String tagDataStr, String h1Tag) {
		String recipes_data = "<div class='recipePage'><table class=\"dataTable\">", tagData = null;
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
		recipes_data += "</table></div>";
		
		//Save Tag Page
		String fileData = readFile(templatePath);
		fileData = fileData.replace("##TITLE_DATA##", "Recipes on " + tagDataStr.toUpperCase() + " | Spicy World by Arpita");
		fileData = fileData.replace("##MIDDLE_DATA##", h1Tag + recipes_data);
		fileData = fileData.replace("##tags_sel##", "selected");
		fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
		fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<link rel='alternate' media='only screen and (max-width: 640px)' href='http://spicyworld.in/mobile/" + tag + "-tag.html' >");
		fileData = fileData.replaceAll("##KEYWORD_DATA##", "Contents, Tags for " + tagData.toUpperCase());
		fileData = fileData.replaceAll("##DESC_DATA##", "Recipes related to " + tagData.toUpperCase() + " tag in Spicy World.");
		fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home1.jpg");
		fileData = fileData.replaceAll("##URL_DATA##", "/" + tag + "-tag.html");
		fileData = fileData.replaceAll("##ONLOAD_CALL##", "enableAd();");
		fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
		saveFile(baseTemplatePath + tag + "-tag.html", fileData);
	}
	
	private static String getPagination(int currentPage, int totalPage, String siteMapData) {
		String data = "";
		for (int i=1; i<=totalPage; i++) {
			if (i == currentPage) {
				data += "<span class=\"selected\">" + i + "</span>";
			} else {
				String pageURL = "", pageTitle = "";
				if (i == 1) {
					pageURL = "recipes.html";
					pageTitle = "My Recipes - Page 1";
				} else {
					pageURL = "recipes-" + (i-1) + ".html";
					pageTitle = "My Recipes - Page " + i;
				}
				data += "<span><a title='" + pageTitle + "' href='" + pageURL + "'>" + i + "</a></span>";
			}
		}
		return data;
	}
	
	public static String getAllImages(String recipes_data, Element eElement) {
		recipes_data += "<div class='imagesPage'><div><a class=\"group1\" href=\"" + eElement.getElementsByTagName("pic").item(0).getTextContent() + buildNo + "\" title='" + eElement.getElementsByTagName("title").item(0).getTextContent() 
				+ "'><img style=\"width: 212px !important;\" src=\""
				+ eElement.getElementsByTagName("thumb").item(0).getTextContent() + buildNo
				+ "\"/></a></div><div style=\"clear:both;padding-left:20px;width:212px;height:70px\"><a href=\"http://www.spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html\">" + eElement.getElementsByTagName("title").item(0).getTextContent() + "</a></div></div>";
		return recipes_data;
	}
	
	public static String staticEntriesSiteMap() {
		return "<url><loc>http://spicyworld.in</loc>"
				+ "<xhtml:link rel=\"alternate\" media=\"only screen and (max-width: 640px)\" href=\"http://spicyworld.in/mobile/index.html\" /></url>"
				+ "<url><loc>http://spicyworld.in/feedback.html</loc></url>"
				+ "<url><loc>http://spicyworld.in/about-me.html</loc>"
				+ "<xhtml:link rel=\"alternate\" media=\"only screen and (max-width: 640px)\" href=\"http://spicyworld.in/mobile/about-me.html\" />"
				+ "</url>"
				+ "<url><loc>http://spicyworld.in/rss.xml</loc></url>"
				+ "<url><loc>http://spicyworld.in/Spicy-World-Cook-Book.pdf</loc></url>"
				+ "<url><loc>http://spicyworld.in/sitemap.xml</loc></url>"
				+ "<url><loc>http://spicyworld.in/all-food-images.html</loc></url>"
				+ "<url><loc>http://spicyworld.in/tags.html</loc>"
				+ "<xhtml:link rel=\"alternate\" media=\"only screen and (max-width: 640px)\" href=\"http://spicyworld.in/mobile/tags.html\" />"
				+ "</url>";
	}
	
	public static String populateRSSData(Element eElement) {
		String homeJSON = null;
		homeJSON = "<item><title>" + eElement.getElementsByTagName("title").item(0).getTextContent() + "</title><link>http://www.spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent()
		+ ".html</link><description>" + eElement.getElementsByTagName("shortDesc").item(0).getTextContent() + "</description></item>";
		return homeJSON;
	}
	
	public static String siteMapEntry(Element eElement) {
		String siteMapDataEntry = null;
		siteMapDataEntry = "<url><loc>http://spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html</loc>"
				+ "<xhtml:link rel=\"alternate\" media=\"only screen and (max-width: 640px)\" href=\"http://spicyworld.in/mobile/" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html\" />"
				+ "</url>";
		return siteMapDataEntry;
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
		String additionalImg = "";
		try  {
			additionalImg = eElement.getElementsByTagName("add-pic").item(0).getTextContent();
			additionalImg = "<div class='div3Pos posLeft'><br/><a class=\"group1\" title=\"" + title + "\" href=\"" + additionalImg + buildNo + "\"><img alt='" + title 
			+ "' title='" + title + "' src='"
			+ additionalImg + buildNo
			+ "' class='topImagePosition' /></a><br/><br/></div>";
		} catch (Exception e) {
			additionalImg = "";
		}
		String endImg = "";
		String endImgStyle = "";
		try  {
			endImg = eElement.getElementsByTagName("end-pic").item(0).getTextContent();
			endImg = "<div class='div3Pos posLeft'><br/><a class=\"group1\" title=\"" + title + " (Final)\" href=\"" + endImg + buildNo + "\"><img style=\"##_##\" alt='" + title 
			+ "' title='" + title + "' src='"
			+ endImg + buildNo
			+ "' class='topImagePosition' /></a><br/><br/></div>";
			try {
				endImgStyle = eElement.getElementsByTagName("end-pic-style").item(0).getTextContent();
				endImg = endImg.replace("##_##", endImgStyle);
			} catch (Exception e) {
				endImg = endImg.replace("##_##", "");
			}
		} catch (Exception e) {
			endImg = "";
		}
		String steps = eElement.getElementsByTagName("process").item(0).getTextContent();
		if (steps.contains("recipeimages")) {
			steps = "<div class='steps-image'>" + steps + "</div>";
		}
		out = "<div vocab='http://schema.org/' typeof='Recipe'><div class='h2Class'><div style=\"clear:both\"><h1 property='name' id='title'>"
				+ title
				+ "</h1></div><div style=\"clear:both\">"
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
				//+ "<div style=\"float:left;padding-left:10px;\"><a  title='Send/Share via Email' title='Send/Share via Email' "
				//+ "href=\"mailto:?subject=" + title + " Recipe at Spicy World&body=" + desc + "\n Visit Spicy World (http://spicyworld.in/" + url + ".html?emailFlag=Y) for detailed recipe.\">"
				//+ "<img style='height:21px;' src='images/email.png' title='Send/Share via Email' title='Send/Share via Email'/></a></div>" 
				// Email Ends
				// Google Plus Starts
				//+ "<div style=\"float:left;padding-left:10px;\"><a  title='Share in Google Plus' title='Share in Google Plus' "
				//+ "target='_blank' href='#' onClick=\"window.open('https://plus.google.com/share?url=http://spicyworld.in/" + url + ".html', '" + title + "','resizable,height=400,width=550');return false;\">"
				//+ "<img style='height:19px;' src='images/google_plus.jpg' title='Share in Google Plus' title='Share in Google Plus'/></a></div>" 
				// Google Plus Ends
				// Linked In Starts
				//+ "<div style=\"float:left;padding-left:10px;\">"
				//+ "<script src=\"//platform.linkedin.com/in.js\" type=\"text/javascript\"> lang: en_US</script><script type=\"IN/Share\" data-url=\"http://spicyworld.in/" + url + ".html\" data-counter=\"right\"></script>"
				//+ "</div>" 
				// Linked In Ends
				// FB Starts
				//+ "<div style=\"float:left;padding-left:10px;\"><div class=\"fb-send\" data-href=\"http://spicyworld.in/" + url + ".html\"></div></div>"
				+ "<div style=\"float:left;padding-left:10px;\"><div class=\"fb-like\" data-href=\"http://spicyworld.in/"
				+ url
				+ ".html\" data-layout=\"button_count\" data-action=\"like\" data-show-faces=\"true\" data-share=\"true\"></div></div>" 
				// FB Ends
				+ "</div>"
				+ "<div class='likePlace'><div class=\"rw-ui-container\"></div></div>"
				+ "<p class=\"descp\" id='description' property='description' style=\"padding-top:8px;clear:both\">" + desc + "</p></div><br/>"
				+ "<div>"
				+ "<div class='div3Pos posLeft'><a class=\"group1\" title=\"" + title + "\" href=\"" + eElement.getElementsByTagName("pic").item(0).getTextContent() + buildNo + "\"><img alt='" + title 
				+ "' title='" + title + "' property='image' src='"
				+ eElement.getElementsByTagName("pic").item(0).getTextContent() + buildNo
				+ "' class='topImagePosition'/></a><br/><br/><div><h2 id='ingredients'>Ingredients</h2></div><div property='ingredients'>"
				+ eElement.getElementsByTagName("ingrediants").item(0)
						.getTextContent()
				+ "</div></div>" + additionalImg
				+ "<div class='div3Pos'><div><h2 id='steps'>Steps</h2></div><div property='recipeInstructions'>"
				+ steps
				+ "</div><br/><div class='complete'>"
				+ eElement.getElementsByTagName("completionStatement").item(0)
						.getTextContent()
				+ "</div>"
				+ "<div class='garnishment'>"
				+ eElement.getElementsByTagName("garnishment").item(0)
						.getTextContent() + "</div></div>" + "</div></div>" + endImg;
		
		try {
			String tagData = eElement.getElementsByTagName("tags").item(0).getTextContent();
			String tags = "<div id='tags' class=\"tagContent\"><span class=\"heading\">Tags:</span>";
			if (tagData!=null) {
				String ss[] = tagData.split(",");
				for (int i=0; i<ss.length; i++) {
					String tg = ss[i];
					tags += "<span><a href=\"" + tg.replace(" ", "-") + "-tag.html\">" + tg + "</a></span>";
				}
			}
			tags += "</div>";
			out += tags;
		} catch (Exception e) {} 
		
		out += "<div style=\"clear:both;padding-top:20px;padding-bottom:20px;\"><div id='comments' class='commentHeader'>Leave Your Comments</div>"
		+ "<div class='disqus_thread_class'><div id=\"disqus_thread\"></div><script type=\"text/javascript\"> var disqus_shortname = 'spicyworld';  (function() {var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true; dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js';(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);  })();</script></div></div>";
		
		
		String fileData = readFile(templatePath + "template/template.html");
		
		String ky = null;
		try {
			ky = eElement.getElementsByTagName("keywords").item(0).getTextContent();
		} catch (Exception e) {}
		String keyword = "";
		if (ky!=null && !"".equals(ky)) {
			keyword = ky + " ";
		} else {
			keyword = title + ", Arpita, kitchen, Spicy World, World of Spices, Spice, Food, Recipes, " + url;
		}
		
		fileData = fileData.replace("##TITLE_DATA##", "How to cook " + title + " | Spicy World by Arpita");
		fileData = fileData.replace("##MIDDLE_DATA##", "<div class='recipeDataPage'>" + out + "</div><div class=\"clear\">&nbsp;</div>");
		fileData = fileData.replace("##recipes_sel##", "selected");
		fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
		fileData = fileData.replaceAll("##KEYWORD_DATA##", keyword);
		fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<link rel='alternate' media='only screen and (max-width: 640px)' href='http://spicyworld.in/mobile/" + url + ".html' >");
		fileData = fileData.replaceAll("##DESC_DATA##", desc);
		fileData = fileData.replaceAll("##IMG_DATA##", eElement.getElementsByTagName("pic").item(0).getTextContent());
		fileData = fileData.replaceAll("##URL_DATA##", "/" + url + ".html");
		fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
		fileData = fileData.replaceAll("##ONLOAD_CALL##", "initFancy();enableAd();");
		
		saveFile(templatePath + url + ".html", fileData);
		System.out.println(count + ". Created Web HTML for " + url);
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
		String data = eElement.getElementsByTagName("shortDesc").item(0).getTextContent();
		data = CookBook.html2text(data);
		if (data.length() > 400) {
			data = data.substring(0, 400) + " ...";
		}
		recipes_data += "<tr  vocab='http://schema.org/' typeof='Recipe' class=\"" + itemTypeClass + "\"><td>";
		recipes_data += "<div style='clear:both;width:100%'><div class='leftitem' style=\"padding-right: 20px;float:left;width: 35%\">"
				+ "<a href='" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html'>"
						+ "<img property='image' title='" + eElement.getElementsByTagName("title").item(0).getTextContent() 
				+ "' alt='" + eElement.getElementsByTagName("title").item(0).getTextContent() + "' src=\""
				+ prefix + eElement.getElementsByTagName("thumb").item(0)
						.getTextContent() + buildNo
				+ "\"/></a></div><div style=\"float:left;width:60%\">"
				+ "<div class=\"title\"><div style=\"float:left;\" class=\""
				+ eElement.getElementsByTagName("type").item(0)
						.getTextContent()
				+ "\">&nbsp;</div><div style=\"float:left;width:90%\">"
				+ "<a alt=\"" + eElement.getElementsByTagName("title").item(0).getTextContent() + "\" "
						+ "title=\"" + eElement.getElementsByTagName("title").item(0).getTextContent() + "\" property='name' class='noStyle' href=\""
				+ eElement.getElementsByTagName("url").item(0).getTextContent()
				+ ".html\">" + eElement.getElementsByTagName("title").item(0).getTextContent()
				+ "</a></div></div><div class=\"desc\" property='description'>"
				+ data + "</div></div></div></td>";
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
	
	private static List getAllImages(String destinationPath, String destFolder,
			int destinationWidth) {
		List listFiles = new ArrayList();
		String src = "";
		String dest = "";
		try {
			File[] files = new File(destinationPath).listFiles();
			for (File file : files) {
				if (!file.isDirectory()) {
					listFiles.add(file.getCanonicalPath());
					src = file.getCanonicalPath();
					dest = destFolder + file.getName();
					createImage(src, dest, destinationWidth);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listFiles;
	}

	private static void createImage(String source, String destination, int destinationWidth) {
		try {
			BufferedImage bimg = ImageIO.read(new File(source));
			int width = bimg.getWidth();
			if (width < destinationWidth && width > 0) {
				destinationWidth = width;
			} else if (width <= 0) {
				System.out.println("cp " + source + " " + destination);
			}
			File image = new File(source);
			File smallImage = new File(destination); // FORNOW: added the file
														// extension just to
														// check the result a
														// bit more easily
			// FORNOW: added print statements just to be doubly sure where we're
			// reading from and writing to
			try {
				BufferedImage bufimage = ImageIO.read(image);

				BufferedImage bISmallImage = Scalr.resize(bufimage,
						destinationWidth); // after this line my dimensions in
											// bISmallImage are correct!
				ImageIO.write(bISmallImage, "jpg", smallImage); // but my
																// smallImage
																// has the same
																// dimension as
																// the original
																// foto
			} catch (Exception e) {
				System.out.println(e.getMessage()); // FORNOW: added just to be
													// sure
			}
		} catch (Exception e) {
		}
	}

}
