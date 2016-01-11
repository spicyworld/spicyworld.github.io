function fixedMenu() {
	$(window).bind('scroll', function() {
         if ($(window).scrollTop() > 150) {
         	$("#siteLogoMenu").show();
         	$('.headerMenu').addClass('fixed');
         } else {
         	$("#siteLogoMenu").hide();
            $('.headerMenu').removeClass('fixed');
         }
    });
}

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
	$('#rightAddSection').show();
	$('#adSectionTop').show();
}

// Search Code Starts
var criteriaData = '';
var globalTemplate = '<tr class=""><td><div style="clear:both;width:100%"><div class="leftitem" style="padding-right: 20px;float:left;width: 35%"><img title="##TITLE##" alt="##TITLE##" width="330px" src="##PIC##"></div><div style="float:left;width:60%"><div class="title"><div style="float:left;width:90%"><a alt="#TITLE##" title="##TITLE##" class="noStyle" href="##URL##.html">##TITLE##</a></div></div><div class="desc">##DESC##</div></div></div></td></tr><tr class="blankTR"></tr>';
var dataFile = '';

function populateData(dataElement) {
	var htmlForm = '<div class="recipePage"><table class="dataTable">';
	if (dataElement.length > 0) {
		for (var i=0; i<dataElement.length; i++) {
			htmlForm += populateSearchResult(dataElement[i]);
		}
		htmlForm += '</table></div>';
		$('#middleDataSearchSection').html(htmlForm);
		highlight(criteriaData, $('.desc'));
	} else {
		$('#searchMdl').html('');
		$('#searchMdl').addClass('f30px');
		$('#searchMdl').html('No results found, please try again with different criteria.');
	}
}

function searchForRecipe() {
	var dataURL = getQueryVariable('criteria').trim();
	if (dataURL != '') {
		$('#searchBox').placeholder = dataURL;
		criteriaData = unescape(dataURL)
		criteriaData = criteriaData.toLowerCase();
		readXML();
	} else {
		$('#searchMdl').addClass('f30px');
		$('#searchMdl').html('Incorrect search critera, please try again.');
	}
}
// Search Code Ends
