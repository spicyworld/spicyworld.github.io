function startTagPage() {
	$("#wordcloud1").awesomeCloud({
		"size" : {
			"grid" : 25,
			"normalize" : true
		},
		"options" : {
			"color" : "random-dark",
			"rotationRatio" : 0.2,
			"printMultiplier" : 3.5,
			"sort" : "random"
		},
		"font" : "fantasy, 'Times New Roman', Times, serif",
		"shape" : "circle"
	});
}

function initFancy() {
	$("a.group1").fancybox();
}


function initFancyAll() {
	$("a.group1").fancybox();
}

function loadSlideShow() {
  jQuery(document).ready(function ($) {
       var width = document.getElementById('slider1_container').offsetWidth;
       document.getElementById('slider1_container').style.width = width;
       document.getElementById('internalID').style.width = width;
       var options = { $AutoPlay: true };
       var jssor_slider1 = new $JssorSlider$('slider1_container', options);
   });
}

function enableAd () {
	$('#adSection').show();
}

function readXML() {
	try {
        var xmlPath = "template/data.xml";
        $.ajax({
            type: "GET",
            url: xmlPath,
            dataType: "xml",
            success: successXML
        });
    } catch (e) {
        alert("Error while reading XML; Description – " + e.description);
    }
}

var criteriaData = '';

function successXML(xmlData) {
	var $element = $(xmlData).find('title').filter(function() {
	    return ($(this).text().indexOf(criteriaData) > -1);
	}).closest('element');	
	$('#searchMdl').html('');
	if ($element.length > 0) {
		for (var i=0; i<$element.length; i++) {
			populateSearchResult($element[i]);
		}
	} else {
		$('#searchMdl').addClass('f30px');
		$('#searchMdl').html('No results found, please try again with different criteria.');
	}
}

function populateSearchResult(elementData) {
	var url = $('url', elementData).text();
	var title = $('title', elementData).text();
	$('#searchMdl').html($('#searchMdl').html() + '<br/><a href="' + url + '.html">' + title + '</a>')
}

function enterPressCheck(B){
	var A;
	if(window.event){
		A=window.event.keyCode;
	}else{
		A=B.which;
	}
	if(A==13){
		searchData();
	}else{
		return"false";
	}
}

function searchData(){
	var data = $('#searchBox').val().trim();
	if (data != '') {
		location.href = 'search.html?criteria=' + data;
	} else {
		$('#searchBox').placeholder = 'Search recipes ...';
	}
}

function searchForRecipe() {
	var dataURL = getQueryVariable('criteria').trim();
	if (dataURL != '') {
		$('#searchBox').placeholder = dataURL;
		criteriaData = dataURL;
		readXML();
	} else {
		$('#searchMdl').addClass('f30px');
		$('#searchMdl').html('Incorrect search critera, please try again.');
	}
}

function getQueryVariable(variable) {
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for (var i=0;i<vars.length;i++) {
    var pair = vars[i].split("=");
    if (pair[0] == variable) {
      return pair[1];
    }
  } 
  return "";
}