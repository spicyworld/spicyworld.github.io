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