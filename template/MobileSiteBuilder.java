package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class MobileSiteBuilder {
	
	public static String buildNo = "?sessionId=113";
	public static String recipeImageBase = "../";

	public static void main(String[] args) {
		mobileSiteBuilder();
	}
	
	public static void mobileSiteBuilder() {
		String basePath = PropertiesUtil.getValue("repotemplate.path") + "mobile/";
		String dataXML = PropertiesUtil.getValue("repotemplate.path");
		String templatePath = basePath;
		String tag_data_template = templatePath + "template/template.html";
		String recipes_data_front = "<table class=\"dataTable\">";
		
		String recipes_data = "";
		String fileData = "";
		int count = 1, perPageData = 18;
		List recipeDataList = new ArrayList();
		String tags = "";
		int homeImg = 3;
		//copyFolder(dataXML + "recipeimages", basePath + "recipeimages");
		String latest3DataForHomePage = "<div class=\"middleTop\"><div class=\"data\"><p>Easy and Simple Recipes make your cooking faster and your food delicious. Check out our recipes.</p><br/><a href=\"recipes.html\">Recipes</a></div></div><div class=\"middleBottom\">";
		try {

			File fXmlFile = new File(dataXML + "template/data.xml");
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
					Element prevElement = null;
					Element nextElement = null;
					try {
						prevElement = (Element) nList.item(temp - 1);
					} catch (Exception e) {
						prevElement = null;
					}
					try {
						nextElement = (Element) nList.item(temp + 1);
					} catch (Exception e) {
						nextElement = null;
					}
					createItemData(templatePath, eElement, count, prevElement, nextElement);
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
						String addTitle = title;
						try { 
							addTitle = title + " / " + eElement.getElementsByTagName("add-title").item(0).getTextContent();
						} catch (Exception e) {}
						String url = eElement.getElementsByTagName("url").item(0).getTextContent();
						latest3DataForHomePage += "<div class=\"" + classToApply + "\"><a title='" + addTitle + "' alt='" + addTitle + "' href=\"" + url + 
								".html\"><img title='" + addTitle + "' alt='" + addTitle + "' src=\"" + recipeImageBase + eElement.getElementsByTagName("pic").item(0).getTextContent() + "\"/>"
										+ "</a><div class=\"title\"><a title='" + addTitle + "' alt='" + addTitle + "' href=\"" + url + ".html\">" + title + "</a></div></div>";
					}
					count++;
					try {
						tags += eElement.getElementsByTagName("tags").item(0).getTextContent() + ",";
					} catch (Exception e) {}
				}
			}
			latest3DataForHomePage += "</div>";
			if (!"".equals(recipes_data)) {
				recipeDataList.add(recipes_data_front + recipes_data + "</table>");
				recipes_data = "";
			}
			
			for (int i=0; i<recipeDataList.size(); i++) {
				fileData = readFile(basePath + "template/template.html");
				recipes_data = (String) recipeDataList.get(i);
				String pagination = getPagination(i+1, recipeDataList.size());
				
				fileData = fileData.replace("##TITLE_DATA##", "Our Recipes - Page " + (i+1) + " | Spicy World by Arpita (Mobile Friendly Website)");
				fileData = fileData.replace("##MIDDLE_DATA##", "<div class='recipePage'>" + recipes_data + "</div><br/><div class='topPaginationData'>" + pagination + "</div><div class='clear'>&nbsp;</div>");
				fileData = fileData.replace("##recipes_sel##", "selected");
				fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
				fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
				fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita. Here are our recipes (page number " + (i+1) + ") that you might like.");
				fileData = fileData.replaceAll("##KEYWORD_DATA##", "Recipes in Spicy World");
				fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home1.jpg");
				
				if (i > 0) {
					fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<link rel='canonical' href='http://spicyworld.in/recipes-" + i + ".html' >");
					fileData = fileData.replaceAll("##URL_DATA##", "/recipes-" + i + ".html");
					saveFile(templatePath + "recipes-" + i + ".html", fileData);	
				} else {
					fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<link rel='canonical' href='http://spicyworld.in/recipes.html' >");
					fileData = fileData.replaceAll("##URL_DATA##", "/recipes.html");
					saveFile(templatePath + "recipes.html", fileData);	
				}
			}
			
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
				htmlTags += "<span><a class=\"data-points " + entry.getValue() + "\" href=\"" + data + "-tag.html\">" + entry.getKey() + " (" + entry.getValue() + ")</a></span>";
				count++;
				String h1Tag = "<h1 class='headerFont'>Posts on <i><b>'" + entry.getKey() + "'</b></i></h1>";
				generateTagHTML(data, tag_data_template, nList, templatePath, count, entry.getKey(), h1Tag);
			}
			
			// Save Tags
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Recipe Tags | Spicy World by Arpita (Mobile Friendly Website)");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div role='main'><div id='wordcloud1' class='wordcloud'>" + htmlTags + "</div></div>");
			fileData = fileData.replace("##tags_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<link rel='canonical' href='http://spicyworld.in/tags.html' >");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "startTagPage();");
			fileData = fileData.replaceAll("##URL_DATA##", "/tags.html");
			fileData = fileData.replaceAll("##DESC_DATA##", "Tag cloud is an easy way to link multiple content and you can easily choose the content you are looking for from various tags.");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", keywordTags);
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home2.jpg");
			saveFile(templatePath + "tags.html", fileData);
			
			//Save HomePage
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Welcome to Spicy World by Arpita - Mobile Friendly Website of Spicy World");
			fileData = fileData.replace("##MIDDLE_DATA##", latest3DataForHomePage);
			fileData = fileData.replace("##index_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<link rel='canonical' href='http://spicyworld.in' >");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##URL_DATA##", "/index.html");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Spicy World, Arpita's Kitchen, Food Recipes, All Spicy Foods.");
			fileData = fileData.replaceAll("##DESC_DATA##", "Easy and Simple Recipes make your cooking faster and your food delicious. Check out all available recipes.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home1.jpg");
			saveFile(basePath + "index.html", fileData);
			
			
			//Save Search Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "Search Recipes | Spicy World by Arpita (Mobile Friendly Website)");
			fileData = fileData.replace("##MIDDLE_DATA##", "<div id='searchMiddle'><img width='40px' style='padding-top:50px' src=\"../images/loading.gif\"/><div style='padding-bottom:50px' >Searching for related recipes, please wait ...</div></div>");
			fileData = fileData.replace("##index_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "searchForRecipe();");
			fileData = fileData.replaceAll("##URL_DATA##", "/search.html");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "Spicy World, Arpita's Kitchen, Search recipes, recipe search, specific recipes, search foods");
			fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita. Search recipes from our library of all recipes.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home3.jpg");
			saveFile(basePath + "search.html", fileData);
			
			//Save About Me Page
			fileData = readFile(basePath + "template/template.html");
			fileData = fileData.replace("##TITLE_DATA##", "About Me | Spicy World by Arpita (Mobile Friendly Website)");
			fileData = fileData.replace("##MIDDLE_DATA##", "<br/><img class='aboutImg' title='Arpita' alt='Arpita' src='" + recipeImageBase + "images/about-arpita.jpg" + buildNo + "'><br/><br/><br/>" 
			+ WebsiteBuilder.aboutPageData.replace("spicyworld.in", "spicyworld.in/mobile") + "<br/><br/>");
			fileData = fileData.replace("##about_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			fileData = fileData.replaceAll("##URL_DATA##", "/about-me.html");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "About me, About Spicy World");
			fileData = fileData.replaceAll("##DESC_DATA##", "Welcome to Spicy World by Arpita. Here are the cast and crew of Spicy World.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/home/home3.jpg");
			saveFile(basePath + "about-me.html", fileData);
			
			//Save User Agreement Page
			fileData = readFile(basePath + "template/template.html");
			String uaData = readFile(PropertiesUtil.getValue("repotemplate.path") + "template/user-agreement.txt");
			fileData = fileData.replace("##TITLE_DATA##", "Spicy World by Arpita: " + "User Agreement");
			fileData = fileData.replace("##MIDDLE_DATA##", uaData);
			fileData = fileData.replace("##index_sel##", "selected").replace("##ua_sel##", "selected");
			fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "");
			fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS_TOP##", "");
			fileData = fileData.replaceAll("##KEYWORD_DATA##", "User Agreement, Cookie, Terms of Use");
			fileData = fileData.replaceAll("##DESC_DATA##", "User Agreement to use Spicy World.");
			fileData = fileData.replaceAll("##IMG_DATA##", "images/site-logo.png");
			fileData = fileData.replaceAll("##URL_DATA##", "/user-agreement.html");
			fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
			saveFile(basePath + "user-agreement.html", fileData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Processed ...");
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
		fileData = fileData.replace("##TITLE_DATA##", "Posts on " + tagDataStr.toUpperCase() + " | Spicy World by Arpita (Mobile Friendly Website)");
		fileData = fileData.replace("##MIDDLE_DATA##", h1Tag + recipes_data);
		fileData = fileData.replace("##tags_sel##", "selected");
		fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
		fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<link rel='canonical' href='http://spicyworld.in/" + tag + "-tag.html' >");
		fileData = fileData.replaceAll("##URL_DATA##", "/" + tag + "-tag.html");
		fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
		saveFile(baseTemplatePath + tag + "-tag.html", fileData);
	}
	
	private static String getPagination(int currentPage, int totalPage) {
		return WebsiteBuilder.getPagination(currentPage, totalPage, null);
	}
	
	public static String siteMapEntry(Element eElement) {
		String siteMapDataEntry = null;
		siteMapDataEntry = "<url><loc>http://spicyworld.in/" + eElement.getElementsByTagName("url").item(0).getTextContent() + ".html</loc></url>";
		return siteMapDataEntry;
	}
	
	public static void createItemData(String templatePath, Element eElement, int count, Element prevElement, Element nextElement) {
		String out = "";
		String type = eElement.getElementsByTagName("type").item(0)
				.getTextContent();
		String blogType = "rb";
		try {
			blogType = eElement.getElementsByTagName("blog-type").item(0).getTextContent();
		} catch (Exception e) {
			blogType = "rb";
		}
		String itemType = "";
		String color = "green";
		if ("nonVegItem".equals(type)) {
			itemType = "Non-Vegetarian";
			color = "red";
		} else {
			itemType = "Vegetarian";
			color = "green";
		}
		String title = eElement.getElementsByTagName("title").item(0).getTextContent();
		try {
			String addTitle = eElement.getElementsByTagName("add-title").item(0).getTextContent();
			title = title + " / " + addTitle;
		} catch (Exception e) {}
		String desc = eElement.getElementsByTagName("shortDesc").item(0).getTextContent();
		String url = eElement.getElementsByTagName("url").item(0).getTextContent();
		String additionalImg = "";
		try  {
			additionalImg = eElement.getElementsByTagName("add-pic").item(0).getTextContent();
			additionalImg = "<br/><div class=\"div3Pos posLeft\"><img alt='" + title + "' title='" + title + "' src='" + recipeImageBase + additionalImg + buildNo + "'></div><br/>";
		} catch (Exception e) {
			additionalImg = "";
		}
		String additionalImgTop = "";
		try  {
			additionalImgTop = eElement.getElementsByTagName("add-pic-top").item(0).getTextContent();
			additionalImgTop = "<br/><div class=\"div3Pos posLeft\"><img style=\"####\" alt='" + title + "' title='" + title + "' src='" + recipeImageBase + additionalImgTop + buildNo + "'></div><br/>";
			try {
				String addImgStyle = eElement.getElementsByTagName("add-pic-top-style").item(0).getTextContent();
				additionalImgTop = additionalImgTop.replace("####", addImgStyle);
			} catch (Exception e) {
				additionalImgTop = additionalImg.replace("####", "");
			}
			if (additionalImg != "") {
				additionalImg += "<br/>" + additionalImgTop;
			} else {
				additionalImg = additionalImgTop;
			}
		} catch (Exception e) {}
		
		String endImg = "";
		try  {
			endImg = eElement.getElementsByTagName("end-pic").item(0).getTextContent();
			endImg = "<br/><div class=\"div3Pos posLeft\"><img alt='" + title + "' title='" + title + " (Final)' src='" + recipeImageBase + endImg + buildNo + "'></div><br/><br/>";
		} catch (Exception e) {
			endImg = "";
		}
		
		String steps = eElement.getElementsByTagName("process").item(0).getTextContent();
		if (steps.contains("recipeimages")) {
			steps = "<div class='steps-image'>" + steps + "</div>";
		}
		DateFormat fullDf = DateFormat.getDateInstance(DateFormat.MEDIUM);
		String datepub;
		try {
			datepub = fullDf.format(new Date(eElement.getElementsByTagName("pubDate").item(0).getTextContent()));
		} catch (Exception e1) {
			datepub = eElement.getElementsByTagName("pubDate").item(0).getTextContent();
		}
		if (!"rb".equals(blogType)) {
			color = "#75C5DF";
		}
		out = "<div><div class='h2Class'><div style=\"clear:both\"><h1>"
				+ title
				+ "</h1></div><div class='copyColor' style='border-left: 10px solid " + color + "'><i>&copy; 2016 Spicy World</i>, Published on: <i>" + datepub + "</i></div>"
				
				+ "<p class=\"descp\" style=\"padding-top:8px;clear:both\">" + desc.replace("recipeimages/", recipeImageBase + "recipeimages/") + "</p></div><br/>"
				+ "<div>"
				+ "<div class='div3Pos posLeft'><img alt='" + title 
				+ "' title='" + title + "' src='"
				+ recipeImageBase + eElement.getElementsByTagName("pic").item(0).getTextContent() + buildNo
				+ "'/><br/><br/>";
		if ("rb".equals(blogType)) {
			out +=  "<div><h2>Ingredients</h2></div>"
				+ eElement.getElementsByTagName("ingrediants").item(0)
						.getTextContent()
				+ "</div>" + additionalImg + "<div class='div3Pos'><div><h2>Steps</h2></div>"
				+ steps.replace("recipeimages/", recipeImageBase + "recipeimages/")
				+ "<br/>";
		} else {
			out +=  "<div class='div3Pos simple-blog'>"
				+ steps.replace("recipeimages/", recipeImageBase + "recipeimages/")
				+ "<br/>";
		}
		if ("rb".equals(blogType)) {
			out +=  "<div class='complete'>"
				+ eElement.getElementsByTagName("completionStatement").item(0)
						.getTextContent()
				+ "</div>"
				+ "<div class='garnishment'>"
				+ eElement.getElementsByTagName("garnishment").item(0)
						.getTextContent() + "</div></div>" + "</div></div>" + endImg;
		}
		
		try {
			String tagData = eElement.getElementsByTagName("tags").item(0).getTextContent();
			String tags = "<div class=\"tagContent\"><span class=\"heading\">Tags:</span>";
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
		
		String oldNew = WebsiteBuilder.nextPreviousPagination(prevElement, nextElement, " (Mobile)");
		out = oldNew + "<br/>" + out + "<br/>" + oldNew;
		
		// For comment section
		out += "<br/><br/><div class='hideMobileLink'><h2>Leave Your Comments</h2>"
				+ "<div class=\"fb-comments\" data-href=\"http://spicyworld.in/" + url + ".html\" data-width=\"100%\" data-numposts=\"8\"></div></div><br/><br/>";
		
		fileData = fileData.replace("##TITLE_DATA##", title + " | Spicy World by Arpita (Mobile Friendly Website)");
		fileData = fileData.replace("##MIDDLE_DATA##", "<div class='recipeDataPage'>" + out + "</div><div class=\"clear\">&nbsp;</div>");
		fileData = fileData.replace("##recipes_sel##", "selected");
		fileData = fileData.replaceAll("##BUILD_NO##", buildNo);
		fileData = fileData.replaceAll("##ADDITIONAL_SCRIPTS##", "<link rel='canonical' href='http://spicyworld.in/" + url + ".html' >");
		fileData = fileData.replaceAll("##ONLOAD_CALL##", "");
		fileData = fileData.replaceAll("##URL_DATA##", "/" + url + ".html");
		fileData = fileData.replaceAll("##DESC_DATA##", Utility.html2text(desc).replace("\"", "'"));
		fileData = fileData.replaceAll("##KEYWORD_DATA##", keyword);
		fileData = fileData.replaceAll("##IMG_DATA##", eElement.getElementsByTagName("pic").item(0).getTextContent() + buildNo);
		saveFile(templatePath + url + ".html", fileData);
	}

	public static String recepiData(String recipes_data, Element eElement, String prefix) {
		String type = eElement.getElementsByTagName("type").item(0)
				.getTextContent();
		String itemTypeClass = "", vegNonVegTag = "";
		if ("nonVegItem".equals(type)) {
			itemTypeClass = "nonVegItem-1";
			vegNonVegTag = "NonVeg Item";
		} else {
			itemTypeClass = "vegItem-1";
			vegNonVegTag = "Veg Item";
		}
		try {
			String blogType = eElement.getElementsByTagName("blog-type").item(0).getTextContent();
			if ("sb".equals(blogType)) {
				vegNonVegTag = "Featured Post";
			}
		} catch (Exception e) {}
		String title = eElement.getElementsByTagName("title").item(0).getTextContent();
		try {
			title = title + " / " + eElement.getElementsByTagName("add-title").item(0).getTextContent();
		} catch (Exception e) {}
		recipes_data += "<tr class=\"" + itemTypeClass + "\"><td>";
		recipes_data += "<div style='clear:both;width:100%'><div class='leftitem' style=\"\">"
				+ "<a href='" +eElement.getElementsByTagName("url").item(0).getTextContent()  + ".html'>"
						+ "<img title='" + title 
				+ "' alt='" + title + "' src=\""
				+ recipeImageBase + prefix + eElement.getElementsByTagName("pic").item(0)
						.getTextContent() + buildNo
				+ "\"/></a></div><div class=\"rightitem\">"
				+ "<div class=\"title\"><div>"
				+ "<a alt=\"" + title + "\" "
						+ "title=\"" + title + "\" class='noStyle' href=\""
				+ eElement.getElementsByTagName("url").item(0).getTextContent()
				+ ".html\">" + title
				+ "</a>&nbsp;(" + vegNonVegTag + ")</div></div><div class=\"desc\">"
				+ Utility.html2text(eElement.getElementsByTagName("shortDesc").item(0)
						.getTextContent()) + "</div></div></div></td>";
		recipes_data += "</tr><tr class=\"blankTR " + itemTypeClass + "\"></tr>";
		return recipes_data;
	}

	public static String readFile(String fileName) {
		return WebsiteBuilder.readFile(fileName);
	}

	public static void saveHTMLFile(String outPath, String fileData) {
		WebsiteBuilder.saveHTMLFile(outPath, fileData);
	}
	
	public static void saveFile(String outPath, String fileData) {
		WebsiteBuilder.saveFile(outPath, fileData);
	}

	public static void selfCopy(String dest, String processor) {
		WebsiteBuilder.selfCopy(dest, processor);
	}
	
	private static void copyFolder(String src, String dest) {
		File srcFolder = new File(src);
    	File destFolder = new File(dest);
    	
    	//make sure source exists
    	if(!srcFolder.exists()){

           //just exit
           System.exit(0);

        }else{

           try{
        	copyFolder(srcFolder,destFolder);
           }catch(IOException e){
        	e.printStackTrace();
           }
        }
	}
	
	private static void copyFolder(File src, File dest)
	    	throws IOException{
	    	
	    	if(src.isDirectory()){
	    		
	    		//if directory not exists, create it
	    		if(!dest.exists()){
	    		   dest.mkdir();
	    		}
	    		
	    		//list all the directory contents
	    		String files[] = src.list();
	    		
	    		for (String file : files) {
	    		   //construct the src and dest file structure
	    		   File srcFile = new File(src, file);
	    		   File destFile = new File(dest, file);
	    		   //recursive copy
	    		   copyFolder(srcFile,destFile);
	    		}
	    	   
	    	}else{
	    		//if file, then copy it
	    		//Use bytes stream to support all file types
	    		InputStream in = new FileInputStream(src);
	    	        OutputStream out = new FileOutputStream(dest); 
	    	                     
	    	        byte[] buffer = new byte[1024];
	    	    
	    	        int length;
	    	        //copy the file content in bytes 
	    	        while ((length = in.read(buffer)) > 0){
	    	    	   out.write(buffer, 0, length);
	    	        }
	 
	    	        in.close();
	    	        out.close();
	    	}
	    }
}
