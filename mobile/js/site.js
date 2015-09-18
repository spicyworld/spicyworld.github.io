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