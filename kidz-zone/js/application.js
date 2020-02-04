var counter = 0;
var customData = [];
		
function loadPage(page) {
	$('.nav-link').removeClass('active');
    $('#' + page).addClass('active');
    page = page.replace('-head', '');
	console.log("Loading " + page + ".html");
	$("#middle-data-container").load("pages/" + page + ".html");
	$('.dim-areas').addClass('dim-areas');
}

function changeCounter(type) {
	if (type == 'prev' && counter > 0) {
		counter = counter - 1;
		loadData();
	} else if (type == 'next' && counter < customData.length) {
		counter = counter + 1;
		loadData();
	}
}

function loadData() {
  if (counter < customData.length && counter > -1) {
 	$('.display-key').html(customData[counter].key);
  	$('.display-val').html(customData[counter].val);  
  	$('.dim-areas').addClass('dim-areas');
  } else {
  	$('.dim-areas').removeClass('dim-areas');
  }
}