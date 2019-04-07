var apiURL = "http://tomcat-spicyworld.rhcloud.com/spicyworldcontent/application?action=";
var apiURLUser = "http://tomcat-spicyworld.rhcloud.com/spicyworldcontent/member?action=";
//var apiURL = "http://localhost:8082/spicyworldcontent/application?action=";
//var apiURLUser = "http://localhost:8082/spicyworldcontent/member?action=";

var activityType = '', pageURLGlobal = '';

function signoutSP() {
	setCookieValue('log', '');
	loginStatusToken('');
}

$(document).ready(function() {

	$(".youtube-video").click(function() {
		$.fancybox({
			'type'			: 'iframe',
			'padding'		: 0,
			'autoScale'		: true,
			'title'			: this.title,
			'href'			: this.href.replace(new RegExp('watch\\?v=', 'i'), 'embed/') + '?rel=0&autoplay=1',
			'overlayShow' : true,
	        'centerOnScroll' : true,
	        'speedIn' : 100,
	        'speedOut' : 50,
	        'allowfullscreen': ''
		});

		return false;
	});
});

$(window).scroll(function() {
    $.each($('.lazy-load-img'), function() {
        if ( $(this).attr('data-src') && $(this).offset().top < ($(window).scrollTop() + $(window).height() + 100) ) {
            var source = $(this).data('src');
            $(this).attr('src', source);
            $(this).removeAttr('data-src');
        }
    })
});

function subscribeEmail() {
	var email = $('.subscribeEmail').val();
	if (email == '') {
              return;
	}
	if (!validateEmail(email)) {
	    return;
	}
	$.ajax({
	   type: "POST",
	   url: "https://docs.google.com/forms/d/e/1FAIpQLSckWaFo0IxkU-5sE1vGc_fswBmZeq0X2qYGthd1yRV0PuMSyQ/formResponse",
	   data: $('form.mG61Hd').serialize(),
	     success: function(msg){
		    console.log("Success");
	     },
	     error: function(){
	     	$('.quantumWizTextinputPaperinputInput').val('');
	     	$('#subsMsgDiv').show();
	     }
	    });
}

function removeBookmarkURL(pageURL) {
	pageURL = pageURL.trim();
	var log = getCookieValue('log');
	if (log != '') {
		var url1 = apiURLUser + 'bookmarkRecipe&remove=Y&token=' + escape(log) + '&url=' + escape(pageURL);
		$.ajax({
			type: 'GET',
			url: url1,
			dataType: "json",
			success: function(temp1) {
				if (temp1.name != '') {
					// User is logged in
					displayBookMark(temp1.bookmarks, pageURL);
				}
			},
		    error: function(xhr) {
		    	
		    }
		});
	}
}

function bookmarkURL(pageURL) {
	activityType = 'bookmark';
	pageURL = pageURL.trim();
	pageURLGlobal = pageURL;
	var log = getCookieValue('log');
	if (log != '') {
		var url1 = apiURLUser + 'bookmarkRecipe&token=' + escape(log) + '&url=' + escape(pageURL);
		$.ajax({
			type: 'GET',
			url: url1,
			dataType: "json",
			success: function(temp1) {
				if (temp1.name != '') {
					// User is logged in
					displayBookMark(temp1.bookmarks, pageURL);
					$('#messagePopupBkm').html("<p>Successfully bookmarked the recipe. You can view the bookmarks under <a href='signin.html'>Membership</a> tab.</p><div><a href='javascript:closePopupMessageBM()'>X</a></div>");
					openSuddenPopup();
				} else {
					openSuddenPopup();
				}
			},
		    error: function(xhr) {
		    	
		    }
		});
	} else {
		openSuddenPopup();
	}
}

function closePopupMessageBM() {
	$('#messagePopupBkm').hide();
}

function openSuddenPopup() {
	$('#messagePopupBkm').show();
	try {
		$('#messagePopupBkm').find('input:text').val('');
		$('#messagePopupBkm').find('input:password').val('');
	} catch (e) {}
}

function getBookMarkData(pageURL) {
	pageURL = pageURL.trim();
	var log = getCookieValue('log');
	if (log != '') {
		var url1 = apiURLUser + 'getUserData&token=' + escape(log);
		$.ajax({
			type: 'GET',
			url: url1,
			dataType: "json",
			success: function(temp1) {
				displayBookMark(temp1.bookmarks, pageURL);
			},
		    error: function(xhr) {
		    	
		    }
		});
	} else {
		$('#bookmark').html('<a onclick="ga(\'send\', \'event\', \'Bookmark\', \'Bookmark: Add\', this.href);"  title="Add recipe to Bookmark" href="javascript:bookmarkURL(\'' + pageURL + '\')">+ Bookmark</a>');
		$('#loggedInFeatures').show();
	}
}

function displayBookMark(bookmarks, pageURL) {
	pageURL = pageURL.trim();
	if (bookmarks.toString().indexOf(pageURL) > -1) {
		$('#bookmark').html('<a onclick="ga(\'send\', \'event\', \'Bookmark\', \'Bookmark: Remove\', this.href);"  title="Remove recipe from Bookmark" href="javascript:removeBookmarkURL(\'' + pageURL + '\')">&#10004; Bookmark</a>');
	} else {
		$('#bookmark').html('<a onclick="ga(\'send\', \'event\', \'Bookmark\', \'Bookmark: Add\', this.href);"  title="Add recipe to Bookmark" href="javascript:bookmarkURL(\'' + pageURL + '\')">+ Bookmark</a>');
	}
	$('#loggedInFeatures').show();
}

function loginStatusToken(tokenVal) {
	$('#loadingAccountPage').hide();
	if (tokenVal != '') {
		var url1 = apiURLUser + 'getUserData&token=' + escape(tokenVal);
		$.ajax({
			type: 'GET',
			url: url1,
			dataType: "json",
			success: function(temp1) {
				if (temp1.name == '') {
					$('#loginRegisterPage').show();
					$('#signedInAccount').hide();
				} else {
					$('#userNameTop').html("Welcome " + temp1.name);
					$('#loginRegisterPage').hide();
					$('#signedInAccount').show();
					accountActivity(1);
				}
			},
		    error: function(xhr) {
		    	
		    }
		});
	} else {
		$('#loginRegisterPage').show();
		$('#signedInAccount').hide();
	}
}

function loginStatus() {
	try {
		var log = getCookieValue('log');
		loginStatusToken(log);
	} catch(e) {} 
}


function enterPressCheckSignin(B){
    var A;
    if(window.event){
        A=window.event.keyCode;
    }else{
        A=B.which;
    }
    if(A==13){
        signInWithSP();
    }else{
    	$('#signInError').html('');
        return "false";
    }
}

function signInWithSP(){
	var emailr = document.getElementById('emails').value;
	var passr = document.getElementById('pass').value;
	if (emailr == '') {
		$('#signInError').removeClass('subsMsgDivSuccess');
		$('#signInError').addClass('subsMsgDivFail');
		$('#signInError').html("Please enter Email Id (Login ID).");
		return;
	}
	if (passr == '') {
		$('#signInError').removeClass('subsMsgDivSuccess');
		$('#signInError').addClass('subsMsgDivFail');
		$('#signInError').html("Please enter Password.");
		return;
	}
	var url1 = apiURLUser + 'signIn&uid=' + escape(emailr) + '&pass=' + escape(passr);
	$.ajax({
		type: 'GET',
		url: url1,
		dataType: "json",
		success: function(temp1) {
			$('.memberLoginPage').find('input:text').val('');
			$('.memberLoginPage').find('input:password').val('');
			if(temp1.message == 'SUCCESS') {
				setCookieValue('log', temp1.token);
				loginStatusToken(temp1.token);
			} else {
				$('#signInError').removeClass('subsMsgDivSuccess');
				$('#signInError').addClass('subsMsgDivFail');
				$('#signInError').html("Unable to login, please check your login credentials or activate your account by verifying your email address.");
			}
		},
	    error: function(xhr) {
	    	$('.memberLoginPage').find('input:text').val('');
			$('.memberLoginPage').find('input:password').val('');
	        $('#signInError').removeClass('subsMsgDivSuccess');
			$('#signInError').addClass('subsMsgDivFail');
			$('#signInError').html("Unable to login, please check your login credentials or activate your account by verifying your email address.");
	    }
	});
}


function enterPressCheckRegister(B){
    var A;
    if(window.event){
        A=window.event.keyCode;
    }else{
        A=B.which;
    }
    if(A==13){
        registerWithSP();
    }else{
    	$('#registerError').html('');
        return "false";
    }
}

function registerWithSP() {
	var emailr = document.getElementById('emailr').value;
	var passr = document.getElementById('passr').value;
	var namer = document.getElementById('namer').value;
	if (emailr == '') {
		$('#registerError').removeClass('subsMsgDivSuccess');
		$('#registerError').addClass('subsMsgDivFail');
		$('#registerError').html("Please enter valid Email Id.");
		return;
	} else {
		if (!validateEmail(emailr)) {
			$('#registerError').removeClass('subsMsgDivSuccess');
			$('#registerError').addClass('subsMsgDivFail');
			$('#registerError').html("Please enter valid Email Id.");
			return;
		}
	}
	if (passr == '') {
		$('#registerError').removeClass('subsMsgDivSuccess');
		$('#registerError').addClass('subsMsgDivFail');
		$('#registerError').html("Please enter Password.");
		return;
	}
	if (namer == '') {
		$('#registerError').removeClass('subsMsgDivSuccess');
		$('#registerError').addClass('subsMsgDivFail');
		$('#registerError').html("Please enter Name.");
		return;
	}
	
	var url1 = apiURLUser + 'register&uid=' + escape(emailr) + '&pass=' + escape(passr) + '&name=' + escape(namer);
	$.ajax({
		type: 'GET',
		url: url1,
		dataType: "json",
		success: function(temp1) {
			$('.memberLoginPage').find('input:text').val('');
			$('.memberLoginPage').find('input:password').val('');
			if(temp1.message == 'SUCCESS') {
				$('#registerError').addClass('subsMsgDivSuccess');
				$('#registerError').removeClass('subsMsgDivFail');
				$('#registerError').html("You are successfully registered, please check your registered email to verify your account.");
			} else if (temp1.message == 'PRESENT') {
				$('#registerError').addClass('subsMsgDivSuccess');
				$('#registerError').removeClass('subsMsgDivFail');
				$('#registerError').html("You are already registered with us, please check your registered email to verify your account or try Sign In with your existing login details.");
			} else {
				$('#registerError').removeClass('subsMsgDivSuccess');
				$('#registerError').addClass('subsMsgDivFail');
				$('#registerError').html("Error occured, please try again later. If error exists please contact us at <a href='mailto:contact@spicyworld.in'>contact@spicyworld.in</a>");
			}
		},
	    error: function(xhr) {
	    	$('.memberLoginPage').find('input:text').val('');
			$('.memberLoginPage').find('input:password').val('');
	        $('#registerError').removeClass('subsMsgDivSuccess');
			$('#registerError').addClass('subsMsgDivFail');
			$('#registerError').html("Error occured, please try again later. If error exists please contact us at <a href='mailto:contact@spicyworld.in'>contact@spicyworld.in</a>");
	    }
	});
}


function whatToCookLoad(input, count) {
	var itemDivID = '';
	if (input == 1) {
		itemDivID = $('#' + count + '_top');
		if (itemDivID.attr('class').indexOf('selectedItemData') > -1) {
			itemDivID.removeClass('selectedItemData');
		} else {
			itemDivID.addClass('selectedItemData');
		}
	} else {
		itemDivID = $('#' + count + '_bot');
		if (itemDivID.attr('class').indexOf('selectedItemDataS') > -1) {
			itemDivID.removeClass('selectedItemDataS');
		} else {
			itemDivID.addClass('selectedItemDataS');
		}
	}
	$('.whatErrorMessage').hide();
}

function resetSelection() {
	$('.inS').removeClass('selectedItemDataS');
	$('.in').removeClass('selectedItemData');
	$('.whatErrorMessage').hide();
	document.getElementById('recoDataSection').innerHTML = '';
}

function whatICanCook() {
	var datas = $('.selectedItemData > .leftItemData');
	var datasS = $('.selectedItemDataS > .leftItemData');
	var dataString = '', dataStringS = '';
	if (datas.length > 0) {
		for (var i=0; i<datas.length; i++) {
			if (i == 0) {
				dataString = datas[i].id;
			} else {
				dataString = dataString + '##' + datas[i].id;
			}
		}
	} else {
		$('.whatErrorMessage').show();
		return;
	}
	if (datasS.length > 0) {
		for (var i=0; i<datasS.length; i++) {
			if (i == 0) {
				dataStringS = datasS[i].id;
			} else {
				dataStringS = dataStringS + '##' + datasS[i].id;
			}
		}
	} else {
		$('.whatErrorMessage').show();
		return;
	}
	$('.whatErrorMessage').hide();
	$('#searchCookingBtn').html('Searching ...');
	var url1 = apiURL + 'getCookingRecommendations&small=' + escape(dataStringS) + '&top=' + escape(dataString);
	$.ajax({
		type: 'GET',
		url: url1,
		dataType: "json",
		success: function(temp1) {
			var dataStr = '', classStr = 'left';
			if (temp1.data && temp1.data.length > 0) {
				for (var i=0;i<temp1.data.length; i++) {
					if (i==0) {
						classStr = "left";
					} else if (i==1) {
						classStr = "middle";
					} else {
						classStr = "right";
					}
					dataStr += "<div class=\"" + classStr + "\"><a onclick=\"ga('send', 'event', 'Recommendation', 'Recommendation: result', this.href);\" title=\"" + temp1.data[i].title + "\" alt=\"" + temp1.data[i].title + "\" href=\"" + temp1.data[i].url + "\"><img title=\"" + temp1.data[i].title + "\" alt=\"" + temp1.data[i].title + "\" src=\"" + temp1.data[i].pic + "\"/></a><div class=\"title\"><a onclick=\"ga('send', 'event', 'Recommendation', 'Recommendation: result', this.href);\" title=\"" + temp1.data[i].title + "\" alt=\"" + temp1.data[i].title + "\" href=\"" + temp1.data[i].url + "\">" + temp1.data[i].title + "</a></div></div>";
				}
			} else {
				dataStr = "Sorry we could not find any recipes.";
			}
			document.getElementById('recoDataSection').innerHTML = "<div class=\"middleBottom\">" + dataStr + "</div>";
			$('#searchCookingBtn').html('Start Cooking');
		},
	    error: function(xhr) { // if error occured
	        $('#searchCookingBtn').html('Start Cooking');
	    }
	});
}

function enterPressCheckSubscribe(B){
    var A;
    if(window.event){
        A=window.event.keyCode;
    }else{
        A=B.which;
    }
    if(A==13){
        subscribeEmail();
    }else{
    	$('#subsMsgDiv').removeClass('subsMsgDivFail');
    	$('#subsMsgDiv').removeClass('subsMsgDivSuccess');
        return "false";
    }
}

function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

function calculateViews(pageURLTmp) {
	/*var viewTxt = "View";
	var url = apiURL + 'getPageViews&url=' + pageURLTmp;
	$.getJSON(url, function(json) { 
   		if (json == "1") {
			viewTxt = "View";
   		} else if (json == "0") {
   			viewTxt = "View";
   		} else {
			viewTxt = "Views";
   		}
   		$('#viewsCount').html("(" + json + " " + viewTxt + ")");
		$('#viewsCount').show();
   });
	getBookMarkData(pageURLTmp);
	getContentRating(pageURLTmp);*/
	
}

var openCloseMenuFlag = 'N';
function loadMobileMenu() {
	if (openCloseMenuFlag == 'N') {
		$('.mobileSpecificClass').attr('style','display:block !important');
		$('.mobileMenuLink').addClass('mobileMenuLinkSelected');
		$('.lineMenu').addClass('lineMenuSelected');
		openCloseMenuFlag = 'Y';
	} else {
		$('.mobileSpecificClass').attr('style','display:none !important');
		$('.mobileMenuLink').removeClass('mobileMenuLinkSelected');
		$('.lineMenu').removeClass('lineMenuSelected');
		openCloseMenuFlag = 'N';
	}
	ga('send', 'event', 'Site Menu', 'Site Menu Mobile: Mobile Menu', location.href);
}

function loadRecipePopup() {
	$("#citydrop").hide();
	$("#recipeClick").mouseover(function () {
	    $("#citydrop").slideDown('slow');
	    toggleMenuHighlight(0);
	});
	$("#popup-link").mouseleave(function () {
	    $("#citydrop").slideUp('slow');
	    toggleMenuHighlight(1);
	});
}


function navigateMenu() {
	var url = document.getElementById('headerMenuShall').value;
	var page = url.replace(".html", "");
	try {
		ga('send', 'event', 'Site Menu', 'Site Menu: ' + page.toUpperCase(), url);
	} catch (Err) {}
	location.href = url;
}

function loadTravelPage() {
	$('.flexslider').flexslider({
	    animation: "slide"
	});
}

function fixedMenu() {
	loadRecipePopup();
	$(window).bind('scroll', function() {
         if ($(window).scrollTop() > 150) {
         	$("#siteLogoMenu").show(300);
         	$('.headerMenu').addClass('fixed');
         	//$('.searchPositionTop').removeClass('searchLeft');
         	//$('.searchPositionTop').addClass('searchWhileNav');
         } else {
         	$("#siteLogoMenu").hide(300);
            $('.headerMenu').removeClass('fixed');
            //$('.searchPositionTop').addClass('searchLeft');
            //$('.searchPositionTop').removeClass('searchWhileNav');
         }
    });
    /*
    var popupmsg = getCookieValue('popupvi2');
    if (popupmsg != 'done') {
    	$('#popupMessageSpecial').show();
    	setCookieValue('popupvi2', 'done');
    } else {
    	$('#popupMessageSpecial').html('');
    }
    */
}

/*
function closePopupMessageBMP() {
	setCookieValue('popupvi2', 'done');
	$('#popupMessageSpecial').hide();
	$('#popupMessageSpecial').html('');
}
*/

function startTagPage() {
	$("#wordcloud1").awesomeCloud({
		"size" : {
			"grid" : 50,
			"normalize" : true
		},
		"options" : {
			"color" : "random-dark",
			"rotationRatio" : 0.2,
			"printMultiplier" : 3.5,
			"sort" : "random"
		},
		"font" : "oblique, 'Times New Roman', Times, serif",
		"shape" : "circle"
	});
}

function initFancy() {
	initFancyAll();
}


function initFancyAll() {
	$("a.group1").fancybox({
		'transitionIn'	:	'elastic',
		'transitionOut'	:	'elastic',
		'speedIn'		:	300, 
		'speedOut'		:	200, 
		'overlayShow'	:	true
	});
}

function loadSlideShow() {
    $('#ei-slider').eislideshow({
		animation			: 'center',
		autoplay			: true,
		slideshow_interval	: 3000,
		titlesFactor		: 0
    });
}

// Search Code Starts
var criteriaData = '';
var globalTemplate = '<div class="recipeListPageItem##RIGHT_CLASS##"><div class="recipeListPageItemLeft"><a alt="##TITLE##" title="##TITLE##" href="##URL##.html"><img title="##TITLE##" alt="##TITLE##" src="##PIC##"/></a><div class="##TYPE_CLASS## itemTypeLabel">##TYPE_DESC##</div></div><div class="recipeListPageItemRight"><div class="title"><a alt="##TITLE##" title="##TITLE##" href="##URL##.html">##TITLE##</a></div><div class="desc">##DESC##</div></div></div>';
var dataFile = '';

function populateData(dataElement) {
	var resultCount = 25;
	var top10Txt = "";
	var htmlForm = '<div class="recipePage"><div>';
	if (dataElement.length > 0) {
		if (dataElement.length > resultCount) {
			resultCount = 25;
		} else {
			resultCount = dataElement.length;
		}
		for (var i=0; i<resultCount; i++) {
			if (i % 2 == 0) {
				htmlForm += "<div class='landingRow'>";
			}
			htmlForm += populateSearchResult(dataElement[i], i);
			if (i % 2 == 1 || i == (resultCount - 1)) {
				htmlForm += "</div>";
			}
		}
		htmlForm += '</div></div>';
		if (resultCount > 25) {
			top10Txt = "<br/><span class='searchNote'><sup>*</sup>Displaying top 25 results</span>";
		}
		htmlForm = "<div class='searchHeader'>Search results on <b><i>" + criteriaData + "</i></b> " + top10Txt + "</div>" + htmlForm;
		$('#middleDataSearchSection').html(htmlForm);
	} else {
		$('#searchMdl').html('');
		$('#searchMdl').addClass('f30px');
		$('#searchMdl').html('No recipes or posts found with the entered criteria, please try again with different criteria (e.g. Chicken Curry).');
	}
}

function searchForRecipe() {
	var dataURL = getQueryVariable('criteria').trim();
	if (dataURL != '') {
		$('#searchBox').placeholder = dataURL;
		criteriaData = unescape(dataURL)
		criteriaData = criteriaData.toLowerCase();
		searchForRecipeNew();
	} else {
		$('#searchMdl').addClass('f30px');
		$('#searchMdl').html('Incorrect search critera, please try again.');
	}
}
// Search Code Ends

function accountActivity(pos){
	for (var i=1; i<4; i++) {
		if (i == pos) {
			$('#account_data_' + i).show();
			$('#account_' + i).addClass('selectedItem');
		} else {
			$('#account_data_' + i).hide();
			$('#account_' + i).removeClass('selectedItem');
		}
	}
	if (pos == 2) {
		var url1 = apiURLUser + 'getMyBookmarks&token=' + getCookieValue('log');
		$.ajax({
			type: 'GET',
			url: url1,
			dataType: "json",
			success: function(temp1) {
				var dataStr = '', classStr = 'left';
				if (temp1.data && temp1.data.length > 0) {
					for (var i=0;i<temp1.data.length; i++) {
						if (i%3 == 0) {
							classStr = "left";
						} else if (i%3 == 1) {
							classStr = "middle";
						} else {
							classStr = "right";
						}
						dataStr += "<div class=\"" + classStr + "\"><a onclick=\"ga('send', 'event', 'Bookmark', 'Bookmark: result', this.href);\" title=\"" + temp1.data[i].title + "\" alt=\"" + temp1.data[i].title + "\" href=\"" + temp1.data[i].url + "\"><img title=\"" + temp1.data[i].title + "\" alt=\"" + temp1.data[i].title + "\" src=\"" + temp1.data[i].pic + "\"/></a><div class=\"title\"><a onclick=\"ga('send', 'event', 'Recommendation', 'Recommendation: result', this.href);\" title=\"" + temp1.data[i].title + "\" alt=\"" + temp1.data[i].title + "\" href=\"" + temp1.data[i].url + "\">" + temp1.data[i].title + "</a></div></div>";
					}
				}
				if (dataStr == '') {
					dataStr = "There are no bookmarked recipes yet.";
				}
				document.getElementById('account_data_2').innerHTML = "<div class='cookyourself'><div class=\"middleBottom\">" + dataStr + "</div></div>";
			},
		    error: function(xhr) { // if error occured
		        
		    }
		});
	} else if (pos == 3) {
		var log = getCookieValue('log');
		if (log != '') {
			var url1 = apiURLUser + 'getUserData&token=' + escape(log);
			$.ajax({
				type: 'GET',
				url: url1,
				dataType: "json",
				success: function(temp1) {
					if (temp1.name != '') {
						// User is logged in
						loadAccSettingsData(temp1);
					}
				},
			    error: function(xhr) {
			    	
			    }
			});
		}
	}
}

function loadAccSettingsData(temp1) {
	var newsFlag = '';
	if (temp1.newsletter == 'N') {
		newsFlag = '<a onclick=\'ga("send", "event", "Account Settings", "Account Settings: Subscribe", this.href);\' href="javascript:subscribeUnsubProf(\'N\')">Subscribe</a>';
	} else {
		newsFlag = '<a onclick=\'ga("send", "event", "Account Settings", "Account Settings: Unsubscribe", this.href);\' href="javascript:subscribeUnsubProf(\'Y\')">Unsubscribe</a>';
	}
	$('#account_data_3').html('<div class="cookyourself settingsAnchors" style="padding-top:26px;"><table align="center" width="60%">'
		+ '<tr><td>Subscribe to Spicy World Newsletters</td><td>' + newsFlag + '</td></tr>'
		+ '<tr><td>Download Android Beta App</td><td><a onclick=\'ga("send", "event", "Account Settings", "Account Settings: Mobile App Download", this.href);\' href="http://tomcat-spicyworld.rhcloud.com/app-download/spicyworld.apk">Download</a></td></tr>'
		+ '<tr><td>Download Cook Book</td><td><a onclick=\'ga("send", "event", "Account Settings", "Account Settings: Cook Book Download", this.href);\' href="Spicy-World-Cook-Book.pdf">Download</a></td></tr>'
		+ '</table></div>');
	$('#account_data_3').show();
}

function subscribeUnsubProf(flag) {
	var log = getCookieValue('log');
	if (log != '') {
		var url1 = apiURLUser + 'subscribeUnsubProf&token=' + escape(log) + '&flag=' + flag;
		$.ajax({
			type: 'GET',
			url: url1,
			dataType: "json",
			success: function(temp1) {
				if (temp1.name != '') {
					loadAccSettingsData(temp1);
				}
			},
		    error: function(xhr) {
		    	
		    }
		});
	}
}

function getContentRating(pageURL) {
	var log = getCookieValue('log');
	var url1 = apiURLUser + 'getContentRating&url=' + escape(pageURL);
	if (log != '') {
		url1 = url1 + '&token=' + escape(log);
	}
	$.ajax({
		type: 'GET',
		url: url1,
		dataType: "json",
		success: function(temp1) {
			loadContentRating(temp1, pageURL);
			
		},
	    error: function(xhr) {
	    	
	    }
	});
}

function loadContentRating(temp1, pageURL) {
	var dataDisplay = "";
	if (temp1.userrating == '0') {
		//No user rating plot all blank stars
		for (var i=0; i<5; i++) {
			if (temp1.actualrating != 0 && temp1.actualrating >= (i+1)) {
				dataDisplay += '<a title="' + (i+1) + '" href="javascript:rateContent(' + (i+1) + ',\'' + pageURL + '\')"><img src="images/full-star.png"/></a>';
			} else if (temp1.actualrating != 0 && temp1.actualrating > i && temp1.actualrating < (i+1)) {
				dataDisplay += '<a title="' + (i+1) + '" href="javascript:rateContent(' + (i+1) + ',\'' + pageURL + '\')"><img src="images/half-star.png"/></a>';
			} else {
				dataDisplay += '<a title="' + (i+1) + '" href="javascript:rateContent(' + (i+1) + ',\'' + pageURL + '\')"><img src="images/blank-star.png"/></a>';
			}
		}
	} else {
		for (var i=0; i<5; i++) {
			if (i < temp1.userrating) {
				dataDisplay += '<a title="' + (i+1) + '" href="javascript:rateContent(' + (i+1) + ',\'' + pageURL + '\')"><img src="images/full-star.png"/></a>';
			} else {
				dataDisplay += '<a title="' + (i+1) + '" href="javascript:rateContent(' + (i+1) + ',\'' + pageURL + '\')"><img src="images/blank-star.png"/></a>';	
			}
		}
	}
	if (temp1.totalrating != "0") {
		var reviewTxt = "review";
		if (temp1.totalrating > 1) {
			reviewTxt += "s";
		}
		dataDisplay += "<div property='aggregateRating' typeof='AggregateRating;' style='display: inline-block !important;'><label><span property='ratingValue'>" + temp1.actualrating + "</span> of 5 (<span property='reviewCount'>" + temp1.totalrating + "</span> " + reviewTxt + ")</label></div>";
	}
	$('#rating').html(dataDisplay);
	$('#loggedInFeatures').show();
}

var globalRating = '';
function rateContent(rating, pageURL) {
	activityType = 'rate';
	globalRating = rating;
	pageURLGlobal = pageURL;
	var log = getCookieValue('log');
	if (log == '' || log == null) {
		log = "guest-" + guid();
		setCookieValue('log', log);
	}
	if (log != '') {
		var url1 = apiURLUser + 'rateContent&url=' + escape(pageURL) + '&token=' + escape(log) + '&rating=' + rating;
		$.ajax({
			type: 'GET',
			url: url1,
			dataType: "json",
			success: function(temp1) {
				loadContentRating(temp1, pageURL);
				if (temp1.userrating == '0') {
					openSuddenPopup();
				}
			},
		    error: function(xhr) {
		    	
		    }
		});
		ga('send', 'event', 'Rate Content', 'Rate Content: Rated', 'http://spicyworld.in/' + pageURL);
	} else {
		openSuddenPopup();
		ga('send', 'event', 'Rate Content', 'Rate Content: Login Needed', 'http://spicyworld.in/' + pageURL);
	}
}


function enterPressCheckSigninPopup(B){
    var A;
    if(window.event){
        A=window.event.keyCode;
    }else{
        A=B.which;
    }
    if(A==13){
        loginFromPopup();
    }else{
    	$('#signInError').html('');
        return "false";
    }
}

function loginFromPopup(){
	var emailr = document.getElementById('uid').value;
	var passr = document.getElementById('pwd').value;
	if (emailr == '') {
		$('#signInError').removeClass('subsMsgDivSuccess');
		$('#signInError').addClass('subsMsgDivFail');
		$('#signInError').html("Please enter Email Id (Login ID).");
		return;
	}
	if (passr == '') {
		$('#signInError').removeClass('subsMsgDivSuccess');
		$('#signInError').addClass('subsMsgDivFail');
		$('#signInError').html("Please enter Password.");
		return;
	}
	var url1 = apiURLUser + 'signIn&uid=' + escape(emailr) + '&pass=' + escape(passr);
	$.ajax({
		type: 'GET',
		url: url1,
		dataType: "json",
		success: function(temp1) {
			$('#messagePopupBkm').find('input:text').val('');
			$('#messagePopupBkm').find('input:password').val('');
			if(temp1.message == 'SUCCESS') {
				setCookieValue('log', temp1.token);
				if (activityType == 'bookmark') {
					bookmarkURL(pageURLGlobal);
				} else if (activityType == 'rate') {
					rateContent(globalRating, pageURLGlobal);
				}
				closePopupMessageBM();
			} else {
				$('#signInError').removeClass('subsMsgDivSuccess');
				$('#signInError').addClass('subsMsgDivFail');
				$('#signInError').html("Unable to login, please check your login credentials or activate your account by verifying your email address.");
			}
		},
	    error: function(xhr) {
	    	$('#messagePopupBkm').find('input:text').val('');
			$('#messagePopupBkm').find('input:password').val('');
	        $('#signInError').removeClass('subsMsgDivSuccess');
			$('#signInError').addClass('subsMsgDivFail');
			$('#signInError').html("Unable to login, please check your login credentials or activate your account by verifying your email address.");
	    }
	});
	ga('send', 'event', 'Sign In', 'Sign In: From Popup', 'http://spicyworld.in/' + pageURL);
}

var displaySocialFlag = false;
function toggleSocial() {
	$( ".social-icon" ).slideToggle("slow");
	if (displaySocialFlag) {
		displaySocialFlag = false;
		$('.social-plus img').attr('src','images/social-icon/plus.png');
	} else {
		displaySocialFlag = true;
		$('.social-plus img').attr('src','images/social-icon/minus.png');
	}
}
