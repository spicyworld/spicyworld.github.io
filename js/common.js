var openCloseFlag = 0;
function openWindow(pageType) {
	location.href = "recipes.html";
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function getPositionDataFromURL(data, url) {
	for (var i=0; i<data.length; i++) {
		if (url == data[i].url) {
			return data[i];
		}
	}
}

function loadDataLoad() {
	var position = getParameterByName("position"), page = getParameterByName("page");
	var out = "";
    var curItem;
    curItem = getPositionDataFromURL(myData, position);
    out = "<div><h3 class='h2Class'><u>" + curItem.title + "</u> <span>(" + curItem.type + ")</span><div class=\"fb-like\" data-href=\"" + location.href.replace('food', 'index') + "\" data-width=\"100\" data-layout=\"button_count\" data-action=\"like\" data-show-faces=\"true\" data-share=\"true\"></div></h3><br/>" +
    "<div>" +
    	"<div class='div3Pos posLeft'><img src='" + curItem.pic + "' width='100%'/><br/><br/><u><b>Ingrediants</b></u><br/><br/>" + 
    	curItem.ingrediants + 
    	"</div>" + 
    	"<div class='div3Pos'><u><b>Steps</b></u><br/><br/>" + 
    	curItem.process + 
    	"<br/><div class='complete'>" + curItem.completionStatement + "</div>" +
    	"<div class='garnishment'>" + curItem.garnishment + "</div></div>" +
    "</div></div>";
    document.getElementById("placeDataFood").innerHTML = out;
}

function loadFoodDataLoad() {
	var position = getParameterByName("item");
	var itemType = "";
	if (position != '') {
		var out = "";
	    var curItem;
	    curItem = getPositionDataFromURL(myData, position);
	    if (curItem.type == 'nonVegItem') {
			itemType = "Non-Vegetarian";
	    } else {
	    	itemType = "Vegetarian";
	    }
	    out = "<div><h3 class='h2Class'><u>" + curItem.title + "</u> <span>(" + itemType + ")</span><div class=\"fb-like\" data-href=\"" + location.href + "\" data-width=\"100\" data-layout=\"button_count\" data-action=\"like\" data-show-faces=\"true\" data-share=\"true\"></div></h3><br/>" +
	    "<div>" +
	    	"<div class='div3Pos posLeft'><img src='" + curItem.pic + "' width='100%'/><br/><br/><u><b>Ingrediants</b></u><br/><br/>" + 
	    	curItem.ingrediants + 
	    	"</div>" + 
	    	"<div class='div3Pos'><u><b>Steps</b></u><br/><br/>" + 
	    	curItem.process + 
	    	"<br/><div class='complete'>" + curItem.completionStatement + "</div>" +
	    	"<div class='garnishment'>" + curItem.garnishment + "</div></div>" +
	    "</div></div>";
	    document.getElementById("placeDataFood").innerHTML = out;
	    document.title = curItem.title + " | Welcome to Arpita's Kitchen"
	}
}

function checkPageType() {
	var position = getParameterByName("position"), page = getParameterByName("page");
	if (position != '') {
		$("#bottom").show();
  		openCloseFlag = 1;
	  	$("#bottom").animate({
		    width: 95 + "%",
		    opacity: 1
		  }, 500, function() {
		  		$('#bottom').html('<iframe class="loadFrame" src="food.html?position=' + position + '&page=' + page + '"/><div class="closeBtn"><a href="javascript:closePopup()"><img src="images/close-button.png"></a></div>');
	 	  }
	 	);
	}
	loadContentForHomePage();
}

function closePopup() {
	openCloseFlag = 0;
  	$("#bottom").animate({
	    width: 10,
	    opacity: 1
	  }, 200, function() {
	  		$("#bottom").html("");
	  		$("#bottom").hide();
 	  }
 	);
 	return false;
}

function loadContentForHomePage() {
	var out = "<table><tr>";
    var i, count = getRandomInt(0, myData.length - 5);
    for(i = count; i < count + 5; i++) {
    	out += "<td><a href='food-item.html?item=" + myData[i].url + "'><img style='width:100% !important; height:130px !important;' src=\"" + myData[i].pic + "\"/></a>" +
    	"</td>";
    }
    out += "</tr></table>";
    document.getElementById("recData").innerHTML = out;
}

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function loadHomePageGraphics() {
    var count = getRandomInt(0, myData.length - 5);
    var out = "<div><a href='" + myData[count].url + "'><img alt='"+myData[count].title+"' title='"+myData[count].title+"' style='width:225px !important;' src=\"" + myData[count].thumb + "\"/></a></div><div>Easy and Simple Recipes make your cooking faster and your food delicious.</div>";
    document.getElementById("homeDiv").innerHTML = out;
    playHomePage();
}

function changePage() {
	var pageDD = document.getElementById('pageDD').value;
	if (pageDD == 'all') {
		$('.vegItem-1').removeClass('hide');
		$('.nonVegItem-1').removeClass('hide');
	} else if (pageDD == 'vegItem-1') {
		$('.vegItem-1').removeClass('hide');
		$('.nonVegItem-1').addClass('hide');
	} else {
		$('.vegItem-1').addClass('hide');
		$('.nonVegItem-1').removeClass('hide');
	}
	
}

function loadRecepiesData(type) {
	var out = "";
    var i;
    out = "<table class=\"dataTable\">";
    tdClass = "";
    var flag = false;
    for(i = 0; i < myData.length; i++) {
    	if (type != '' && type == myData[i].type) {
    		flag = true;
    	} else if (type != '' && type != myData[i].type) {
    		flag = false;
    	} else {
    		flag = true;
    	}
    	if (flag) {
	    	tdClass = "<tr><td>";
	    	out += tdClass + "<div style='clear:both;width:750px'><div class='leftitem' style=\"padding-right: 20px;float:left;width: 220px\"><a href=\"food-item.html?item=" + myData[i].url + "\"><img src=\"" + myData[i].thumb + "\"/></a></div><div style=\"float:left;width:480px\"><a class='noStyle' href=\"food-item.html?item=" + myData[i].url + "\"><div class=\"title\"><div style=\"float:left;\" class=\"" + myData[i].type + "\">&nbsp;</div><div style=\"float:left;width:90%\">" + myData[i].title + "</div></div><div class=\"desc\">" + myData[i].shortDesc + "</div></a></div></div></td>";
	    	out += "</tr>";
			out += "<tr class=\"blankTR\"></tr>";
		}
    }
    out = out + "</table>";
    document.getElementById("placeData").innerHTML = out;
}
