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

// Search Code Starts
var criteriaData = '';
var globalTemplate = '<tr class=""><td><div style="clear:both;width:100%"><div class="leftitem" style=""><a href="##URL##.html"><img title="##TITLE##" alt="##TITLE##" src="##PIC##"/></a></div><div class="rightitem"><div class="title"><div><a alt="##TITLE##" title="##TITLE##" class="noStyle" href="##URL##.html">##TITLE##</a>&nbsp;(##TYPE_DESC##)</div></div><div class="desc">##DESC##</div></div></div></td></tr><tr class="blankTR"></tr>';
var dataFile = '../';

function populateData(dataElement) {
	var htmlForm = '<div class="recipePage"><table class="dataTable">';
	if (dataElement.length > 0) {
		for (var i=0; i<dataElement.length; i++) {
			htmlForm += populateSearchResult(dataElement[i]);
		}
		htmlForm += '</table></div>';
		$('#searchMiddle').html(htmlForm);
	} else {
		$('#searchMiddle').addClass('pad50px');
		$('#searchMiddle').html('No results found, please try again with different criteria.');
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
// Search Code Ends

function judgeFullSite() {
	var isApp = getQueryVariable('isApp');
	if (isApp != 'Y') {
		$('#hideMobileLink').show();
	} else {
		$('a').each(function() {
		  appFlag = 'Y';
		  this.href += (/\?/.test(this.href) ? '&' : '?') + 'isApp=Y';
		});
	}
}