var appFlag = 'N';

function guid() {
  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1);
  }
  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
    s4() + '-' + s4() + s4() + s4();
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
        return "false";
    }
}

function searchForRecipeNew() {
    var options = {
      shouldSort: true,
      tokenize: true,
      threshold: 0.3, // Decrease to increase exact match
      location: 0,
      maxPatternLength: 32,
      keys: ['title', 'add-title', 'keywords', 'ingrediants']
    };
    var f = new Fuse(allRecipes, options);
    var result = f.search(criteriaData);
    populateData(result);
}

function populateSearchResult(elementData, count) {
    var url = $('url', elementData).text() || elementData.url;
    var title = $('title', elementData).text() || elementData.title;
    var pic = $('pic', elementData).text() || elementData.pic;
    var desc = $('shortDesc', elementData).text() || elementData.shortDesc;
    var type = $('type', elementData).text() || elementData.type;
    var blogType = elementData['blog-type'] || type;
    var typeDesc = '';
    if (type == 'vegItem') {
        typeDesc = 'Veg Recipe';
    } else {
        typeDesc = 'NonVeg Recipe';
    }
    if (blogType == 'sb') {
        typeDesc = 'Featured Post';
        type = 'featuredBlog';
    }
    var template = globalTemplate;
    template = replaceAll('##TITLE##', title, template);
    template = replaceAll('##DESC##', desc, template);
    template = replaceAll('##TYPE##', type, template);
    template = replaceAll('##PIC##', pic, template);
    template = replaceAll('##URL##', url, template);
    template = replaceAll('##TYPE_DESC##', typeDesc, template);
    template = replaceAll('##TYPE_CLASS##', type, template);
    if (count % 2 == 1) {
        template = replaceAll('##RIGHT_CLASS##', " rightClass", template);
    } else {
        template = replaceAll('##RIGHT_CLASS##', "", template);
    }
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
        $('#searchBox').placeholder = 'Search Recipes';
    }
}

function setCookieValue(name, value) {
    if(typeof(Storage) !== "undefined") {
        localStorage.setItem(name, value);
    } else {
        var d = new Date();
        // Expire cookiw in 1 day, expDay = 1
        var expDay = 1;
        d.setTime(d.getTime() + (expDay*24*60*60*1000));
        var expires = "expires="+d.toUTCString();
        document.cookie = name + "=" + value + "; " + expires + "; path=/";
    }
}

function getCookieValue (cname) {
    if(typeof(Storage) !== "undefined") {
        return localStorage.getItem(cname);
    } else {
        var name = cname + "=";
        var ca = document.cookie.split(';');
        for(var i=0; i<ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0)==' ') c = c.substring(1);
            if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
        }
        return "";
    }
}
