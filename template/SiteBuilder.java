import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class SiteBuilder {
	
	public static String buildNo = "?sessionId=233";
	public static String imgBuildNo = "?sessionId=214";
	public static String pinterestData = "<script async data-pin-color=\"red\" data-pin-hover=\"true\" defer src=\"//assets.pinterest.com/js/pinit.js\"></script>";
	public static String aboutPageData = "Hello Friends, <br/><br/><h1 class='specialH1'>Arpita</h1>&nbsp;is a daughter and homemaker from two lovely Bengali families. At present she lives in Austin, Texas with her husband Amitava.<br/><br/>They both are originally from greater Kolkata and real food lovers.<br/><br/>Cooking, learning about new recipes, listening and singing old songs in lonely afternoons are her hobbies. Arpita is also a big fan and follower of authentic bengali cooking and very much all kinds of indian street foods. Everyday as a self taught cook she paints her food with spices, colors, love and care. Behind everything Amitava is her real inspiration. After marriage, getting compliments from husband about cooking is a great achievement.<br/><br/>So, she heartily invites you all to take a colorful journey through her little \"<a href='http://spicyworld.in'>Spicy World</a>\" ...<br/><br/>Contact us: <u><a href='mailto:contact@spicyworld.in' target='_top' onclick=\"ga('send', 'event', 'Email Click', 'Email Click: Btn Click', this.href);\">contact@spicyworld.in</a></u>";
	
	public static void main(String[] args) {
		String basePath = "/Volumes/Pearson/spicyworld/";
		String processorBasePath = "/Users/vghosam/Documents/workspace/test";
		
		//Compress files
		compressFiles(basePath, processorBasePath);
		
		/*String img = "samosa";
		createImage(basePath + "/template/recipeimages/" + img + ".jpg", basePath + "/recipeimages/" + img + ".jpg", 1500, true, basePath);
		for (int i=1;i<=23; i++) {
			String limg = img + "-" + i;
			createImage(basePath + "/template/recipeimages/" + limg + ".jpg", basePath + "/recipeimages/" + limg + ".jpg", 1500, true, basePath);
		}*/
		
		//transformAllImages(basePath);
		//System.exit(1);
		
		
		String templatePath = basePath;
		String processor = processorBasePath + "/src/SiteBuilder.java";
		String tag_data_template = templatePath + "template/template.html";
		String recipes_data_front = "<div>";
		String recipes_data = "";
		String siteMapData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\">";
		siteMapData = siteMapData + staticEntriesSiteMap();
		String rssXMLData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\"><channel>"
				+ "<title>Spicy World</title><link>http://www.spicyworld.in</link>"
				+ "<description>Welcome to Spicy World by Arpita Ghosh Das. Easy and Simple Recipes make your cooking faster and your food delicious.</description>"
				+ "<atom:link href=\"http://spicyworld.in/rss.xml\" rel=\"self\" type=\"application/rss+xml\" />";
		String recipes_data_img = "", fileData = "";
		int count = 1, perPageData = 18;
		List recipeDataList = new ArrayList();
		String tags = "";
		String carosalImg = "";
		//String notice = "";
		String topTags = "";//"<div class='topTags'></div>";
		String latest3DataForHomePage = "<div class=\"middleTop\"><div class=\"left\">"
				+ "<div class=\"data\"><p><h1>Spicy World's Easy and Simple Recipes make your cooking faster and your food delicious. Check out our recipes.</h1></p>"
				+ "<a onclick=\"ga('send', 'event', 'Home Page', 'Home Page: recipes_button', this.href);\" href=\"http://spicyworld.in/recipes.html\">Recipes</a></div></div>"
				+ "<div class=\"middle\">&nbsp;</div><div class=\"right\">"
				+ "<div id=\"slider1_container\" style=\"position: relative; top: 0px; left: 0px; height: 300px;\">"
				+ "<div id='internalID' u=\"slides\" style=\"cursor: move; position: absolute; overflow: hidden; left: 0px; top: 0px;  height: 300px;\">##HOME_IMAGE_TOP##</div>"
				+ "</div></div></div>" + topTags + "<div class=\"middleBottom\">";
		String nextAvailable = "", prevAvailable = "";
		List elementList = new ArrayList();
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
					try {
						tags += eElement.getElementsByTagName("tags").item(0).getTextContent() + ",";
					} catch (Exception e) {}
					elementList.add(eElement);
				}
			}
			
			// Create tag cloud Starts
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
				htmlTags += "<span data-weight=\"" + entry.getValue() + "\"><a title='" + entry.getKey()  + "' href=\"" + data + "-tag.html\">" + entry.getKey() + "</a></span>";
				count++;
				String h1Tag = "<h1 class='headerFont'>Recipes on <i><b>'" + entry.getKey() + "'</b></i></h1>";
				generateTagHTML(data, tag_data_template, nList, templatePath, count, entry.getKey(), h1Tag);
				siteMapData += "<url><loc>http://spicyworld.in/" + data + "-tag.html</loc>"
						+ "<xhtml:link rel=\"alternate\" media=\"only screen and (max-width: 640px)\" href=\"http://spicyworld.in/mobile/" + data + "-tag.html\" />"
						+ "</url>";
			}
			// Create tag cloud Ends
			count = 1;
			String blogTable = "";
			for (int i=0;i<elementList.size();i++) {
				Element nextElement = null, prevElement = null;
				Element eElement = (Element) elementList.get(i);
				
				//if (i<5) {
				blogTable += "<br/><br/><div><a href='http://spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html?from=blog'><img style='box-shadow: 9px 8px 5px #888888;padding: 2px;background: grey;' width='550px' src='http://spicyworld.in/" + eElement.getElementsByTagName("pic").item(0)
						.getTextContent() + "'/></a></div>"
						+ "<div style='padding-top:12px'><a style='font-size: 26px;color: #22B5E3;' href='http://spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html?from=blog'>" + eElement.getElementsByTagName("title").item(0).getTextContent()+ "</a></div>"
								+ "<div>" + CookBook.html2text(eElement.getElementsByTagName("shortDesc").item(0).getTextContent()).substring(100) + " ..." + "</div>";
				//}
				if (count % perPageData == 0) {
					recipeDataList.add(recipes_data_front + recipes_data + "</div>");
					recipes_data = "";
				}
				String relatedRecipes = relatedRecipes(elementList, i);
				recipes_data = recepiData(recipes_data, eElement, "");
				recipes_data_img = getAllImages(recipes_data_img, eElement);
				if (i > 0) {
					prevElement = (Element) elementList.get(i-1);
				}
				if (i < (elementList.size()-1)) {
					nextElement = (Element) elementList.get(i+1);
				}
				createItemData(templatePath, eElement, count, nextElement, prevElement, relatedRecipes, htmlTags);
				String classToApply = null;
				if (count == 1 || count == 4 || count == 7) {
					classToApply = "left";
				} else if (count == 2 || count == 5 || count == 8) {
					classToApply = "middle";
				} else if (count == 3 || count == 6 || count == 9) {
					classToApply = "right";
				} else {
					classToApply = null;
				}
				if (classToApply!=null) {
					String title = eElement.getElementsByTagName("title").item(0).getTextContent();
					String url = eElement.getElementsByTagName("url").item(0).getTextContent();
					latest3DataForHomePage += "<div class=\"" + classToApply + "\"><a onclick=\"ga('send', 'event', 'Home Page', 'Home Page: recipe_image', this.href);\" title='" + title + "' alt='" + title + "' href=\"" + url + 
							".html\"><img title='" + title + "' alt='" + title + "' src=\"" + eElement.getElementsByTagName("pic").item(0).getTextContent() + "\"/>"
									+ "</a><div class=\"title\"><a onclick=\"ga('send', 'event', 'Home Page', 'Home Page: recipe_title', this.href);\" title='" + title + "' alt='" + title + "' href=\"" + url + ".html\">" + title + "</a></div></div>";
				}
				if (count > 9 && count < 14) {
					String title = eElement.getElementsByTagName("title").item(0).getTextContent();
					carosalImg += "<div><img title='" + title + "' alt='" + title + "' class=\"show\" u=\"image\" src=\"" + eElement.getElementsByTagName("pic").item(0).getTextContent() + "\" /></div>";
				}
				count++;
				siteMapData += siteMapEntry(eElement);
				rssXMLData += populateRSSData(eElement);
				
			}
			System.out.println(blogTable);
			
			latest3DataForHomePage += "</div>";
			latest3DataForHomePage = latest3DataForHomePage.replace("##HOME_IMAGE_TOP##", carosalImg);
			if (!"".equals(recipes_data)) {
				recipeDataList.add(recipes_data_front + recipes_data + "</div>");
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
					fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
					fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "<link rel='alternate' media='only screen and (max-width: 640px)' href='http://spicyworld.in/mobile/recipes-" + i + ".html' />");
					fileData = fileData.replaceAll("##URL_DATA##", "/recipes-" + i + ".html");
					saveFile(templatePath + "recipes-" + i + ".html", fileData);	
					siteMapData += "<url><loc>http://spicyworld.in/recipes-" + i + ".html</loc>"
							+ "<xhtml:link rel=\"alternate\" media=\"only screen and (max-width: 640px)\" href=\"http://spicyworld.in/mobile/recipes-" + i + ".html\" />"
							+ "</url>";
				} else {
					fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
					fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "<link rel='alternate' media='only screen and (max-width: 640px)' href='http://spicyworld.in/mobile/recipes.html' />");
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
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##DESC_DATA##", "You can find all images of our Recipes in Spicy World. You can now also visit the recipe details from here by clicking the links below the images.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home1.jpg");
			fileData = fileData.replaceAll("##URL_DATA##", "/all-food-images.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "initFancyAll();enableAd();");
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(templatePath + "all-food-images.html", fileData);
			
			// Save Tags
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Recipe Tags | Spicy World by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div role='main'><div id='wordcloud1' class='wordcloud'>" + htmlTags.replace("href=", "onclick=\"ga('send', 'event', 'Tag Click', 'Tag Click: tag_page', this.href);\" href=") + "</div></div>");
			fileData = fileData.replace("##tags_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##KEYWORD_DATA##", keywordTags);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<script type=\"text/javascript\" src=\"js/jquery.awesomeCloud-0.2.min.js\"></script>");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "<link rel='alternate' media='only screen and (max-width: 640px)' href='http://spicyworld.in/mobile/tags.html' />");
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
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<script type=\"text/javascript\" src=\"js/jssor.slider.mini.js\"></script>");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "<link rel='alternate' media='only screen and (max-width: 640px)' href='http://spicyworld.in/mobile/index.html' />");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Spicy World, Arpita's Kitchen, Food Recipes, All Spicy Foods.");
			fileData = fileData.replaceAll("##DESC_DATA##", "Easy and Simple Recipes make your cooking faster and your food delicious. Check out all available recipes.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home1.jpg");
			fileData = fileData.replaceAll("##URL_DATA##", "");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "loadSlideShow();homeImg();");
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(basePath + "index.html", fileData);
			
			
			//Save Feedback Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Feedback or Comment | Spicy World by Arpita");
			//fileData = fileData.replace("##MIDDLE_DATA##", "<div style=\"clear:both;width:95%;min-height: 500px;padding:20px;\"><div id=\"disqus_thread\"></div><script type=\"text/javascript\"> var disqus_shortname = 'spicyworld';  (function() {var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true; dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js';(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);  })();</script></div>");
			//fileData = fileData.replace("##MIDDLE_DATA##", "<div style=\"clear:both;width:95%;min-height: 500px;padding:20px;\"><div class=\"fb-comments\" data-href=\"http://spicyworld.in\" data-width=\"100%\" data-numposts=\"20\"></div></div>");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div style=\"clear:both;width:95%;min-height: 500px;padding:20px;\">"
						+ "<div style='float:left;width:450px'>"
							+ "<div class=\"fb-page\" data-href=\"https://www.facebook.com/spicyworldrecipes\" data-tabs=\"timeline\" data-width=\"100%\" data-height=\"100%\" data-small-header=\"true\" data-adapt-container-width=\"false\" data-hide-cover=\"false\" data-show-facepile=\"true\"><div class=\"fb-xfbml-parse-ignore\"><blockquote cite=\"https://www.facebook.com/spicyworldrecipes\"><a href=\"https://www.facebook.com/spicyworldrecipes\">Spicy World</a></blockquote></div></div>"
						+ "</div>"
						+ "<div style='float:right;width:450px;'>"
							+ "<div class=\"fb-comments\" data-href=\"http://spicyworld.in\" data-width=\"100%\" data-numposts=\"20\">"
						+ "</div>"
					+ "</div>");
			fileData = fileData.replace("##feedback_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
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
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
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
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Spicy World, Arpita's Kitchen, Search recipes, recipe search, specific recipes, search foods");
			fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita Ghosh Das. Search recipes from our library of all recipes.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home3.jpg");
			fileData = fileData.replaceAll("##URL_DATA##", "/search.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "enableAd();searchForRecipe();");
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(basePath + "search.html", fileData);
			
			
			//About Me Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Arpita | About Me | Spicy World by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div><img alt='Arpita from Spicy World' title='Arpita from Spicy World' src='images/about.jpg" + imgBuildNo + "'  width='100%'/>"
					+ "<div id='aboutData'><div>" + aboutPageData + "</div></div></div>");
			fileData = fileData.replace("##about_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "About me, About Spicy World");
			fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita Ghosh Das. Here are the cast and crew of Spicy World.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home3.jpg");
			fileData = fileData.replaceAll("##URL_DATA##", "/about-me.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", "");
			saveFile(basePath + "about-me.html", fileData);
			
			
			//Save User Agreement Page
			fileData = readFile(basePath + "template/template.html");
			String uaData = readFile(basePath + "template/user-agreement.txt");
			fileData = fileData.replace("##TITLE_DATA##", "User Agreement | Spicy World by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", uaData);
			fileData = fileData.replace("##index_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "User Agreement, Cookie, Terms of Use");
			fileData = fileData.replaceAll("##DESC_DATA##", "User Agreement to use Spicy World.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home3.jpg");
			fileData = fileData.replaceAll("##URL_DATA##", "/user-agreement.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", "");
			saveFile(basePath + "user-agreement.html", fileData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		selfCopy(templatePath + "template/SiteBuilder.java", processor);
		System.out.println("Processed ...");
		//getAllImages(basePath + "recipeimages");
		MobileSiteBuilder.buildNo = buildNo;
		MobileSiteBuilder.mobileSiteBuilder();
		//CookBook.main(args);
	}
	
	
	private static void transformAllImages (String basePath) {
		String title = null;
		try {
			File fXmlFile = new File(basePath + "template/data.xml");
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
					title = eElement.getElementsByTagName("url").item(0).getTextContent();
					if ("chicken-tandoori".equals(title)) {
						break;
					}
					String img = title;
					createImage(basePath + "template/recipeimages/" + img + ".jpg", basePath + "recipeimages/" + img + ".jpg", 1500, true, basePath);
					System.out.println(basePath + "template/recipeimages/" + img + ".jpg");
					int i = 1;
					while (true) {
						String limg = img + "-" + i;
						File f = new File(basePath + "template/recipeimages/" + limg + ".jpg");
						if (f.exists()) {
							System.out.println(basePath + "template/recipeimages/" + limg + ".jpg");
							createImage(basePath + "/template/recipeimages/" + limg + ".jpg", basePath + "recipeimages/" + limg + ".jpg", 1500, true, basePath);
							i++;
						} else {
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
		fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "<link rel='alternate' media='only screen and (max-width: 640px)' href='http://spicyworld.in/mobile/" + tag + "-tag.html' />");
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
				data += "<span><a onclick=\"ga('send', 'event', 'Recipe Pagination', 'Recipe Pagination: Page " + i + "', this.href);\" title='" + pageTitle + "' href='" + pageURL + "'>" + i + "</a></span>";
			}
		}
		return data;
	}
	
	private static String relatedRecipes(List eElements, int position) {
		String retData = "";
		int totalCount = 6;
		int total = eElements.size();
		if (position == 0) {
			for (int i=1; i < (totalCount + 1); i++) {
				retData = bottomRelated(retData, (Element) eElements.get(i));
			}
		} else if (position == (total - 1) || (total - position) <= (totalCount/2)) {
			for (int i=(position - totalCount); i<position; i++) {
				retData = bottomRelated(retData, (Element) eElements.get(i));
			}
		} else {
			if (position >= totalCount) {
				for (int i=position - (totalCount/2); i<= position + (totalCount/2); i++) {
					if(i != position) {
						retData = bottomRelated(retData, (Element) eElements.get(i));
					}
				}
			} else {
				for (int i=0; i<(totalCount + 1); i++) {
					if(i != position) {
						retData = bottomRelated(retData, (Element) eElements.get(i));
					}
				}
			}
		}
		return retData;
	}
	
	private static String bottomRelated(String recipes_data, Element eElement) {
		String title = eElement.getElementsByTagName("title").item(0).getTextContent();
		String gaCode = "onclick=\"ga('send', 'event', 'Additional Recipe', 'Additional Recipe: bottom_links', this.href);\"";
		recipes_data += "<div class='imagesPage'><div><a " + gaCode+ " href=\"" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html\" title='" + title 
				+ "'><img alt='" + title + "' title='" + title + "' style=\"width: 280px !important;\" src=\""
				+ eElement.getElementsByTagName("pic").item(0).getTextContent() + imgBuildNo
				+ "\"/></a></div><div style=\"clear:both;padding-left:20px;width:280px;height:70px\"><a " + gaCode + " href=\"" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html\">" + title + "</a></div></div>";
		return recipes_data;
	}
	
	public static String getAllImages(String recipes_data, Element eElement) {
		recipes_data += "<div class='imagesPage'><div><a class=\"group1\" href=\"" + eElement.getElementsByTagName("pic").item(0).getTextContent() + imgBuildNo + "\" title='" + eElement.getElementsByTagName("title").item(0).getTextContent() 
				+ "'><img style=\"width: 212px !important;\" src=\""
				+ eElement.getElementsByTagName("pic").item(0).getTextContent() + imgBuildNo
				+ "\"/></a></div><div style=\"clear:both;padding-left:20px;width:212px;height:70px\"><a href=\"" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html\">" + eElement.getElementsByTagName("title").item(0).getTextContent() + "</a></div></div>";
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
		String url = eElement.getElementsByTagName("url").item(0).getTextContent();
		String title = eElement.getElementsByTagName("title").item(0).getTextContent();
		String pubDate = null;
		try {
			pubDate = "<pubDate>" + getDate(eElement.getElementsByTagName("pubDate").item(0).getTextContent()) + "</pubDate>";
		} catch (Exception e) {
			pubDate = "";
		}
		homeJSON = "<item><guid>http://www.spicyworld.in/" + url + ".html</guid><author>contact@spicyworld.in (Spicy World by Arpita)</author>" + pubDate
				+ "<title>" + title + "</title><link>http://www.spicyworld.in/" + url
		+ ".html</link><description><![CDATA[<img src='http://spicyworld.in/" + eElement.getElementsByTagName("pic").item(0).getTextContent() + "' alt='" + title + "' title='" + title + "' />" + eElement.getElementsByTagName("shortDesc").item(0).getTextContent().replace("class='topImagePosition descCSSStyle' ", "") + "]]></description></item>";
		return homeJSON;
	}
	
	private static String getDate(String inDate) {
		Date dd = new Date(inDate);
		SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        String dt[] = dd.toString().split(" ");
		return dt[0] + ", " + dt[2] + " " + dt[1] + " " + dt[5] + " 00:00:00 CST";
	}
	
	public static String siteMapEntry(Element eElement) {
		String siteMapDataEntry = null;
		siteMapDataEntry = "<url><loc>http://spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html</loc>"
				+ "<xhtml:link rel=\"alternate\" media=\"only screen and (max-width: 640px)\" href=\"http://spicyworld.in/mobile/" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html\" />"
				+ "</url>";
		return siteMapDataEntry;
	}
	
	public static void createItemData(String templatePath, Element eElement, int count, Element nextElement, Element prevElement, String relatedRecipes, String htmlTags) {
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
			additionalImg = "<div class='div3Pos posLeft'><br/><a onclick=\"ga('send', 'event', 'Image Click', 'Image Click: recipe_top', this.href);\" class=\"group1\" title=\"" + title + "\" href=\"" + additionalImg + imgBuildNo + "\"><img style=\"####\" alt='" + title 
			+ "' title='" + title + "' src='"
			+ additionalImg + imgBuildNo
			+ "' class='topImagePosition' /></a><br/><br/></div>";
			String addImgStyle = "";
			try {
				addImgStyle = eElement.getElementsByTagName("add-pic-style").item(0).getTextContent();
				additionalImg = additionalImg.replace("####", addImgStyle);
			} catch (Exception e) {
				additionalImg = additionalImg.replace("####", "");
			}
		} catch (Exception e) {
			additionalImg = "";
		}
		String endImg = "";
		String endImgStyle = "";
		try  {
			endImg = eElement.getElementsByTagName("end-pic").item(0).getTextContent();
			endImg = "<div class='div3Pos posLeft'><br/><a onclick=\"ga('send', 'event', 'Image Click', 'Image Click: recipe_bottom', this.href);\" class=\"group1\" title=\"" + title + " (Final)\" href=\"" + endImg + imgBuildNo + "\"><img style=\"##_##\" alt='" + title 
			+ "' title='" + title + "' src='"
			+ endImg + imgBuildNo
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
			steps = "<div class='steps-image'>" + steps.replace("<img ", "<img alt='Cooking Step: " + title + "' title='Cooking Step: " + title + "' ") + "</div>";
		}
		DateFormat fullDf = DateFormat.getDateInstance(DateFormat.MEDIUM);
		String datepub;
		try {
			datepub = fullDf.format(new Date(eElement.getElementsByTagName("pubDate").item(0).getTextContent()));
		} catch (Exception e1) {
			datepub = eElement.getElementsByTagName("pubDate").item(0).getTextContent();
		}
		
		out = "<div vocab='http://schema.org/' typeof='Recipe'><div class='h2Class'><div style=\"clear:both\"><h1 property='name' id='title'>"
				+ title
				+ "</h1></div><div class='copyright copy" + type + "'><i>&copy; 2016 Spicy World</i>, Published on: <i>" + datepub + "</i></div><div style=\"clear:both\">"
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
				+ "<div style=\"float:left;padding-left:10px;\"><div class=\"fb-send\" data-href=\"http://spicyworld.in/" + url + ".html\"></div></div>"
				+ "<div style=\"float:left;padding-left:10px;\"><div class=\"fb-like\" data-href=\"http://spicyworld.in/"
				+ url
				+ ".html\" data-layout=\"button_count\" data-action=\"like\" data-show-faces=\"true\" data-share=\"true\"></div></div>" 
				// FB Ends
				+ "</div>"
				+ "<p class=\"descp\" id='description' property='description' style=\"padding-top:8px;clear:both\">" + desc + "</p></div><br/>"
				+ "<div>"
				+ "<div class='div3Pos posLeft'><a onclick=\"ga('send', 'event', 'Image Click', 'Image Click: recipe_top_main', this.href);\" class=\"group1\" title=\"" + title + "\" href=\"" + eElement.getElementsByTagName("pic").item(0).getTextContent() + imgBuildNo + "\"><img alt='" + title 
				+ "' title='" + title + "' src='"
				+ eElement.getElementsByTagName("pic").item(0).getTextContent() + imgBuildNo
				+ "' class='topImagePosition' /></a><br/><br/><div><h2 id='ingredients'>Ingredients</h2></div><div property='ingredients'>"
				+ eElement.getElementsByTagName("ingrediants").item(0)
						.getTextContent()
				+ "</div></div>" + additionalImg
				+ "<div class='div3Pos'><div><h2 id='steps'>Steps</h2></div><div property='recipeInstructions'>"
				+ steps.replace(".jpg", ".jpg" + imgBuildNo)
				+ "</div><br/><div class='complete'>"
				+ eElement.getElementsByTagName("completionStatement").item(0)
						.getTextContent()
				+ "</div>"
				+ "<div class='garnishment'>"
				+ eElement.getElementsByTagName("garnishment").item(0)
						.getTextContent() + "</div></div>" + "</div></div>" + endImg;
		

		
		String oldNew = SiteBuilder.nextPreviousPagination(prevElement, nextElement, "");
		try {
			String tagData = eElement.getElementsByTagName("tags").item(0).getTextContent();
			String tags = "<div id='tags' class=\"tagContent\">";
			if (tagData!=null) {
				String ss[] = tagData.split(",");
				for (int i=0; i<ss.length; i++) {
					String tg = ss[i];
					tags += "<span><a onclick=\"ga('send', 'event', 'Tag Click', 'Tag Click: recipe_tags', this.href);\" href=\"" + tg.replace(" ", "-") + "-tag.html\">" + tg + "</a></span>";
				}
			}
			tags += "</div>";
			out += tags;
		} catch (Exception e) {} 
		if (relatedRecipes!=null && relatedRecipes.length()>0) {
			relatedRecipes = "<br/><div id='relatedRecipes' class='relatedRecipes'>You may also like</div><div class='clear relatedRecipesData'>" + relatedRecipes + "</div>";
		}
		out += "<div class='botNextPrev'>" + oldNew + "</div>" + relatedRecipes
		+ "<br/><br/>"
				+"<div id='relatedRecipesTags' class='relatedRecipes'>Categories</div><div class='bottomLinksTags'>" + htmlTags.replace("href=", "onclick=\"ga('send', 'event', 'Tag Click', 'Tag Click: recipe_bottom', this.href);\" href=") + "</div>"
		// For comment section
		+ "<br/><div style=\"clear:both;padding-top:20px;padding-bottom:20px;\"><div id='comments' class='commentHeader'>Leave Your Comments</div>"
		// For Disqus
		//+ "<div class='disqus_thread_class'><div id=\"disqus_thread\"></div><script type=\"text/javascript\"> var disqus_shortname = 'spicyworld';  (function() {var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true; dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js';(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);  })();</script></div>"
		+ "<div class=\"fb-comments\" data-href=\"http://spicyworld.in/" + url + ".html\" data-width=\"100%\" data-numposts=\"5\"></div>"
		+ "</div>";
		
		
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
		
		if ("nonVegItem".equals(type)) {
			fileData = fileData.replace("##recipe_type##", "nonVegTopLine");
		} else {
			fileData = fileData.replace("##recipe_type##", "vegTopLine");
		}
		
		
		fileData = fileData.replace("##TITLE_DATA##", "How to cook " + title + " | Spicy World by Arpita");
		fileData = fileData.replace("##MIDDLE_DATA##", oldNew + "<div class='recipeDataPage'>" + out + "</div><div class=\"clear\">&nbsp;</div>");
		fileData = fileData.replace("##recipes_sel##", "selected");
		fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
		fileData = fileData.replaceAll("##KEYWORD_DATA##", keyword);
		fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
		fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "<link rel='alternate' media='only screen and (max-width: 640px)' href='http://spicyworld.in/mobile/" + url + ".html' />");
		fileData = fileData.replaceAll("##DESC_DATA##", CookBook.html2text(desc));
		fileData = fileData.replaceAll("##IMG_DATA##", eElement.getElementsByTagName("pic").item(0).getTextContent());
		fileData = fileData.replaceAll("##URL_DATA##", "/" + url + ".html");
		fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
		fileData = fileData.replaceAll("##ONLOAD_CALL##", "initFancy();enableAd();homeImg();");
		
		saveFile(templatePath + url + ".html", fileData);
		System.out.println(count + ". Created Web HTML for " + url);
	}

	public static String recepiData(String recipes_data, Element eElement, String prefix) {
		String type = eElement.getElementsByTagName("type").item(0)
				.getTextContent();
		String text = "";
		if ("nonVegItem".equals(type)) {
			text = "Nonveg Recipe";
		} else {
			text = "Veg Recipe";
		}
		String data = eElement.getElementsByTagName("shortDesc").item(0).getTextContent();
		data = CookBook.html2text(data);
		if (data.length() > 400) {
			data = data.substring(0, 400) + " ...";
		}
		
		recipes_data += "<div class='recipeListPageItem'>"
				+ "<div class='recipeListPageItemLeft'><a href='" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html'>"
				+ "<img title='" + eElement.getElementsByTagName("title").item(0).getTextContent() 
				+ "' alt='" + eElement.getElementsByTagName("title").item(0).getTextContent() + "' src=\""
				+ prefix + eElement.getElementsByTagName("pic").item(0)
						.getTextContent() + imgBuildNo
				+ "\" /></a><div class='" + type + " itemTypeLabel'>" + text + "</div></div>"
				+ "<div class='recipeListPageItemRight'>"
				+ "<div class=\"title\">"
				+ "<a alt=\"" + eElement.getElementsByTagName("title").item(0).getTextContent() + "\" "
						+ "title=\"" + eElement.getElementsByTagName("title").item(0).getTextContent() + "\" class='noStyle' href=\""
				+ eElement.getElementsByTagName("url").item(0).getTextContent()
				+ ".html\">" + eElement.getElementsByTagName("title").item(0).getTextContent()
				+ "</a></div>"
				+ "<div class=\"desc\">"
				+ data + "</div>"
				+ "</div>"
				+ "</div>";
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
				if (!line.equals("")) {
					fileData += "\n" + line;
				} else {
					fileData += "";
				}
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
			int destinationWidth, String basePath) {
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
					createImage(src, dest, destinationWidth, basePath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listFiles;
	}

	private static void createImage(String source, String destination, int destinationWidth, String basePath) {
		createImage(source, destination, destinationWidth, false, basePath);
	}
	
	private static void createImage(String source, String destination, double destinationWidth, boolean waterMarkFlag, String basePath) {
		try {
			BufferedImage bimg = ImageIO.read(new File(source));
			double width = bimg.getWidth(), height = bimg.getHeight();
			BufferedImage image = ImageIO.read(new File(source));
	        BufferedImage overlay = ImageIO.read(new File(basePath + "/images/water-mark.png"));
	        

	        // create the new image, canvas size is the max. of both image sizes
	        BufferedImage combined = new BufferedImage(convertToNearestInt(width), convertToNearestInt(height), BufferedImage.TYPE_INT_BGR);
	        // paint both images, preserving the alpha channels
	        Graphics g = combined.getGraphics();
	        g.drawImage(image, 0, 0, null);
	        g.drawImage(overlay, 20, convertToNearestInt(height) - 115, null);
	        ImageIO.write(combined, "jpg", new File(destination));
	        
	        reduceImageFileSize(destination, destinationWidth);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private static void reduceImageFileSize (String filePath, double destinationWidth) {
		try {
		String srcPath = filePath;
		String destPath = filePath;
		float quality = 0.15f;
		Iterator iter = ImageIO.getImageWritersByFormatName("jpg");

		ImageWriter writer = (ImageWriter)iter.next();

		ImageWriteParam iwp = writer.getDefaultWriteParam();

		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

		iwp.setCompressionQuality(quality);

		File file = new File(destPath);
		FileImageOutputStream output = new FileImageOutputStream(file);
		writer.setOutput(output);

		FileInputStream inputStream = new FileInputStream(srcPath);
		BufferedImage originalImage = ImageIO.read(inputStream);

		IIOImage image = new IIOImage(originalImage, null, null);
		writer.write(null, image, iwp);
		writer.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static int convertToNearestInt (double ff) {
		int i = 0;
		i = (int) ff;
		return i;
	}
	
	private static void resImg(String file, String output, int destinationWidth) {
		try {
			// Take the width,height as 2,3 args
	        int w = destinationWidth;
	        int h= -1;
	       
	        // Get the BufferedImage object by reading the image
	        // from the given input stream
	        BufferedImage bim=ImageIO.read(new FileInputStream(file));
	       
	        // I am using fast scaling
	        Image resizedImg=bim.getScaledInstance(w,h,Image.SCALE_FAST);
	       
	        // Create a BufferedImage object of w,h width and height
	        // and of the bim type
	        BufferedImage rBimg=new BufferedImage(w,h,bim.getType());
	       
	        // Create Graphics object
	        Graphics2D g=rBimg.createGraphics();
	       
	        // Draw the resizedImg from 0,0 with no ImageObserver
	        g.drawImage(resizedImg,0,0,null);
	       
	        // Dispose the Graphics object, we no longer need it
	        g.dispose();
	       
	        // Now, what? Just write to another file
	       
	        // The first argument is the resized image object
	        // The second argument is the image file type, So i got the
	        // extension of the output file and passed it
	        // The next argument is the FileOutputStream to where the resized
	        // image is to be written.
	        ImageIO.write(rBimg,output.substring(output.indexOf(".")+1),new FileOutputStream(output));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String nextPreviousPagination (Element prevElement, Element nextElement, String deviceType) {
		//Next Previous Link Starts
		String gaCode = "onclick=\"ga('send', 'event', 'Recipe Next Prev" + deviceType + "', 'Recipe Next Prev: link_#POS#', this.href);\"";
		String prev = "", next = "";
		if (prevElement != null) {
			String prevTitle = "Next Recipe: " + prevElement.getElementsByTagName("title").item(0).getTextContent();
			prev = "<a " + gaCode.replace("#POS#", "left") + " title='" + prevTitle + "' alt='" + prevTitle + "' class='prevLink' href='" + prevElement.getElementsByTagName("url").item(0).getTextContent() + ".html'>&nbsp;Previous Recipe</a>";
		} else {
			prev = "<span class='prevLinkD'>&nbsp;Previous Recipe</span>";
		}
		if (nextElement != null) {
			String nextTitle = "Previous Recipe: " + nextElement.getElementsByTagName("title").item(0).getTextContent();
			next = "<a " + gaCode.replace("#POS#", "right") + " title='" + nextTitle + "' alt='" + nextTitle + "' class='netxLink' href='" + nextElement.getElementsByTagName("url").item(0).getTextContent() + ".html'>Next Recipe&nbsp;</a>";
		} else {
			next = "<span class='netxLinkD'>Next Recipe&nbsp;</span>";
		}
		return "<div class='clear linkColor topNextPrevNavLinks'><div class='fleft'>" + prev + "</div><div class='fright'>" + next + "</div></div>";
	}
	
	private static void compressFiles(String srcBase, String jarBase) {
		String jarFile = jarBase + "/jar/yuicompressor-2.4.8.jar";
		String jsFile = srcBase + "/template/js/site.js";
		String jsDest = srcBase + "/js/site.js";
		String compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
		try {
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(compressString);
			System.out.println("Compressing: " + jsFile);
			
			jsFile = srcBase + "/template/js/siteCommon.js";
			jsDest = srcBase + "/js/siteCommon.js";
			compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
			pr = rt.exec(compressString);
			System.out.println("Compressing: " + jsFile);
			
			
			jsFile = srcBase + "/template/css/site.css";
			jsDest = srcBase + "/css/site.css";
			compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
			pr = rt.exec(compressString);
			System.out.println("Compressing: " + jsFile);
			
			
			jsFile = srcBase + "/template/css/site-mobile.css";
			jsDest = srcBase + "/mobile/css/site.css";
			compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
			pr = rt.exec(compressString);
			System.out.println("Compressing: " + jsFile);
			
			jsFile = srcBase + "/template/js/site-mobile.js";
			jsDest = srcBase + "/mobile/js/site-mobile.js";
			compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
			pr = rt.exec(compressString);
			System.out.println("Compressing: " + jsFile);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}