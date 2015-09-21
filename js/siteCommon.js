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

function readXML() {
	try {
        var xmlPath = dataFile + "template/data.xml";
        $.ajax({
            type: "GET",
            url: xmlPath,
            dataType: "xml",
            success: successXML
        });
    } catch (e) {
        alert("Error Loading Recipe from Server: " + e.description);
    }
}

function successXML(xmlData) {
    var cri = criteriaData.split(" ");
    var $element = $(xmlData).find('element').filter(function() {
        var data = ($(this).text()).toLowerCase();
        for (var i=0; i<cri.length; i++) {
            if (data.indexOf(cri[0]) > -1) {
                return true;
            }
        }
    }).closest('element');  
    populateData($element);
}

function populateSearchResult(elementData) {
    var url = $('url', elementData).text();
    var title = $('title', elementData).text();
    var thumb = $('thumb', elementData).text();
    var desc = $('shortDesc', elementData).text();
    var type = $('type', elementData).text();
    var typeDesc = '';
    if (type == 'vegItem') {
        typeDesc = 'Veg Item';
    } else {
        typeDesc = 'Non-Veg Item';
    }
    var template = globalTemplate;
    template = replaceAll('##TITLE##', title, template);
    template = replaceAll('##DESC##', desc, template);
    template = replaceAll('##TYPE##', type, template);
    template = replaceAll('##PIC##', thumb, template);
    template = replaceAll('##URL##', url, template);
    template = replaceAll('##TYPE_DESC##', typeDesc, template);
    return template;
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

function replaceAll(find, rep, str) {
    var re = new RegExp(find, 'g');
    str = str.replace(re, rep);
    return str;
}

function searchData(){
    var data = $('#searchBox').val().trim();
    if (data != '') {
        location.href = 'search.html?criteria=' + data;
    } else {
        $('#searchBox').placeholder = 'Search recipes ...';
    }
}

function highlight(word, element) {
    var rgxp = new RegExp(word, 'g');
    var repl = '<span class="highlight">' + word + '</span>';
    if (element.length > 0) {
        for (var i=0; i<element.length; i++) {
            var html = element[i].innerHTML;
            element[i].innerHTML = html.replace(rgxp, repl);
        }
    } else {
        var html = element.html();
        element.html(html.replace(rgxp, repl));
    }
}
