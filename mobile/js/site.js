function startTagPage() {
	var spans = $('.data-points');
	for (var i=0; i<spans.length; i++) {
		spans[i].style.fontSize = 18 + eval(spans[i].getAttribute('class').replace('data-points ', '')) + 'px';
		spans[i].style.color = getRandomColor(eval(spans[i].getAttribute('class').replace('data-points ', '')));
	}
}

function getRandomColor(inp) {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < inp; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

function callMe(url) {
	location.href = url;
}

var criteriaData = '';

function readXML() {
	try {
        var xmlPath = "../template/data.xml";
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
	var cri = criteriaData.split(" ");
	var $element = $(xmlData).find('title').filter(function() {
		var data = ($(this).text()).toLowerCase();
		for (var i=0; i<cri.length; i++) {
			if (data.indexOf(cri[0]) > -1) {
				return true;
			}
		}
	}).closest('element');	
	var htmlForm = '<div class="recipePage"><table class="dataTable">';
	if ($element.length > 0) {
		for (var i=0; i<$element.length; i++) {
			htmlForm += populateSearchResult($element[i]);
		}
		htmlForm += '</table></div>';
		$('#searchMiddle').html(htmlForm);
	} else {
		$('#searchMiddle').html('');
		$('#searchMiddle').addClass('f30px');
		$('#searchMiddle').html('No results found, please try again with different criteria.');
	}
}

function populateSearchResult(elementData) {
	var url = $('url', elementData).text();
	var title = $('title', elementData).text();
	var thumb = $('thumb', elementData).text();
	var desc = $('shortDesc', elementData).text();
	var type = $('type', elementData).text();
	var template = '<tr class=""><td><div style="clear:both;width:100%"><div class="leftitem" style="padding-right: 20px;float:left;width: 30%"><img title="##TITLE##" alt="##TITLE##" src="##PIC##"></div><div style="float:left;width:60%"><div class="title"><div style="float:left;" class="##TYPE##">&nbsp;</div><div style="float:left;width:90%"><a alt="#TITLE##" title="##TITLE##" class="noStyle" href="##URL##.html">##TITLE##</a></div></div><div class="desc">##DESC##</div></div></div></td></tr><tr class="blankTR"></tr>';
	template = replaceAll('##TITLE##', title, template);
	template = replaceAll('##DESC##', desc, template);
	template = replaceAll('##TYPE##', type, template);
	template = replaceAll('##PIC##', thumb, template);
	template = replaceAll('##URL##', url, template);
	return template;
}

function replaceAll(find, rep, str) {
	var re = new RegExp(find, 'g');
	str = str.replace(re, rep);
	return str;
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
		criteriaData = criteriaData.toLowerCase();
		readXML();
	} else {
		$('#searchMiddle').addClass('pad50px');
		$('#searchMiddle').html('Incorrect search critera, please try again.');
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