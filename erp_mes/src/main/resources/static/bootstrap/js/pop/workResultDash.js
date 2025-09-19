//=============== 도넛차트(불량률, 생산률) ======================================= 
function updateQuantityChart(workOrders) {
	var width = 230,
		height = 270,
		radius = Math.min(width, height) / 2;

	// 총 생산량과 불량 수량 계산
	var goodCount = 0;
	var defectCount = 0;

	workOrders.forEach(d => {
		goodCount += Number(d.goodQty) || 0;
		defectCount += Number(d.defectQty) || 0;
	});
	

	var data = [
		{ label: "불량률", value: defectCount },
		{ label: "생산률", value: goodCount }
	];

	var color = d3.scaleOrdinal()
		.domain(["불량률", "생산률"])
		.range(["rgb(7 75 125)", "rgb(62 172 223)"]);

	// 이전 차트 제거
	d3.select("#quantityChart").selectAll("*").remove();

	var pie = d3.pie()
		.sort(null)
		.value(d => d.value);

	var arc = d3.arc()
		.outerRadius(radius - 10)
		.innerRadius(radius / 2); // 도넛 차트

	var svg = d3.select("#quantityChart")
		.attr("width", width)
		.attr("height", height)
		.append("g")
		.attr("transform", `translate(${width / 2},${height / 2 - 20})`);

	// 제목
	svg.append("text")
		.attr("x", 0)
		.attr("y", radius + 30)
		.attr("text-anchor", "middle")
		.style("font-size", "16px")
		.style("font-weight", "bold")
		.text("생산 상태");

	// 파이 조각
	var g = svg.selectAll(".arc")
		.data(pie(data))
		.enter().append("g")
		.attr("class", "arc");

	g.append("path")
		.attr("d", arc)
		.style("fill", d => color(d.data.label));

	g.append("text")
		.attr("transform", d => `translate(${arc.centroid(d)})`)
		.attr("dy", "0.35em")
		.style("text-anchor", "middle")
		.style("fill", "white")
		.style("font-weight", "bold")
		.text(d => d.data.label);

	// 중앙 총 생산량 표시
	svg.append("text")
		.attr("text-anchor", "middle")
		.attr("dy", "0.35em")
		.style("font-size", "18px")
		.style("font-weight", "bold")
		.text("총 생산 " + goodCount + "개");
}


	
//=============== 도넛차트(전체진행률) ======================================= 
function updateProgressChart(workOrders) {
	var width = 230, height = 270, radius = Math.min(width, height) / 2;

	// 완료/미완료 계산
	var total = workOrders.length;
	var completed = workOrders.filter(d => d.workOrderStatus === '검사대기').length;
	var incomplete = total - completed;

	var data = [
		{ label: "완료", value: completed },
		{ label: "미완료", value: incomplete }
	];

	var color = d3.scaleOrdinal()
       .domain(["완료", "미완료"])
       .range(["#4caf50", "#ccc"]);

	d3.select("#progressChart").selectAll("*").remove(); // 이전 차트 제거

	
	var pie = d3.pie()
		.sort(null)
		.value(d => d.value);    	
		
	var arc = d3.arc()
		.outerRadius(radius - 10)
		.innerRadius(radius / 2);

	var svg = d3.select("#progressChart")
			.attr("width", width)
			.attr("height", height)
			.append("g")
			.attr("transform", `translate(${width/2},${height/2 - 20})`);
			
	// 제목 추가
	svg.append("text")
		.attr("x", 0)
		.attr("y", radius + 30)   // 도넛 아래에 위치
		.attr("text-anchor", "middle")
		.style("font-size", "16px")
		.style("font-weight", "bold")
		.text("작업 진행률");			

	var g = svg.selectAll(".arc")
		.data(pie(data))
		.enter().append("g")
		.attr("class", "arc");

	g.append("path")
		.attr("d", arc)
		.style("fill", d => color(d.data.label));

	g.append("text")
		.attr("transform", function(d) { return "translate(" + arc.centroid(d) + ")"; })
		.attr("dy", "0.35em")
		.style("text-anchor", "middle")
		.style("fill", "white")
		.style("font-weight", "bold")
		.text(d => d.data.label);


	// 중앙 퍼센트 표시
	svg.append("text")
		.attr("text-anchor", "middle")
		.attr("dy", "0.35em")
		.style("font-size", "20px")
		.style("font-weight", "bold")
		.text(Math.round((completed / total) * 100) + "%");
}

// ======================== 설비별 막대 그래프 ============================================
function updateEquipmentChart() {
	const rows = $('#workOrderBody tr');
	const equipmentData = {};


	rows.each(function() {
		const equip = $(this).data('equipment');
		const qty = parseInt($(this).data('goodqty')) || 0;
		if (!equipmentData[equip]) equipmentData[equip] = 0;
		equipmentData[equip] += qty;
	});
	

	const data = Object.keys(equipmentData).map(equip => ({
		equipment: equip,
		quantity: equipmentData[equip]
	}));

	// SVG 초기화
	const svg = d3.select("#equipmentChart");
	svg.selectAll("*").remove();

	const margin = { top: 30, right: 30, bottom: 30, left: 80 };
	const width = +svg.attr("width") - margin.left - margin.right;
	const height = +svg.attr("height") - margin.top - margin.bottom;

	const g = svg.append("g").attr("transform", `translate(${margin.left},${margin.top})`);

	// X축: 설비 이름
	const x = d3.scaleBand()
		.domain(data.map(d => d.equipment))
		.range([0, width])
		.padding(0.4);

	// Y축: 퍼센트 (0~100%)
	const y = d3.scaleLinear()
		.domain([0, 100])
		.range([height, 0]);

	// X축
	g.append("g")
		.attr("transform", `translate(0,${height})`)
	    .call(d3.axisBottom(x))
	    .selectAll("text")
	    .attr("transform", "rotate(0)") // 회전 제거
	    .style("text-anchor", "middle") // 가운데 정렬
	    .style("font-weight", "bold")   // 진하게
		.style("font-size", "12px")
	    .style("fill", "#666");         // 검정색

	// Y축 (퍼센트)
	g.append("g")
		.call(d3.axisLeft(y).ticks(10).tickFormat(d => d + "%"));

	// 제목
	svg.append("text")
		.attr("x", width / 2 + margin.left)
		.attr("y", margin.top / 2)
		.attr("text-anchor", "middle")
		.style("font-size", "16px")
		.style("font-weight", "bold")
		.text("당일 설비별 생산률(량)");

	// 막대
	g.selectAll(".bar")
		.data(data)
		.enter()
		.append("rect")
		.attr("class", "bar")
		.attr("x", d => x(d.equipment) + x.bandwidth() * 0.2) // 폭 60%로 가운데
	  	.attr("y", d => y(d.quantity))
	  	.attr("width", x.bandwidth() * 0.6) 
		.attr("height", d => height - y(Math.min(d.quantity, 100)))

	// 값 표시
	g.selectAll(".label")
		.data(data)
		.enter()
  		.append("text")
  		.attr("x", d => x(d.equipment) + x.bandwidth() / 2)
  		.attr("y", d => y(d.quantity) - 5) 
  		.attr("text-anchor", "middle")
  		.style("font-size", "12px") // 글자 크기 줄임
  		.text(d => d.quantity + "개");
		
}
