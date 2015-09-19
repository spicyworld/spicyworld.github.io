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

function successXML(xmlData) {
	console.log(xmlData);
	var $element = $(xmlData).find('process').filter(function() {
	    return ($(this).text().indexOf("marination") > -1);
	}).closest('element');
	console.log($element);
	var url = $('url', $element).text();
	var pic = $('pic', $element).text();
	console.log(url + " " + pic);
}