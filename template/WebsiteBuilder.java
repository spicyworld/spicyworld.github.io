package utility;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;

import com.mysql.jdbc.Util;

public class WebsiteBuilder {
	
	public static String buildNo = "?sessionId=" + PropertiesUtil.getValue("buildNo");
	public static String imgBuildNo = ""; //"?sessionId=" + PropertiesUtil.getValue("image.buildNo");
	public static String pinterestData = "<script async data-pin-color=\"red\" data-pin-hover=\"true\" defer src=\"//assets.pinterest.com/js/pinit.js\"></script>";
	public static String aboutPageData = "Hello Friends, <br/><br/><h1 class='specialH1'>Arpita</h1>&nbsp;is a daughter and homemaker from two lovely Bengali families. At present she lives in West Bengal, India with her husband Amitava & a growing little boy <i>CoCo</i>. Along side with her Parents and In-Laws.<br/><br/>They both are real food lovers.<br/><br/>Cooking, learning about new recipes, listening and singing old songs in lonely afternoons are her hobbies. Arpita is also a big fan and follower of authentic bengali cooking and very much all kinds of indian street foods. Everyday as a self taught cook she paints her food with spices, colors, love and care. Behind everything Amitava is her real inspiration. After marriage, getting compliments from husband about cooking is a great achievement for her. Trying different cuisines and traveling various places are her favourite hobbies.<br/><br/>This blog is the reflection of her love towards food and passion for travel. Travel and food they are like two sides of a coin and co-exist or better to say compliments each other. To experience various culture and their food, you need to visit places and thats what she is trying to portray here in her blog.<br/><br/>So, she heartily invites you all to take a colorful journey through her little \"<a href='http://spicyworld.in'>Spicy World</a>\" ...<br/><br/>Contact us: <u><a href='mailto:contact@spicyworld.in' target='_top' class='email-btn-click'>contact@spicyworld.in</a></u>";
	public static String recipeMenuData = "";
	public static String basePath = PropertiesUtil.getValue("repotemplate.path");
	public static String headerGAData = readFile(basePath + "template/header-ga.txt");
	
	public static void main(String ss[]) {
		buildWebsite();
	}
	
	public static void buildWebsite() {
		String srcBasePath = PropertiesUtil.getValue("fs.path");
		String processorBasePath = PropertiesUtil.getValue("fs.path") + PropertiesUtil.getValue("config.path");
		
		//Compress files
		compressFiles(basePath, processorBasePath.substring(0, processorBasePath.length()-1));
		searchReadyJSON(basePath, processorBasePath.substring(0, processorBasePath.length()-1));
		//System.exit(1);
		
		String templatePath = basePath;
		String tag_data_template = templatePath + "template/template.html";
		String recipes_data_front = "<div>";
		String recipes_data = "";
		String siteMapData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\">";
		siteMapData = siteMapData + staticEntriesSiteMap();
		/*String rssXMLData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\"><channel>"
				+ "<title>Spicy World</title><link>http://www.spicyworld.in</link>"
				+ "<description>Welcome to Spicy World by Arpita. Easy and Simple Recipes make your cooking faster and your food delicious.</description>"
				+ "<atom:link href=\"http://spicyworld.in/rss.xml\" rel=\"self\" type=\"application/rss+xml\" />";
		*/
		String rssXMLData = "<rss version=\"2.0\"><channel>"
				+ "<title>Spicy World</title><link>http://www.spicyworld.in</link>"
				+ "<description>Welcome to Spicy World by Arpita. Easy and Simple Recipes make your cooking faster and your food delicious.</description>"
				+ "<atom:link href=\"http://spicyworld.in/rss.xml\" rel=\"self\" type=\"application/rss+xml\" />";
		String recipes_data_img = "", fileData = "";
		int count = 1, perPageData = 10;
		List recipeDataList = new ArrayList();
		String tags = "";
		String carosalImg = "<ul class='ei-slider-large'>";
		String carosalImgThumb = "<ul class='ei-slider-thumbs'><li class='ei-slider-element'>Current</li>";
		//String notice = "";
		String topTags = "";
		String latest3DataForHomePage = "<div class=\"middleTop\">##HOME_IMAGE_TOP##</div><div class=\"middleBottom\">";
		String nextAvailable = "", prevAvailable = "";
		List elementList = new ArrayList();
		int totalRecipes = 0, totalFeaturedPosts = 0;
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
					try {
						if ("sb".equals(eElement.getElementsByTagName("blog-type").item(0).getTextContent())) {
							totalFeaturedPosts = totalFeaturedPosts + 1;
						} else {
							totalRecipes = totalRecipes + 1;
						}
					} catch (Exception e) {
						totalRecipes = totalRecipes + 1;
					}
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
			
			//Populating tags page data
			recipeMenuData = sortByValue(tagMap);
			
			Iterator iterator = tagMap.entrySet().iterator();
			String htmlTags = "", keywordTags = "", specialHTMLTags = "";
			count = 0;
			int fontSize = 10;
			while (iterator.hasNext()) {
				Map.Entry<String,Integer> entry = (Map.Entry<String,Integer>) iterator.next();
				String data = entry.getKey();
				keywordTags += data + " ";
				data = data.replace(" ", "-");
				htmlTags += "<span data-weight=\"" + (entry.getValue() + 10) + "\"><a title=\"" + entry.getKey()  + "\" href=\"" + data + "-tag.html\">" + entry.getKey() + " (" + entry.getValue() + ")</a></span>";
				if (entry.getValue() > 50) {
					fontSize = (50 * 100) / 20 + entry.getValue();
				} else if (entry.getValue() < 10) {
					fontSize = (10 * 100) / 20 + entry.getValue() * 30;
				} else {
					fontSize = (entry.getValue() * 100) / 20 + entry.getValue() * 2;
				}
				specialHTMLTags += "<span><a style='color: " + Utility.getColorCode() + ";font-size: " + fontSize + "%;' class=\"data-points\" href=\"" + data + "-tag.html\" title='" + entry.getKey() + " [Recipes# " + entry.getValue() + "]'>" + entry.getKey() + " (" + entry.getValue() + ")</a></span>";
				
				count++;
				String h1Tag = "<h1 class='headerFont'>Posts on <i><b>'" + entry.getKey() + "'</b></i></h1>";
				generateTagHTML(data, tag_data_template, nList, templatePath, count, entry.getKey(), h1Tag);
				siteMapData += "<url><loc>http://spicyworld.in/" + data + "-tag.html</loc></url>";
			}
			
			// Create tag cloud Ends
			count = 1;
			int itemCount = 0;
			String blogTable = "";
			
			Node[] nodeArrayToSort = (Node[]) elementList.toArray(new Node[elementList.size()]);
			Arrays.sort(nodeArrayToSort, new NodeComparator());
			
			elementList = Arrays.asList(nodeArrayToSort);
			
			for (int i=0;i<elementList.size();i++) {
			//for (int i=0;i<nodeArrayToSort.length;i++) {
				Element nextElement = null, prevElement = null;
				Element eElement = (Element) elementList.get(i);
				String descMod = Utility.html2text(eElement.getElementsByTagName("shortDesc").item(0).getTextContent());
				if (descMod.length() > 100) {
					descMod = descMod.substring(100);
				}
				//if (i<5) {
				blogTable += "<br/><br/><div><a href='http://spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html?from=blog'><img style='box-shadow: 9px 8px 5px #888888;padding: 2px;background: grey;' width='550px' src='http://spicyworld.in/" + eElement.getElementsByTagName("pic").item(0)
						.getTextContent() + "'/></a></div>"
						+ "<div style='padding-top:12px'><a style='font-size: 26px;color: #22B5E3;' href='http://spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html?from=blog'>" + eElement.getElementsByTagName("title").item(0).getTextContent()+ "</a></div>"
								+ "<div>" + descMod + " ..." + "</div>";
				//}
				if (count % perPageData == 0) {
					recipeDataList.add(recipes_data_front + recipes_data + "</div></div>");
					recipes_data = "";
					itemCount = 0;
				}
				String relatedRecipes = relatedRecipes(elementList, i);
				recipes_data = recepiData(recipes_data, eElement, "", itemCount);
				itemCount ++;
				recipes_data_img = getAllImages(recipes_data_img, eElement);
				if (i > 0) {
					prevElement = (Element) elementList.get(i-1);
				}
				if (i < (elementList.size()-1)) {
					nextElement = (Element) elementList.get(i+1);
				}
				createItemData(templatePath, eElement, count, nextElement, prevElement, relatedRecipes, htmlTags);
				String classToApply = null;
				if (count == 1 || count == 4) {
					classToApply = "left";
				} else if (count == 2 || count == 5) {
					classToApply = "middle";
				} else if (count == 3 || count == 6) {
					classToApply = "right";
				} else {
					classToApply = null;
				}
				if (classToApply!=null) {
					String title = eElement.getElementsByTagName("title").item(0).getTextContent();
					String desc = eElement.getElementsByTagName("shortDesc").item(0).getTextContent();
					desc = Utility.html2text(desc).replace("\"", "'");
					String addTitle = title;
					try { 
						addTitle = title + " / " + eElement.getElementsByTagName("add-title").item(0).getTextContent();
					} catch (Exception e) {}
					String url = eElement.getElementsByTagName("url").item(0).getTextContent();
					if ("left".equals(classToApply)) {
						latest3DataForHomePage += "<div class='clearboth'>";
					}
					String tagData = eElement.getElementsByTagName("tags").item(0).getTextContent();
					String tagHTML = getTagHTML(tagData);
					String datepub = getDateView(eElement.getElementsByTagName("pubDate").item(0).getTextContent());
					String youTubeLink = "";
					try {
						youTubeLink = eElement.getElementsByTagName("youtube-url").item(0).getTextContent();
						youTubeLink = "<a class='readMore marginleft10px youtube-video' title=\"Youtube Video for " + addTitle + "\" href=\"" + youTubeLink + "\" target=\"_blank\">Youtube Video >></a>";
					} catch (Exception e) {}
					
					latest3DataForHomePage += "<div class=\"" + classToApply + "\">"
													+ "<div class=\"img-hover-zoom\"><a class=\"homepage-recipe-image\" title=\"" + addTitle + "\" href=\"" + url + ".html\"><img title=\"" + addTitle + "\" alt=\"" + addTitle + "\" src=\"" + eElement.getElementsByTagName("pic").item(0).getTextContent() + "\"/></a></div>"
													+ "<div><a class=\"homepage-recipe-title\" title=\"" + addTitle + "\" href=\"" + url + ".html\"><h2 class='homeTitleH2'>" + addTitle + "</h2></a></div>"
													+ "<div class='descHomeDate'>" + datepub + "</div>"
													+ "<div class='desc'>" + desc + "</div>"
													+ "<div class='linkReadMoreDiv'><a class='readMore homepage-recipe-readmore' title=\"" + addTitle + "\" href=\"" + url + ".html\">Read More >></a>" + youTubeLink + "</div>"
													+ tagHTML
											+ "</div>";	
					if ("right".equals(classToApply)) {
						latest3DataForHomePage += "</div>";
					}
				}
				if (count > 6 && count < 17) {
					String datepub = getDateView(eElement.getElementsByTagName("pubDate").item(0).getTextContent());
					String title = eElement.getElementsByTagName("title").item(0).getTextContent();
					try {
						title = title + " / " + eElement.getElementsByTagName("add-title").item(0).getTextContent();
					} catch (Exception e) {}
					carosalImg += "<li><img src='" + eElement.getElementsByTagName("pic").item(0).getTextContent() + "' title='" + title + "' alt='" + title + "'/><div class='ei-title'><h2>" + title + "</h2><h3>" + datepub + "</h3></div></li>";
					carosalImgThumb += "<li><a href='" + eElement.getElementsByTagName("url").item(0).getTextContent() +".html'>" + title + "</a><img src='" + eElement.getElementsByTagName("pic").item(0).getTextContent() + "' alt='" + title + "' /></li>";
				}
				count++;
				siteMapData += siteMapEntry(eElement);
				rssXMLData += populateRSSData(eElement);
			}
			if (elementList.size() % 2 == 1) {
				recipes_data += "</div>";
			}
			System.out.println(blogTable);
			carosalImg += "</ul>";
			carosalImgThumb += "</ul>";
			carosalImg = "<div id='ei-slider' class='ei-slider'>" + carosalImg + carosalImgThumb + "</div>";
			carosalImg += "<div class='staticMessage'><div class='homeJoinUsDivBot'><h1 class='topH1Tag'>Spicy World</h1> is a food blog where you can find Simple and easy recipes to make you and your loved ones happy and smile. Check out all our ##RECIPES## recipes and ##POSTS## featured posts.</div><a class=\"home-all-posts\" href=\"recipes.html\">ALL POSTS</a><div class='homeJoinUsDiv homeJoinUsDivBot'>Any Questions, feel Free to Email Us !!!</div><a class=\"home-email\" href=\"mailto:contact@spicyworld.in\">contact@spicyworld.in</a></div>";
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
				
				fileData = fileData.replace("##TITLE_DATA##", "Recipes - Page " + (i+1) + " - Spicy World Simple and Easy Recipes by Arpita");
				fileData = fileData.replace("##MIDDLE_DATA##", "<div class='paginationHeader'><h1 class='headerFont'>Our Recipes - Page " + (i+1) + "</h1><div class='topPaginationData'>" + pagination + "</div></div><div class='recipePage'>" + recipes_data + "</div><br/><div class='topPaginationData'>" + pagination + "</div><div class='clear'>&nbsp;</div>");
				fileData = fileData.replace("##recipes_sel##", "selected");
				fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
				fileData = fileData.replaceAll("##KEYWORD_DATA##", "Recipes in Spicy World");
				fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita. Here are our recipes (page number " + (i+1) + ") that you might like.");
				fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
				fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
				fileData = fileData.replaceAll("##HEADER_GA##", "");
				fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
				
				
				if (i > 0) {
					fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
					fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
					fileData = fileData.replaceAll("##URL_DATA##", "/recipes-" + i + ".html");
					saveFile(templatePath + "recipes-" + i + ".html", fileData);	
					siteMapData += "<url><loc>http://spicyworld.in/recipes-" + i + ".html</loc></url>";
				} else {
					fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
					fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
					fileData = fileData.replaceAll("##URL_DATA##", "/recipes.html");
					saveFile(templatePath + "recipes.html", fileData);	
					siteMapData += "<url><loc>http://spicyworld.in/recipes.html</loc></url>";
				}
			}
			
			//Save rss.xml
			saveFile(templatePath + "rss.xml", formatXML(rssXMLData.replace("&", "and") + "</channel></rss>"));
			
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "All Recipes and Food Images - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<h1 class='headerFont'>All Our Posts</h1><div role='main'><div class='wordcloudImg'>" + recipes_data_img + "</div></div><div class=\"clear\">&nbsp;</div>");
			fileData = fileData.replace("##all-food-images_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "All images, images in Spicy World, Our food images");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##DESC_DATA##", "You can find all images of our Recipes in Spicy World. You can now also visit the recipe details from here by clicking the links below the images.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/all-food-images.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "initFancyAll();");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(templatePath + "all-food-images.html", fileData);
			
			// food plating
			fileData = readFile(basePath + "template/template.html");
			File directory = new File("c:\\code\\foodplating-src\\");
			String filePath = "";
	        //get all the files from a directory
	        File[] fList = directory.listFiles();
	        for (File file : fList){
	            if (file.isFile()){
	            	filePath = "c:\\code\\foodplating-src\\" + file.getName();
	            	Utility.resizeAndWaterMarkImg(filePath, "c:\\code\\spicyworld\\foodplating\\" + file.getName().replaceAll(" ", "-").replaceAll("&", "").toLowerCase(), false);
	                System.out.println("Food Plating: Converting " + filePath);
	            }
	        }
			recipes_data_img = readFile(basePath + "template/food-plating.txt");
			fileData = fileData.replace("##TITLE_DATA##", "Food Plating - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<h1 class='headerFont'>Food Plating</h1><div role='main'><div class='wordcloudImg'>" + recipes_data_img + "</div></div><div class=\"clear\">&nbsp;</div>" + "<br/><div class='addBottomRecipePage'><div id='commentSection'><div id='comments' class='commentHeader'>Leave Your Comments</div><br/><div class=\"fb-comments\" data-href=\"http://spicyworld.in/food-plating.html\" data-width=\"100%\" data-numposts=\"8\"></div></div></div>");
			fileData = fileData.replace("##food-plating_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Food Plating, Daily food, home food");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##DESC_DATA##", "Day to day life we have some food that are captured in our camera, we just add the for our viewers under this page.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/food-plating.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "initFancyAll();");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(templatePath + "food-plating.html", fileData);
			
			// Save Tags
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Recipe Categories or Tags - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<h1 class='hide'>Categories</h1><br/><div role='main' class='siteTagsAll'><div class='siteTagsAllMobile'>" + specialHTMLTags.replace("href=", "class=\"tag-page-click\" href=") + "</div></div>");
			fileData = fileData.replace("##tags_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##KEYWORD_DATA##", keywordTags.trim());
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##DESC_DATA##", "Tag cloud (Categories) is an easy way to link multiple content and you can easily choose the content you are looking for from various tags or categories.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/tags.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(templatePath + "tags.html", fileData);
			
			// Save Videos
			/*fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Recipe Videos - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<h1 class='hide'>Videos</h1><br/><div role='main' class='siteTagsAll'><div class='siteTagsAllMobile'>" + specialHTMLTags.replace("href=", "onclick=\"ga('send', 'event', 'Category Click', 'Category Click: tag_page', this.href);\" href=") + "</div><div id='wordcloud1' class='wordcloud'>" + htmlTags.replace("href=", "onclick=\"ga('send', 'event', 'Tag Click', 'Tag Click: tag_page', this.href);\" href=") + "</div></div>");
			fileData = fileData.replace("##tags_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Recipe, videos, youtube, links, new videos, stay tuned, our videos");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<script type=\"text/javascript\" src=\"js/jquery.awesomeCloud-0.2.min.js\"></script>");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "<script type='text/javascript'>window.onresize = function(event) {var div = document.getElementById('awesomeCloudwordcloud1');div.parentNode.removeChild(div);startTagPage();}</script>");
			fileData = fileData.replaceAll("##DESC_DATA##", "Spicyworld videos are now present in youtube, a new video in every week, like, share & comment in youtube. Subscribe to our channel for daily updates.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/tags.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(templatePath + "videos.html", fileData);*/
			
			//Saving sitemap.xml
			saveFile(basePath + "sitemap.xml", formatXML(siteMapData + "</urlset>").replace("UTF-16", "UTF-8"));
			
			//Save HomePage
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Welcome Foodies - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", latest3DataForHomePage.replace("##RECIPES##", "<b>" + totalRecipes + "</b>").replace("##POSTS##", "<b>" + totalFeaturedPosts + "</b>"));
			fileData = fileData.replace("##index_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<script type='text/javascript' src='js/jquery.eislideshow.js'></script><script type='text/javascript' src='js/jquery.easing.1.3.js'></script>");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "<link rel='stylesheet' type='text/css' href='css/home-slide.css'/>");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Spicy World, Arpita's Kitchen, Food Recipes, All Spicy Foods, indian food, spicy indian recipes.");
			fileData = fileData.replaceAll("##DESC_DATA##", "Easy and Simple Recipes make your cooking faster and your food delicious. Check out all available recipes.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "loadSlideShow();");
			fileData = fileData.replaceAll("##HEADER_GA##", "");
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(basePath + "index.html", fileData);
			
			
			//Save Feedback Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Feedback or Comment - Spicy World Simple and Easy Recipes by Arpita");
			//fileData = fileData.replace("##MIDDLE_DATA##", "<div style=\"clear:both;width:95%;min-height: 500px;padding:20px;\"><div id=\"disqus_thread\"></div><script type=\"text/javascript\"> var disqus_shortname = 'spicyworld';  (function() {var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true; dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js';(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);  })();</script></div>");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div style=\"clear:both;width:95%;min-height: 500px;padding:20px;\"><div class=\"fb-comments\" data-href=\"http://spicyworld.in\" data-width=\"100%\" data-numposts=\"20\"></div></div>");
			fileData = fileData.replace("##index_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Spicy World, Arpita's Kitchen, Feedback to Spicy World, Leave your comment.");
			fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita. Please provide your feedback or your comments about our recipes.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/feedback.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(basePath + "feedback.html", fileData);
			
			//Save Mobile apps page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Mobile companion apps for Android users - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", readFile(basePath + "template/our-apps.txt"));
			fileData = fileData.replace("##index_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Spicy World, Arpita's Kitchen, Mobile apps. Android apps. Food/recipe apps.");
			fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita. Our mobile companion apps will help you in every step of your daily food and recipe findings.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/feedback.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(basePath + "our-apps.html", fileData);
			
			
			//Save 404 Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Page Not Found (404) - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div style='min-height: 550px; text-align:center;padding: 100px;'><div style='font-size:50px;'>We are Sorry !!</div>"
					+ "<div style='background-color: lightgrey;padding: 20px;font-size: 21px;'>This is a custom 404 (Page Not found) page.<br/><br/>Please click Home or any other tabs from the header menu to navigate within the website.</div>"
					+ "</div>");
			fileData = fileData.replace("##indexss_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "<base href=\"http://spicyworld.in\">");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Spicy World, Arpita's Kitchen, This is a custom 404 Page by Spicy World.");
			fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita. This is a custom 404 page (Page Not found). Please click Home or any other tabs from header menu to navigate within the website.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/404.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(basePath + "404.html", fileData);
			
			
			//Save Search Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Search Recipes - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div id='searchMdl'><div><img width='40px' src=\"images/loading.gif\" nopin='nopin'/></div><div class='searchTxt'>Searching for recipes, please wait ...</div>"
					+ "</div>");
			fileData = fileData.replace("##recipes_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "<script type='text/javascript' src='js/searchdata.js" + buildNo + "'></script>");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Spicy World, Arpita's Kitchen, Search recipes, recipe search, specific recipes, search foods");
			fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita. Search recipes from our library of all recipes.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/search.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "searchForRecipe();");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
			saveFile(basePath + "search.html", fileData);
			
			
			//About Me Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "About Arpita - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div class='aboutPageMain'><img alt='Arpita from Spicy World' title='Arpita from Spicy World' src='images/about-arpita.jpg" + imgBuildNo + "'/>"
					+ "<div id='aboutData' class='aboutData'><div>" + aboutPageData + "</div></div></div>");
			fileData = fileData.replace("##about_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "About me, About Spicy World");
			fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita. Here are the cast and crew of Spicy World.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/about-me.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", "");
			saveFile(basePath + "about-me.html", fileData);
			
			
			//Save User Agreement Page
			fileData = readFile(basePath + "template/template.html");
			String uaData = readFile(basePath + "template/user-agreement.txt");
			fileData = fileData.replace("##TITLE_DATA##", "User Agreement - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", uaData);
			fileData = fileData.replace("##index_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "User Agreement, Cookie, Terms of Use");
			fileData = fileData.replaceAll("##DESC_DATA##", "User Agreement to use Spicy World.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/user-agreement.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", "");
			saveFile(basePath + "user-agreement.html", fileData);
			
			//Save Recipe Share Page
			fileData = readFile(basePath + "template/template.html");
			uaData = readFile(basePath + "template/recipe-how-to.txt");
			fileData = fileData.replace("##TITLE_DATA##", "How to share Recipes - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", uaData);
			fileData = fileData.replace("##index_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "How to share Recipes, Cookie, Terms of Use");
			fileData = fileData.replaceAll("##DESC_DATA##", "How to share Recipes with Spicy World.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/recipe-how-to-share.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", "");
			saveFile(basePath + "recipe-how-to-share.html", fileData);
			
			//Save Collaborate Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Collaborate by Recipe Sharing & Partnering Program - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<iframe src='https://docs.google.com/forms/d/1F1CSv8oYbr8sQc7lC9wP07qIxYJq6dfcK-7LocznRBc/formResponse' height='1080px' width='100%' style='border: none;'></iframe>");
			fileData = fileData.replace("##index_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Recipe Sharing, Partnering Program, Collaborate");
			fileData = fileData.replaceAll("##DESC_DATA##", "Collaborate with Spicy World by Recipe Sharing & Partnering Program.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/collaborate.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", "");
			saveFile(basePath + "collaborate.html", fileData);
			
			
			//Save Public Presence Page
			fileData = readFile(basePath + "template/template.html");
			uaData = readFile(basePath + "template/public-presence.txt");
			fileData = fileData.replace("##TITLE_DATA##", "Public Presence - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", uaData);
			fileData = fileData.replace("##index_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Public Presence, Relation, Market Presence");
			fileData = fileData.replaceAll("##DESC_DATA##", "Public Presence, Relation, Market Presence of Spicy World.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/public-presence.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", "");
			saveFile(basePath + "public-presence.html", fileData);
			
			//Save Join Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Join US - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div id='subscribeMsg'><div id='loadingIcon'><img src=\"images/loading.gif\" nopin='nopin'/></div><div id='subscribeTxt' class='searchTxt'></div>");
			fileData = fileData.replace("##index_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Email Subscribe");
			fileData = fileData.replaceAll("##DESC_DATA##", "Email Subscribe of Spicy World.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/join.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "subscribeUnsubscribeEmail();");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", "");
			saveFile(basePath + "join.html", fileData);
			
			
			//Save Sign In / Register Page
			fileData = readFile(basePath + "template/template.html");
			uaData = readFile(basePath + "template/signin.txt");
			String whatToData = readFile(basePath + "template/what-to-cook.txt");
			fileData = fileData.replace("##TITLE_DATA##", "Membership - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", uaData.replace("##WHAT_TO_DATA##", whatToData));
			fileData = fileData.replace("##cook_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Membership, Cook Book, PDF, Bookmarks, Account Settings, Promotions, Cooking Companion, Sign In, Register, Newsletters, Android App Download");
			fileData = fileData.replaceAll("##DESC_DATA##", "Sign In or Register and become a member of Spicy World where you can download our Cook Book, Bookmark our recipes, find new Promotions and use our Cooking Companion, subscribe to our Newsletters and Download our Android App. We will randomly choose one of you to send out some cool give aways from Spicy World.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/signin.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "loginStatus();");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", "");
			saveFile(basePath + "signin.html", fileData);
			
			//Save Sign In / Register Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Free Online Games - Spicy World Simple and Easy Recipes by Arpita");
			fileData = fileData.replace("##MIDDLE_DATA##", "<iframe src='template/ticktac.html' frameborder='0' style='overflow: hidden; height: 700px; width: 95%; ' height='700px' width='95%'></iframe>");
			fileData = fileData.replace("##index_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Tic Tac Toe, Game, Online, Time pass");
			fileData = fileData.replaceAll("##DESC_DATA##", "Play free online tic tac toe game.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/games.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
			fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", "");
			saveFile(basePath + "games.html", fileData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		MobileSiteBuilder.buildNo = buildNo;
		MobileSiteBuilder.mobileSiteBuilder();
		//CookBook.main(null);
		
	}
	
	private static void searchReadyJSON (String templatePath, String jarBase) {
		String xml = templatePath + "template/data.xml";
		try {
			JSONObject xmlJSONObj = XML.toJSONObject(readFile(xml));
	        String jsonPrettyPrintString = xmlJSONObj.toString(4);
	        saveFile(templatePath + "template/search.js", jsonPrettyPrintString.replace("{\"root\": {\"element\":", "var allRecipes = ").replace("]}}", "];"));
	        String compressString = "java -jar " + jarBase + "/yuicompressor-2.4.8.jar " + templatePath  + "template/search.js -o " + templatePath + "js/searchdata.js --charset utf-8";
	        Runtime rt = Runtime.getRuntime();
	        Process pr = rt.exec(compressString);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	private static void generateTagHTML(String tag, String templatePath, NodeList nList, String baseTemplatePath, int count, String tagDataStr, String h1Tag) {
		String recipes_data = "<div class='recipePage'>", tagData = null;
		int recordCount = 0;
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
						recipes_data = recepiData(recipes_data, eElement, "", recordCount);
						recordCount++;
					}
				}
			}
		} catch (Exception e) {}
		if (recordCount % 2 == 1) {
			recipes_data += "</div>";
		}
		recipes_data += "</div>";
		
		//Save Tag Page
		String fileData = readFile(templatePath);
		fileData = fileData.replace("##TITLE_DATA##", "Posts on " + tagDataStr.toUpperCase() + " - Spicy World Simple and Easy Recipes by Arpita");
		fileData = fileData.replace("##MIDDLE_DATA##", h1Tag + recipes_data);
		fileData = fileData.replace("##tags_sel##", "selected");
		fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
		fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
		fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
		fileData = fileData.replaceAll("##KEYWORD_DATA##", "Contents, Categories, Posts on " + tag + ", " + tagData.toUpperCase());
		fileData = fileData.replaceAll("##DESC_DATA##", "Posts on " + tag + " and also recipes related to other categories " + tagData.toUpperCase() + " in Spicy World.");
		fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
		fileData = fileData.replaceAll("##URL_DATA##", "/" + tag + "-tag.html");
		fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
		fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
		fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
		saveFile(baseTemplatePath + tag + "-tag.html", fileData);
	}
	
	public static String getPagination(int currentPage, int totalPage, String siteMapData) {
		String data = "", pageURL = "", pageTitle = "";
		int fnum = 0, snum = 0, tnum = 0;
		boolean nextDots = false, prevDots = false;
		if (currentPage == 1) {
			data += "<span class=\"selected sdisabled\">&lt;</span>";
		} else {
			if (currentPage == 2) {
				pageURL = "recipes.html";
				pageTitle = "My Recipes - Page 1";
			} else {
				pageURL = "recipes-" + (currentPage-2) + ".html";
				pageTitle = "My Recipes - Page " + (currentPage - 2);
			}
			data += "<span><a class=\"recipes-pagination-prev\" title=\"" + pageTitle + "\" href='" + pageURL + "'>&lt;</a></span>";
		}
		
		if (currentPage == 1 || currentPage == 2) {
			fnum = 1;
			snum = 2; 
			tnum = 3;
			nextDots = true;
		} else if (currentPage > 2 && currentPage < (totalPage - 1)) {
			fnum = currentPage - 1;
			snum = currentPage; 
			tnum = currentPage + 1;
			nextDots = true;
			prevDots = true;
		} else if (totalPage == currentPage || (totalPage - 1) == currentPage) {
			fnum = totalPage - 2;
			snum = totalPage - 1; 
			tnum = totalPage;
			nextDots = false;
			prevDots = true;
		}
		if (currentPage == 3) {
			prevDots = false;
		}
		if (currentPage + 2 == totalPage) {
			nextDots = false;
		}
		
		for (int i=1; i<=totalPage; i++) {
			if (i == totalPage && nextDots) {
				data += "<span>...</span>";
			}
			if (i == fnum || i == snum || i == tnum || i == 1 || i == totalPage) {
				if (i == currentPage) {
					data += "<span class=\"selected\">" + i + "</span>";
				} else {
					if (i == 1) {
						pageURL = "recipes.html";
						pageTitle = "My Recipes - Page 1";
					} else {
						pageURL = "recipes-" + (i-1) + ".html";
						pageTitle = "My Recipes - Page " + i;
					}
					data += "<span><a class=\"recipes-pagination\" title=\"" + pageTitle + "\" href='" + pageURL + "'>" + i + "</a></span>";
				}
			}
			if (i == 1 && prevDots) {
				data += "<span>...</span>";
			}
		}
		if (currentPage == totalPage) {
			data += "<span class=\"selected sdisabled\">&gt;</span>";
		} else {
			pageURL = "recipes-" + (currentPage) + ".html";
			pageTitle = "My Recipes - Page " + (currentPage);
			data += "<span><a class=\"recipes-pagination-next\" title=\"" + pageTitle + "\" href='" + pageURL + "'>&gt;</a></span>";
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
		String addTitle = title;
		try {
			addTitle = addTitle + " / " + eElement.getElementsByTagName("add-title").item(0).getTextContent();
		} catch (Exception e) {}
		try {
			recipes_data += "<div class='imagesPage'><div><a class=\"additional-recipes-link\" href='" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html' title=\"" + addTitle 
					+ "\">"
					//+ "<img src='" + eElement.getElementsByTagName("pic").item(0).getTextContent() + imgBuildNo + "' alt=\"" + addTitle + "\" title=\"" + addTitle + "\" style=\"width: 280px;\"/>"
					+ "<div style='background-image: url(" + eElement.getElementsByTagName("pic").item(0).getTextContent().replace(".jpg", "-ico.jpg") + imgBuildNo + ");' class='addImgDiv' title=\"" + addTitle + "\">&nbsp;</div>"
							+ "</a></div><div class=\"additionalRecipeCaption\"><a class=\"additional-recipes-link\" title=\"" + addTitle + "\" href='" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html'>" + title + "</a></div></div>";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recipes_data;
	}
	
	public static String getAllImages(String recipes_data, Element eElement) {
		String title = eElement.getElementsByTagName("title").item(0).getTextContent();
		try {
			title = title + " / " + eElement.getElementsByTagName("add-title").item(0).getTextContent();
		} catch (Exception e) {}
		String picURL = eElement.getElementsByTagName("pic").item(0).getTextContent() + imgBuildNo;
		String picClass = "";
		try {
			picURL = eElement.getElementsByTagName("youtube-url").item(0).getTextContent();
			picClass = " youtube-video";
		} catch (Exception e) {}
		
		recipes_data += "<div class='imagesPage'><div class=\"img-hover-zoom\"><a class='group1 all-images-img" + picClass + "' href='" + picURL + "' title=\"" + title 
				+ "\"><img alt=\"" + eElement.getElementsByTagName("title").item(0).getTextContent() + "\" class='allImageImage' src=\""
				+ eElement.getElementsByTagName("pic").item(0).getTextContent() + imgBuildNo
				+ "\"/></a></div><div class='allImageImageTitle'><a class='all-images-link' title=\"" + title + "\" href=\"" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html\">" + eElement.getElementsByTagName("title").item(0).getTextContent() + "</a></div></div>";
		return recipes_data;
	}
	
	public static String staticEntriesSiteMap() {
		String data = "";
		data += "<url><loc>http://spicyworld.in</loc></url>"
				+ "<url><loc>http://spicyworld.in/feedback.html</loc></url>"
				+ "<url>"
					+ "<loc>http://spicyworld.in/about-me.html</loc>"
					+ "<image:image>"
						+ "<image:loc>http://spicyworld.in/images/about.jpg</image:loc>"
						+ "<image:caption>Arpita from Spicy World</image:caption>"
					+ "</image:image>"
				+ "</url>"
				/*
				+ "<url>"
					+ "<loc>http://spicyworld.in/about-amitava.html</loc>"
					+ "<image:image>"
						+ "<image:loc>http://spicyworld.in/images/profile-amitava.jpg</image:loc>"
						+ "<image:caption>Amitava Ghosh, Editor and Web Developer of Spicy World</image:caption>"
					+ "</image:image>"
				+ "</url>"
				*/
					
				+ "<url><loc>http://spicyworld.in/rss.xml</loc></url>"
				+ "<url><loc>http://spicyworld.in/Spicy-World-Cook-Book.pdf</loc></url>"
				+ "<url><loc>http://spicyworld.in/sitemap.xml</loc></url>"
				+ "<url><loc>http://spicyworld.in/all-food-images.html</loc></url>"
				+ "<url><loc>http://spicyworld.in/food-plating.html</loc></url>"
				//+ "<url><loc>http://spicyworld.in/spicyworld.apk</loc></url>"
				+ "<url><loc>http://spicyworld.in/public-presence.html</loc></url>"
				+ "<url><loc>http://spicyworld.in/tags.html</loc></url>"
				+ "<url><loc>http://spicyworld.in/signin.html</loc></url>"
				+ "<url><loc>http://spicyworld.in/games.html</loc></url>"
				// App links
				+ "<url><loc>http://spicyworld.in/our-apps.html</loc></url>"
				+ "<url>"
					+ "<loc>http://spicyworld.in/collaborate.html</loc>"
				+ "</url>";
		return data;
	}

	public static String populateRSSData(Element eElement) {
		String homeJSON = null;
		String url = eElement.getElementsByTagName("url").item(0).getTextContent();
		String title = eElement.getElementsByTagName("title").item(0).getTextContent();
		try {
			String addTitle = eElement.getElementsByTagName("add-title").item(0).getTextContent();
			title = title + " / " + addTitle;
		} catch (Exception e) {}
		String pubDate = null;
		try {
			pubDate = "<pubDate>" + getDate(eElement.getElementsByTagName("pubDate").item(0).getTextContent()) + "</pubDate>";
		} catch (Exception e) {
			pubDate = "";
		}
		homeJSON = "<item><guid>http://www.spicyworld.in/" + url + ".html</guid><author>contact@spicyworld.in (Spicy World by Arpita)</author>" + pubDate
				+ "<title>" + title + "</title><link>http://www.spicyworld.in/" + url
		+ ".html</link><description><![CDATA[<img src='http://spicyworld.in/" + eElement.getElementsByTagName("pic").item(0).getTextContent() + "' />" + eElement.getElementsByTagName("shortDesc").item(0).getTextContent().replace("class='topImagePosition descCSSStyle' ", "") + "]]></description></item>";
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
		String title = eElement.getElementsByTagName("title").item(0).getTextContent();
		try {
			title = title + " / " + eElement.getElementsByTagName("add-title").item(0).getTextContent();
		} catch (Exception e) {}
		siteMapDataEntry = "<url>"
				+ "<loc>http://spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html</loc>"
				+ getSitemapImageTag(title, eElement, "pic")
				+ getSitemapImageTag(title + " - how you serve", eElement, "add-pic")
				+ getSitemapImageTag(title + " - how you plate this", eElement, "add-pic-top")
				+ getSitemapImageTag(title + " with Garnishment", eElement, "end-pic")
				+ getProcessImages(eElement.getElementsByTagName("process").item(0).getTextContent(), title)
			+ "</url>";
		return siteMapDataEntry;
	}
	
	private static String getProcessImages(String str, String title) {
		String data = "";
		int i = 1;
		Pattern p = Pattern.compile("recipeimages/(.*?).jpg");
	    Matcher m = p.matcher(str);
	    while (m.find()) {
	        data += "<image:image>"
					+ "<image:loc>http://spicyworld.in/" + m.group() + "</image:loc>"
					+ "<image:caption>Related to " + title.replace("&", "and") + "</image:caption>"
				+ "</image:image>";
	        i++;
	    }
		return data;
	}
	
	private static String getSitemapImageTag(String title, Element eElement, String node) {
		String data = "";
		try {
			data = "<image:image>"
					+ "<image:loc>http://spicyworld.in/" + eElement.getElementsByTagName(node).item(0).getTextContent() + "</image:loc>"
					+ "<image:caption>" + title.replace("&", "and") + "</image:caption>"
				+ "</image:image>";
		} catch (Exception e) {
			data = "";
		}
		return data;
	}
	
	public static void createItemData(String templatePath, Element eElement, int count, Element nextElement, Element prevElement, String relatedRecipes, String htmlTags) {
		String out = "";
		String youtubeLink = "";
		String type = eElement.getElementsByTagName("type").item(0)
				.getTextContent();
		String blogType = "rb";
		try {
			blogType = eElement.getElementsByTagName("blog-type").item(0).getTextContent();
		} catch (Exception e) {
			blogType = "rb";
		}
		String itemType = "";
		if ("nonVegItem".equals(type)) {
			itemType = "Non-Vegetarian";
		} else {
			itemType = "Vegetarian";
		}
		String title = eElement.getElementsByTagName("title").item(0).getTextContent();
		try {
			String addtitle = eElement.getElementsByTagName("add-title").item(0).getTextContent();
			title = title + " / " + addtitle;
		} catch (Exception ee) {}
		String desc = eElement.getElementsByTagName("shortDesc").item(0).getTextContent();
		String url = eElement.getElementsByTagName("url").item(0).getTextContent();
		String additionalImg = "";
		try  {
			additionalImg = eElement.getElementsByTagName("add-pic").item(0).getTextContent();
			additionalImg = "<div class='div3Pos posLeft'><br/><a class=\"group1 image-link-click-top\" title=\"" + title + "\" href=\"" + additionalImg + imgBuildNo + "\"><img style=\"####\" alt=\"" + title 
			+ "\" title=\"" + title + "\" src='' data-src='"
			+ additionalImg + imgBuildNo
			+ "' class='topImagePosition lazy-load-img' /></a><br/></div>";
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
		
		String additionalImgTop = "";
		try  {
			additionalImgTop = eElement.getElementsByTagName("add-pic-top").item(0).getTextContent();
			additionalImgTop = "<div class='div3Pos posLeft'><br/><a class=\"group1 image-link-click-top\" title=\"" + title + "\" href=\"" + additionalImgTop + imgBuildNo + "\"><img style=\"####\" alt=\"" + title 
			+ "\" title=\"" + title + "\" src='' data-src='"
			+ additionalImgTop + imgBuildNo
			+ "' class='topImagePosition lazy-load-img' /></a><br/><br/></div>";
			String addImgStyle = "";
			try {
				addImgStyle = eElement.getElementsByTagName("add-pic-top-style").item(0).getTextContent();
				additionalImgTop = additionalImgTop.replace("####", addImgStyle);
			} catch (Exception e) {
				additionalImgTop = additionalImg.replace("####", "");
			}
			if (additionalImg != "") {
				additionalImg += "<br/>" + additionalImgTop;
			} else {
				additionalImg = additionalImgTop;
			}
		} catch (Exception e) {
			additionalImgTop = "";
		}
		
		String endImg = "";
		String endImgStyle = "";
		try  {
			endImg = eElement.getElementsByTagName("end-pic").item(0).getTextContent();
			endImg = "<div class='div3Pos posLeft'><br/><a class=\"group1 image-link-click-btm\" title=\"" + title + " (Final)\" href=\"" + endImg + imgBuildNo + "\"><img style=\"##_##\" alt=\"" + title 
			+ "\" title=\"" + title + "\" src='' data-src='"
			+ endImg + imgBuildNo
			+ "' class='topImagePosition lazy-load-img' /></a><br/><br/></div>";
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
			steps = "<div class='steps-image' #@#@#>" + steps.replace("<div><div>", "<div class='header-style'>").replace("</div></div>", "</div>").replace("<img ", "<img class=\"lazy-load-img\" alt=\"Cooking Step: " + title + "\" title=\"Cooking Step: " + title + "\" ").replace("src", "src=\"\" data-src") + "</div>";
		} else {
			steps = "<div class='steps-image'>" + steps.replace("<div><div>", "<div class='header-style'>").replace("</div></div>", "</div>") + "</div>";
		}
		
		String datepub = getDateView(eElement.getElementsByTagName("pubDate").item(0).getTextContent());
		
		String pubType = "datePublished";
		String author = "Arpita";
		if ("rb".equals(blogType)) {
			out = "<div itemscope itemtype=\"http://schema.org/Recipe\"><div class='h2Class'><div style=\"clear:both\"><h1 itemprop='name' id='title'>";
			pubType = "datePublished";
		} else {
			out = "<div itemscope itemtype=\"http://schema.org/LiveBlogPosting\"><div class='h2Class'><div itemprop='name' style=\"clear:both\"><h1 id='title'>";
			type = "copynonBlogItem";
			pubType = "datePublished";
			author = "Amitava & Arpita";
		}
		String youtubeIFRAME = "";
		try {
			youtubeLink = eElement.getElementsByTagName("youtube-url").item(0).getTextContent();
			youtubeLink = youtubeLink.substring(youtubeLink.indexOf("v=") + 2, youtubeLink.length());
			youtubeIFRAME = "<div class='youtubeVideoTop'><iframe src=\"https://www.youtube.com/embed/" + youtubeLink + "?enablejsapi=1&amp;playsinline=1\" frameborder=\"0\" allowfullscreen=\"\" class='topYoutube'></iframe></div>";
		} catch (Exception e) {
			youtubeIFRAME = "";
		}
		
		String ratingWidget = "<div id=\"wpac-rating\"></div><script type=\"text/javascript\">wpac_init = window.wpac_init || [];wpac_init.push({widget: 'Rating', id: 11133});(function() { if ('WIDGETPACK_LOADED' in window) return; WIDGETPACK_LOADED = true; var mc = document.createElement('script'); mc.type = 'text/javascript'; mc.async = true; mc.src = 'https://embed.widgetpack.com/widget.js'; var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(mc, s.nextSibling);})();</script><a href=\"https://widgetpack.com\" class=\"wpac-cr\">Star Rating WIDGET PACK</a>";
		
		String keywords = "";
		try {
			keywords = eElement.getElementsByTagName("keywords").item(0).getTextContent();
			keywords = "<meta itemprop=\"keywords\" content=\"" + keywords + "\">";
		} catch (Exception e) {}
		
			out += title
				+ "</h1></div>" + keywords
					+ "<div class='copyright copy" + type + "'>"
						+ "By <span itemprop='author'>" + author + "</span>&nbsp;<i>&copy; 2016 Spicy World</i><br/><i><span itemprop='" + pubType + "' content='" + datepub + "'>" + datepub + "</span></i><span id='viewsCount'></span>"
					+ "</div>"
				+ "<div id='loggedInFeatures'><div id='rating'>" + ratingWidget + "</div></div>"
				//AMITAVA NOT NEEDED //+ "<div id='messagePopupBkm'><p><b>Login</b>Email ID (Login ID)<br/><input type='text' id='uid' onkeypress='enterPressCheckSigninPopup(event)'/><br/>Password<br/><input onkeypress='enterPressCheckSigninPopup(event)' id='pwd' type='password'/><br/><label id='signInError'></label><a href='javascript:loginFromPopup()' class='btn'>&nbsp;Sign In</a><span>&nbsp;</span>Not a member? Create your free account by visiting <a href='signin.html'>Membership</a> tab.</p><div><a href='javascript:closePopupMessageBM()'>X</a></div></div>"
				+ "<div class='socialAndOtherButtons'>"				
				
				// Comment Button Starts
				//+ "<div class='commentButton'><a title='Add Comment' onclick=\"ga('send', 'event', 'Comment', 'Comment: BTN_" + url + "', this.href);\" href='#comments'>Comment</a></div>"
				// Comment Button Ends
				
				// Group Button Starts
				//+ "<div class='commentButton'><a onclick=\"ga('send', 'event', 'Group', 'Group: BTN_" + url +"', this.href);\" title='Join Facebook Group' href='https://www.facebook.com/groups/975021199218034/'>Facebook Group</a></div>"
				// Group Button Ends
				
				// Print Button Starts
				//+ "<div class='commentButton commentButtonPrint'><a onclick=\"ga('send', 'event', 'Print', 'Print: BTN_" + url +"', this.href);\" title='Print Recipe' href='javascript:window.print()'>Print</a></div>"
				// Print Button Ends
				
				// Youtube Subscribe Starts
				+ "<div class=\"recipeSocialLinks padleft0px\"><script src=\"https://apis.google.com/js/platform.js\"></script><div class=\"g-ytsubscribe\" data-channelid=\"UC9nZ7WurQoi0LJPSyKXS7NQ\" data-layout=\"default\" data-count=\"default\"></div></div>"
				// Youtube Subscribe Ends
				
				// Pinterest Starts
				+ "<div class=\"recipeSocialLinks\"><a href=\"//www.pinterest.com/pin/create/button/\" data-pin-do=\"buttonBookmark\"  data-pin-height=\"26\" data-pin-color=\"red\"><img alt='Pinterest Share' src=\"//assets.pinterest.com/images/pidgets/pinit_fg_en_rect_red_20.png\" /></a><script type=\"text/javascript\" async defer src=\"//assets.pinterest.com/js/pinit.js\"></script>"	 
				+ "</div>"
				// Pinterest Ends
				
				// Stumble Upon Button Starts
				//+ "<div style=\"float:left;height:10px;padding-left:10px;\">"
				//+ "<su:badge layout=\"4\"></su:badge><script type=\"text/javascript\">  (function() {    var li = document.createElement('script'); li.type = 'text/javascript'; li.async = true;    li.src = ('https:' == document.location.protocol ? 'https:' : 'http:') + '//platform.stumbleupon.com/1/widgets.js';    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(li, s);  })();</script>"
				//+ "</div>"
				// Stumble Upon Button Ends
				
				// Twitter Starts
				//+ "<div class=\"recipeSocialLinks\" style=\"height:10px;\">"
				//+ "<a href='https://twitter.com/share' class='twitter-share-button' data-url='http://spicyworld.in/" + url + "' data-text='" + title + "' data-via='spicy_world' data-hashtags='spicyworldrecipes'>&nbsp;</a>"
				//+ "<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document, 'script', 'twitter-wjs');</script>"
				//+ "</div>"
				// Twitter Ends
				
				// Email Starts
				//+ "<div style=\"float:left;padding-left:10px;\"><a title='Send/Share via Email' "
				//+ "href=\"mailto:?subject=" + title + " Recipe at Spicy World&body=" + desc + "\n Visit Spicy World (http://spicyworld.in/" + url + ".html?emailFlag=Y) for detailed recipe.\">"
				//+ "<img style='height:21px;' src='images/email.png' title='Send/Share via Email' title='Send/Share via Email'/></a></div>" 
				// Email Ends
				
				// Linked In Starts
				//+ "<div style=\"float:left;padding-left:10px;\">"
				//+ "<script src=\"//platform.linkedin.com/in.js\" type=\"text/javascript\"> lang: en_US</script><script type=\"IN/Share\" data-url=\"http://spicyworld.in/" + url + ".html\" data-counter=\"right\"></script>"
				//+ "</div>" 
				// Linked In Ends
				
				// FB Starts
				//+ "<div style=\"float:left;padding-left:10px;\"><div class=\"fb-send\" data-href=\"http://spicyworld.in/" + url + ".html\"></div></div>"
				+ "<div class=\"recipeSocialLinks\"><div class=\"fb-like\" data-href=\"http://spicyworld.in/"
				+ url
				+ ".html\" data-layout=\"button_count\" data-action=\"like\" data-size=\"small\" data-show-faces=\"true\" data-share=\"true\"></div></div>" 
				// FB Ends
				
				+ "</div>"
				+ "<p class=\"descp\" id='description' itemprop='description' style=\"padding-top:8px;clear:both\">" + desc + "</p>" + youtubeIFRAME + "</div><br/>"
				+ "<div>"
				+ "<div class='div3Pos posLeft'>";
				String hideImage = "";
				if (!Utility.isNullOrEmpty(youtubeIFRAME)) {
					hideImage = " hidden";
				}
				out += "<a class=\"group1 image-link-click-top-main " + hideImage + "\" title=\"" + title + "\" href=\"" + eElement.getElementsByTagName("pic").item(0).getTextContent() + imgBuildNo + "\">"
						+ "<img itemprop='image' src='" + eElement.getElementsByTagName("pic").item(0).getTextContent() + imgBuildNo  + "' alt=\"" + title  + "\" title=\"" + title + "\" class='topImagePosition' /></a>";
				if (Utility.isNullOrEmpty(youtubeIFRAME)) {
					out += "<br/><br/>";
				}
				if ("rb".equals(blogType)) {
					out += "<div><h2 id='ingredients'>Ingredients</h2></div><div itemprop='ingredients'>"
					+ eElement.getElementsByTagName("ingrediants").item(0)
							.getTextContent().replace("><div>", " class='internalLI'>").replace("</div>", "").replace("<li", "<li itemprop=\"recipeIngredient\"")
					+ "</div></div>" + additionalImg + "<div class='div3Pos'><div><h2 id='steps'>Steps</h2></div><div itemprop='recipeInstructions'>"
					+ steps.replace(".jpg", ".jpg" + imgBuildNo).replace(" #@#@#", "")
					+ "</div><br/>";
				} else {
					out += "<div class='div3Pos'><div itemprop='liveBlogUpdate' typeof='BlogPosting' class='simple-blog'>"
							+ "<time class='hide' itemprop='datePublished' content='" + datepub + "T00:00:00-00:00'>" + datepub + "</time>"
							+ steps.replace(".jpg", ".jpg" + imgBuildNo).replace("#@#@#", "itemprop='articleBody'")
							+ "</div><br/>";
				}
				if ("rb".equals(blogType)) {
					String recipeCategory = "";
					String recipeCuisine = "";
					String prepTime = "";
					String cookTime = "";
					String servCount = "";
					boolean addSection = false;
					try {
						recipeCategory = eElement.getElementsByTagName("recipecategory").item(0).getTextContent();
						addSection = true;
					} catch (Exception e) {}
					try {
						recipeCuisine = eElement.getElementsByTagName("recipecuisine").item(0).getTextContent();
						addSection = true;
					} catch (Exception e) {}
					try {
						prepTime = eElement.getElementsByTagName("preptime").item(0).getTextContent();
						addSection = true;
					} catch (Exception e) {}
					try {
						cookTime = eElement.getElementsByTagName("cooktime").item(0).getTextContent();
						addSection = true;
					} catch (Exception e) {}
					try {
						servCount = eElement.getElementsByTagName("servcount").item(0).getTextContent();
						addSection = true;
					} catch (Exception e) {}
					
					if (addSection) {
						out += "<div><div><h2 id=\"additional-info\">Additional Info</h2></div>"
								+ "<div class=\"steps-image\">"
								+ "<div itemprop=\"recipeCategory\" content=\"" + recipeCategory + "\">Recipe Category: " + recipeCategory + "</div>"
								+ "<div itemprop=\"recipeCuisine\" content=\"" + recipeCuisine + "\">Recipe Cuisine: " + recipeCuisine + "</div>"
								+ "<div itemprop=\"prepTime\" content=\"PT" + prepTime + "M\">Preparation Time: " + prepTime + " minutes</div>"
								+ "<div itemprop=\"cookTime\" content=\"PT" + prepTime + "M\">Cooking Time: " + cookTime + " minutes</div>"
								+ "<div itemprop=\"recipeYield\" content=\"" + servCount + "\">Serves: " + servCount + "</div>"
										+ "</div></div><br/>";
					}
					
					out += "<div class='complete'>"
					+ eElement.getElementsByTagName("completionStatement").item(0)
							.getTextContent()
					+ "</div>"
					+ "<div class='garnishment'><ul><li>"
					+ eElement.getElementsByTagName("garnishment").item(0)
							.getTextContent() + "</li></ul></div></div>" + "</div></div>" + endImg;
				}
		

		
		String oldNew = nextPreviousPagination(prevElement, nextElement, "");
		try {
			String tagData = eElement.getElementsByTagName("tags").item(0).getTextContent();
			String tags = "<div id='tags' class=\"tagContent\">";
			if (tagData!=null) {
				String ss[] = tagData.split(",");
				for (int i=0; i<ss.length; i++) {
					String tg = ss[i];
					tags += "<span><a class=\"tag-link\" href=\"" + tg.replace(" ", "-") + "-tag.html\">" + tg + "</a></span>";
				}
			}
			tags += "</div>";
			out += tags;
		} catch (Exception e) {} 
		// Print only note
		out += "<div class='printOnlyRecipeNote'><br/>All Images and Recipes are copyrighted to Spicy World.<br/>For more recipes visit us at http://spicyworld.in</div>";
		
		if (relatedRecipes!=null && relatedRecipes.length()>0) {
			relatedRecipes = "<br/><div id='relatedRecipes' class='relatedRecipes'>You may also like</div><div class='clear relatedRecipesData'>" + relatedRecipes + "</div>";
		}
		
		/*try {
			if (!Utility.isNullOrEmpty(youtubeLink)) {
				youtubeLink = youtubeLink.substring(youtubeLink.indexOf("v=") + 2, youtubeLink.length());
				out += "<div class='youtubeLink'><iframe src=\"https://www.youtube.com/embed/" + youtubeLink + "\" frameborder=\"0\" allowfullscreen=\"\"></iframe></div>";
			}
		} catch (Exception e) {}*/
		
		
		try {
			String recoLinks = eElement.getElementsByTagName("recoLinks").item(0).getTextContent();
			if (!Utility.isNullOrEmpty(recoLinks)) {
				out += "<div class='recoLinks'><div class='commentHeader'>Recommended Links</div><div class='recoLinksBot'>" + recoLinks.replace("<a ", "<a class=\"affl-link\" rel='nofollow' target='_blank' ") + "</div></div>";
			}
		} catch (Exception e) {}
		
		
		out += "<div class='botNextPrev' id='botNextPrev_bot'>" + oldNew + "</div>";
		String additionalAdds = readFile(basePath + "template/recipe-ads.html");
		// For comment section
		out += "<br/><div class='addBottomRecipePage'><div id='commentSection'><div id='comments' class='commentHeader'>Leave Your Comments</div><br/>"
		// For Disqus
		//+ "<div class='disqus_thread_class'><div id=\"disqus_thread\"></div><script type=\"text/javascript\"> var disqus_shortname = 'spicyworld';  (function() {var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true; dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js';(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);  })();</script></div>"
		+ "<div class=\"fb-comments\" data-href=\"http://spicyworld.in/" + url + ".html\" data-width=\"100%\" data-numposts=\"8\"></div></div>"
		+ "<br/>" +relatedRecipes
		+ "<br/><br/>" + additionalAdds + "<br/><br/>"
		// Page loading increase - AMITAVA
		//+ "<div id='relatedRecipesTags' class='relatedRecipes'>Categories</div><div class='bottomLinksTags'>" + htmlTags.replace("href=", "onclick=\"ga('send', 'event', 'Tag Click', 'Tag Click: recipe_bottom', this.href);\" href=") + "</div>"
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
		
		
		fileData = fileData.replace("##TITLE_DATA##", title + " - Spicy World Simple and Easy Recipes by Arpita");
		fileData = fileData.replace("##MIDDLE_DATA##", oldNew + "<div class='recipeDataPage'>" + out + "</div><div class=\"clear\">&nbsp;</div>");
		fileData = fileData.replace("##recipes_sel##", "selected");
		fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
		fileData = fileData.replaceAll("##KEYWORD_DATA##", keyword.trim());
		fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
		fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
		fileData = fileData.replaceAll("##DESC_DATA##", Utility.html2text(desc).replace("\"", "'"));
		fileData = fileData.replaceAll("##IMG_DATA##", eElement.getElementsByTagName("pic").item(0).getTextContent());
		fileData = fileData.replaceAll("##URL_DATA##", "/" + url + ".html");
		fileData = fileData.replaceAll("##PINTEREST_INCLUDE##", pinterestData);
		fileData = fileData.replaceAll("##ONLOAD_CALL##", "calculateViews('" + url + ".html" + "');initFancy();");
		fileData = fileData.replaceAll("##HEADER_GA##", headerGAData);
		saveFile(templatePath + url + ".html", fileData);
	}

	public static String recepiData(String recipes_data, Element eElement, String prefix, int recordCount) {
		String type = eElement.getElementsByTagName("type").item(0)
				.getTextContent();
		String text = "";
		if ("nonVegItem".equals(type)) {
			text = "Nonveg Recipe";
		} else {
			text = "Veg Recipe";
		}
		
		String title = eElement.getElementsByTagName("title").item(0).getTextContent();
		try {
			title = title + " / " + eElement.getElementsByTagName("add-title").item(0).getTextContent();
		} catch (Exception e) {}
		String data = eElement.getElementsByTagName("shortDesc").item(0).getTextContent();
		data = Utility.html2text(data);
		if (data.length() > 400) {
			data = data.substring(0, 400) + " ...";
		}
		String oddEvelClass = "";
		if (recordCount % 2 == 1) {
			oddEvelClass = " rightClass";
		} else {
			recipes_data += "<div class='landingRow'>";
		}
		try {
			String blogType = eElement.getElementsByTagName("blog-type").item(0).getTextContent();
			if ("sb".equals(blogType)) {
				type = "featuredBlog";
				text = "Featured Post";
			}
		} catch (Exception e) {
			
		}
		
		String tagData = eElement.getElementsByTagName("tags").item(0).getTextContent();
		String tagHTML = getTagHTML(tagData);
		String datepub = getDateView(eElement.getElementsByTagName("pubDate").item(0).getTextContent());
		
		String youTubeLink = "";
		String videoID = "";
		try {
			youTubeLink = eElement.getElementsByTagName("youtube-url").item(0).getTextContent();
			videoID = youTubeLink.substring(youTubeLink.indexOf("v=") + 2, youTubeLink.length());
			youTubeLink = "<a class='readMore marginleft10px recipes-list-youtube-video' href=\"" + youTubeLink + "\" title=\"Youtube Video for " + title + "\" target='_blank'>Youtube Video >></a>";
		} catch (Exception e) {}
		
		recipes_data += "<div class='recipeListPageItem" + oddEvelClass + "'>"
				+ "<div class='recipeListPageItemLeft'><a href='" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html'>"
				+ "<img title=\"" + title 
				+ "\" alt=\"" + title + "\" src=\""
				+ prefix + eElement.getElementsByTagName("pic").item(0)
						.getTextContent() + imgBuildNo
				+ "\" /></a><div class='" + type + " itemTypeLabel'>" + text + "</div></div>"
				+ "<div class='recipeListPageItemRight'>"
				+ "<div class=\"title\">"
				+ "<a title=\"" + title + "\" class='noStyle recipes-list-link' href=\""
				+ eElement.getElementsByTagName("url").item(0).getTextContent()
				+ ".html\">" + title
				+ "</a></div><div class='descHomeDate'>" + datepub + "</div>"
				+ "<div class=\"desc\">"
				+ data + "</div>"
				+ "<div class='linkReadMoreDiv'><a class='readMore recipes-list-read-more' title=\"" + title + "\" href=\"" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html\">Read More >></a>" + youTubeLink + "</div>"
				+ tagHTML
				+ "</div>"
				+ "</div>";
		if (oddEvelClass != "") {
			recipes_data += "</div>";
		}
		return recipes_data;
	}
	
	public static String readFile(String fileName, String menuTagsData) {
		recipeMenuData = menuTagsData;
		return readFile(fileName);
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
		return fileData.replaceAll("(?s)<!--.*?-->", "");
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
			//String applyHidden = "desktop-add-" + Utility.getRandomNumberInRange(1, 3);
			File newTextFile = new File(outPath);
			FileWriter fw = new FileWriter(newTextFile);
			//fw.write(fileData.replace(applyHidden, applyHidden + " hidden"));
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
	
	public static String nextPreviousPagination (Element prevElement, Element nextElement, String deviceType) {
		//Next Previous Link Starts
		String prev = "", next = "";
		if (prevElement != null) {
			String prevTitle = prevElement.getElementsByTagName("title").item(0).getTextContent();
			try { 
				prevTitle = prevElement.getElementsByTagName("title").item(0).getTextContent() + " / " + prevElement.getElementsByTagName("add-title").item(0).getTextContent();
			} catch (Exception e) {}
			prevTitle = "Next Post: " + prevTitle;
			prev = "<a title=\"" + prevTitle + "\" class='prevLink prev-recipe-link' href='" + prevElement.getElementsByTagName("url").item(0).getTextContent() + ".html'>&nbsp;Previous Post</a>";
		} else {
			prev = "<span class='prevLinkD'>&nbsp;Previous Post</span>";
		}
		if (nextElement != null) {
			String nextTitle = nextElement.getElementsByTagName("title").item(0).getTextContent();
			try { 
				nextTitle = nextElement.getElementsByTagName("title").item(0).getTextContent() + " / " + nextElement.getElementsByTagName("add-title").item(0).getTextContent();
			} catch (Exception e) {}
			nextTitle = "Previous Post: " + nextTitle;
			next = "<a title=\"" + nextTitle + "\" class='netxLink next-recipe-link' href='" + nextElement.getElementsByTagName("url").item(0).getTextContent() + ".html'>Next Post&nbsp;</a>";
		} else {
			next = "<span class='netxLinkD'>Next Post&nbsp;</span>";
		}
		return "<div class='clear linkColor topNextPrevNavLinks'><div class='fleft'>" + prev + "</div><div class='fright'>" + next + "</div></div>";
	}
	
	private static void compressFiles(String srcBase, String jarBase) {
		String jarFile = jarBase + "/yuicompressor-2.4.8.jar";
		String jsFile = srcBase + "template/js/site.js";
		String jsDest = srcBase + "js/site.js";
		String compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
		try {
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(compressString);
			System.out.println("Compressing: " + compressString);
			
			jsFile = srcBase + "template/js/siteCommon.js";
			jsDest = srcBase + "js/siteCommon.js";
			compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
			pr = rt.exec(compressString);
			System.out.println(compressString);
			System.out.println("Compressing: " + jsFile);
			
			jsFile = srcBase + "template/js/jquery.flexslider.js";
			jsDest = srcBase + "js/jquery.flexslider.js";
			compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
			pr = rt.exec(compressString);
			System.out.println("Compressing: " + jsFile);
			
			
			jsFile = srcBase + "template/css/site.css";
			jsDest = srcBase + "css/site.css";
			compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
			pr = rt.exec(compressString);
			System.out.println("Compressing: " + jsFile);
			
			jsFile = srcBase + "template/js/jquery.fancybox-1.3.4.js";
			jsDest = srcBase + "js/jquery.fancybox-1.3.4.js";
			compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
			pr = rt.exec(compressString);
			System.out.println("Compressing: " + jsFile);
			
			jsFile = srcBase + "template/css/site-mobile.css";
			jsDest = srcBase + "mobile/css/site.css";
			compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
			pr = rt.exec(compressString);
			System.out.println(compressString);
			
			jsFile = srcBase + "template/css/jquery.fancybox-1.3.4.css";
			jsDest = srcBase + "css/jquery.fancybox-1.3.4.css";
			compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
			pr = rt.exec(compressString);
			System.out.println("Compressing: " + jsFile);
			
			jsFile = srcBase + "template/css/flexslider.css";
			jsDest = srcBase + "css/flexslider.css";
			compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
			pr = rt.exec(compressString);
			System.out.println("Compressing: " + jsFile);
			
			jsFile = srcBase + "template/js/site-mobile.js";
			jsDest = srcBase + "mobile/js/site-mobile.js";
			compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
			pr = rt.exec(compressString);
			System.out.println("Compressing: " + jsFile);
			
			jsFile = srcBase + "template/css/home-slide.css";
			jsDest = srcBase + "css/home-slide.css";
			compressString = "java -jar " + jarFile + " " + jsFile + " -o " + jsDest + " --charset utf-8";
			pr = rt.exec(compressString);
			System.out.println("Compressing: " + jsFile);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String formatXML(String xml) {

        try {
            final InputSource src = new InputSource(new StringReader(xml));
            final Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
            final Boolean keepDeclaration = Boolean.valueOf(xml.startsWith("<?xml"));

            //May need this: System.setProperty(DOMImplementationRegistry.PROPERTY,"com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");


            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            final LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); // Set this to true if the output needs to be beautified.
            writer.getDomConfig().setParameter("xml-declaration", keepDeclaration); // Set this to true if the declaration is needed to be outputted.

            return writer.writeToString(document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public static String sortByValue(Map map) {
		String dataNode = "<div class='top10Tags'><div class='headingTags'>Top 10 categories of our blog:</div><br/>";
		Set set = map.entrySet();
        List<Entry<String, Integer>> list = new ArrayList(set);
        Collections.sort( list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );
        int count = 0;
        for(Map.Entry<String, Integer> entry:list) {
        	count ++;
        	if (count == 11) {
        		break;
        	}
        	dataNode += "<a class=\"tag-link-click\" title=\"Posts on " + entry.getKey() + "\" href='" + entry.getKey().replace(" ", "-") + "-tag.html'>" + entry.getKey() + " (" + entry.getValue() + ")</a>";
        }
        dataNode += "</div>";
        System.out.println(dataNode);
        return dataNode;
	} 
	
	public static String getTagHTML(String tagData) {
		String tagHTML = "<div class=\"homeTagContent\">";
		if (tagData!=null) {
			String ss1[] = tagData.split(",");
			for (int ii=0; ii<ss1.length; ii++) {
				String tg = ss1[ii];
				tagHTML += "<div><img alt='Tag Marker' src='images/tag-image-small.png' nopin='nopin'/><a class=\"tag-link-click\" href=\"" + tg.replace(" ", "-") + "-tag.html\">" + tg + "</a></div>";
			}
		}
		tagHTML += "</div>";
		return tagHTML;
	}
	
	public static String getDateView(String inputDate) {
		DateFormat fullDf = DateFormat.getDateInstance(DateFormat.MEDIUM);
		String datepub;
		try {
			datepub = fullDf.format(new Date(inputDate));
		} catch (Exception e1) {
			datepub = inputDate;
		}
		return datepub;
	}

}