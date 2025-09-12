// 차트 크기 설정
var width = 200,
	height = 250,
	radius = Math.min(width, height) / 2;

// 색상 스케일
var color = d3.scale.ordinal()
	.domain(["불량", "정상", "재고요청"])
	.range(["#ff4d4f", "#a3c27b", "#ffc107"]);

// 데이터
var data = [
	{ label: "불량", value: 20 },
	{ label: "정상", value: 60 },
	{ label: "재고요청", value: 20 }
];

// pie 레이아웃
var pie = d3.layout.pie()
	.value(function(d) { return d.value; });

// arc 생성기
var arc = d3.svg.arc()
	.outerRadius(radius - 10)
	.innerRadius(0);  // 도넛 차트 원하면 여기에 값 넣기

// svg 추가
var svg = d3.select("#quantityChart")
	.attr("width", width)
	.attr("height", height)
	.append("g")
	.attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

// SVG 생성
var svg = d3.select("#quantityChart")
	.attr("width", width)
	.attr("height", height);

// 메인 그룹: 파이 차트용
var pieGroup = svg.append("g")
	.attr("transform", "translate(" + width / 2 + "," + (height / 2 - 10) + ")"); // 조금 위로 올려서 밑 제목 공간 확보

// 파이 조각 그룹
var g = pieGroup.selectAll(".arc")
	.data(pie(data))
	.enter().append("g")
	.attr("class", "arc");

// path
g.append("path")
	.attr("d", arc)
	.style("fill", function(d) { return color(d.data.label); })
	.transition()
	.duration(1000)
	.attrTween("d", function(d) {
		var i = d3.interpolate({ startAngle: 0, endAngle: 0 }, d);
		return function(t) { return arc(i(t)); };
	});

// 파이 안 텍스트
g.append("text")
	.attr("transform", function(d) { return "translate(" + arc.centroid(d) + ")"; })
	.attr("dy", "0.35em")
	.style("text-anchor", "middle")
	.text(function(d) { return d.data.label; });

// hover 효과
g.on("mouseover", function(d) {
	d3.select(this).select("path")
		.transition().duration(200)
		.attr("d", d3.svg.arc()
			.outerRadius(radius + 10)
			.innerRadius(0)(d))
		.style("fill", color(d.data.label));
}).on("mouseout", function(d) {
	d3.select(this).select("path")
		.transition().duration(200)
		.attr("d", arc)
		.style("fill", color(d.data.label));
	});

// 파이 밑 제목
svg.append("text")
	.attr("x", width / 2)
	.attr("y", height - 5) // SVG 맨 아래
	.attr("text-anchor", "middle")
	.style("font-size", "16px")
	.style("font-weight", "bold")
	.text("생산상태");
