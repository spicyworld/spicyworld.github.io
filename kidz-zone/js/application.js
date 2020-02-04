function loadPage(page) {
	$('.nav-link').removeClass('active');
    $('#' + page).addClass('active');
    page = page.replace('-head', '');
	console.log("Loading " + page + ".html");
	$("#middle-data-container").load("pages/" + page + ".html");
}

var numbers = [
		{"key": "1", "val": "ONE"}, 
		{"key": "2", "val": "TWO"}
];