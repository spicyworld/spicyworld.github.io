var appFlag = 'N';
function homeImg() {
    /*var cooData = getCookieValue('homeimg');
    if (cooData == 'donee') {
        return;
    }
    $t = $(".specialHome");
    $t1 = $(".specialHomeContainer");
    $("#overlay").css({
      top     : 0,
      width   : $t.outerWidth(),
      height  : $t1.outerHeight()
    });

    $("#overlay").fadeIn();
    $("#img-load-link").css({
      top     : document.getElementById('img-load').offsetTop + ($('#img-load').height() / 2) + 20
    });*/
}

function closeSpecialHome() {
    setCookieValue('homeimg', 'donee');
    $("#overlay").fadeOut(700);
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
    var $element = $(xmlData).find('title').filter(function() {
        var data = ($(this).text()).toLowerCase();
        // Full Text search
        if (data.indexOf(criteriaData) > -1) {
            return true;
        }
        // Tokenized search
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
    var pic = $('pic', elementData).text();
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
    template = replaceAll('##PIC##', pic, template);
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
    appFlag = getQueryVariable('isApp').trim();
    var data = $('#searchBox').val().trim();
    if (data != '') {
        if (appFlag == 'Y') {
            location.href = 'search.html?isApp=Y&criteria=' + data;
        } else {
            location.href = 'search.html?criteria=' + data;
        }
    } else {
        $('#searchBox').placeholder = 'Search recipes by title ...';
    }
}

function highlight(word, element) {
    var rgxp = new RegExp(word, 'ig');
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

function setCookieValue(name, value) {
    var d = new Date();
    // Expire cookiw in 1 day, expDay = 1
    var expDay = 1;
    d.setTime(d.getTime() + (expDay*24*60*60*1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = name + "=" + value + "; " + expires + "; path=/";
}

function getCookieValue (cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
    }
    return "";
}
