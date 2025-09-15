//=============== 도넛차트(불량률, 생산률) ======================================= 
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
	.innerRadius(radius / 2);  // 도넛 차트 원하면 여기에 값 넣기

// svg 추가
var svg = d3.select("#quantityChart")
	.attr("width", width)
	.attr("height", height)
	.append("g")
	.attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

// 파이 조각 그룹
var g = svg.selectAll(".arc")
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


	
// 도넛 중앙에 총 생산량/불량률 표시
var totalValue = d3.sum(data, function(d) { return d.value; });
svg.append("text")
    .attr("text-anchor", "middle")
    .attr("dy", "0.35em")
    .style("font-size", "16px")
    .style("font-weight", "bold")
    .text("생산상태");

	
//=============== 도넛차트(전체진행률) ======================================= 
var data2 = [
    { label: "완료", value: 70 },
    { label: "미완료", value: 30 }
];

var color2 = d3.scale.ordinal()
    .domain(["완료","미완료"])
    .range(["#4caf50","#e0e0e0"]);

var svg2 = d3.select("#progressChart")
    .attr("width", width)
    .attr("height", height)
  .append("g")
    .attr("transform", "translate(" + width/2 + "," + height/2 + ")");

var g2 = svg2.selectAll(".arc")
    .data(pie(data2))
  .enter().append("g")
    .attr("class", "arc");

g2.append("path")
    .attr("d", arc)
    .style("fill", d => color2(d.data.label));

g2.append("text")
    .attr("transform", d => "translate(" + arc.centroid(d) + ")")
    .attr("dy", "0.35em")
    .style("text-anchor", "middle")
    .text(d => d.data.label);

// 중앙 퍼센트 표시
svg2.append("text")
    .attr("text-anchor", "middle")
    .attr("dy", "0.35em")
    .style("font-size", "18px")
    .style("font-weight", "bold")
    .text("진행률");	
