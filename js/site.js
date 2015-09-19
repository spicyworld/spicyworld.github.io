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
		var data = ($(this).text()).toLowercase();
	    return (data.indexOf(criteriaData) > -1);
	}).closest('element');	
	var htmlForm = '<div class="recipePage"><table class="dataTable">';
	if ($element.length > 0) {
		for (var i=0; i<$element.length; i++) {
			htmlForm += populateSearchResult($element[i]);
		}
		htmlForm += '</table></div>';
		$('.middleData').html(htmlForm);
	} else {
		$('#searchMdl').html('');
		$('#searchMdl').addClass('f30px');
		$('#searchMdl').html('No results found, please try again with different criteria.');
	}
}

function populateSearchResult(elementData) {
	var url = $('url', elementData).text();
	var title = $('title', elementData).text();
	var thumb = $('thumb', elementData).text();
	var desc = $('shortDesc', elementData).text();
	var type = $('type', elementData).text();
	var template = '<tr class=""><td><div style="clear:both;width:100%"><div class="leftitem" style="padding-right: 20px;float:left;width: 30%"><img title="##TITLE##" alt="##TITLE##" src="##PIC##"></div><div style="float:left;width:60%"><div class="title"><div style="float:left;" class="##TYPE##">&nbsp;</div><div style="float:left;width:90%"><a alt="#TITLE##" title="##TITLE##" class="noStyle" href="##URL##.html">##TITLE##</a></div></div><div class="desc">##DESC#</div></div></div></td></tr><tr class="blankTR"></tr>';
	return template.replace('##TITLE##', title).replace('##DESC##', desc).replace('##TYPE##', type).replace('##PIC##', thumb).replace('##URL##', url);
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
		criteriaData = unescape(dataURL)
		criteriaData = criteriaData.toLowercase();
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