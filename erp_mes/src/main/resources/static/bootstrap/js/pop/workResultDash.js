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
	
	// 빈도넛용
	var arcZero = d3.arc()
		.outerRadius(radius - 10)
		.innerRadius(radius / 2)
		.startAngle(0)
		.endAngle(2 * Math.PI); // 360도 전체

	g.append("path")
		.attr("d", d => d.data.value === 0 ? arcZero() : arc(d))
	    .style("fill", d => d.data.value === 0 ? "rgb(223 222 222)" : color(d.data.label));	

	g.append("text")
		.attr("transform", d => `translate(${arc.centroid(d)})`)
		.attr("dy", "0.35em")
		.style("text-anchor", "middle")
		.style("fill", "white")
		.style("font-weight", "bold")
		  .text(d => d.data.value === 0 ? "" : d.data.label);

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
	var completed = workOrders.filter(d => d.workOrderStatus === '검사대기' || d.workOrderStatus === '작업완료').length;
	var incomplete = total - completed;

	var data = [
		{ label: "완료", value: completed },
		{ label: "미완료", value: incomplete }
	];

	var color = d3.scaleOrdinal()
		.domain(["완료", "미완료"])
		.range(["#4caf50", "rgb(223 222 222)"]);

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
		.attr("transform", `translate(${width / 2},${height / 2 - 20})`);
		

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
		.attr("transform", d => "translate(" + arc.centroid(d) + ")")
		.attr("dy", "0.35em")
		.style("text-anchor", "middle")
		.style("fill", "white")
		.style("font-weight", "bold")
		.text(d => d.data.value === 0 ? "" : d.data.label);


	// 중앙 퍼센트 표시
	svg.append("text")
		.attr("text-anchor", "middle")
		.attr("dy", "0.35em")
		.style("font-size", "20px")
		.style("font-weight", "bold")
		.text(Math.round((completed / total) * 100) + "%");
}

// ======================== 설비별 막대 그래프 ============================================

const STORAGE_KEY = "cumulativeEquipment";
const Y_AXIS_MIN = 10;

function updateEquipmentChart(bomData = []) {

	if (!bomData || bomData.length === 0) {
		// 데이터 없으면 빈 차트만 표시
		drawChart([]);
		return;
	}

	const saved = JSON.parse(localStorage.getItem(STORAGE_KEY) || "{}");

	bomData.forEach(row => {
		const equip = row.equipmentNm;
		saved[equip] = (saved[equip] || 0) + 1; // 누적
	});

	// 로컬 스토리지 업데이트
	localStorage.setItem(STORAGE_KEY, JSON.stringify(saved));

	// 차트용 데이터 변환
	const data = Object.keys(saved).map(equip => ({
		equipment: equip,
		count: saved[equip]
	}));

	drawChart(data);
}
// 새로고침 시 저장된 값 복원 후 차트 표시
document.addEventListener("DOMContentLoaded", () => {
    const saved = JSON.parse(localStorage.getItem(STORAGE_KEY) || "{}");
    const data = Object.keys(saved).map(equip => ({
        equipment: equip,
        count: saved[equip]
    }));
    drawChart(data);
});

function drawChart(data) {
    const svg = d3.select("#equipmentChart");
    const margin = { top: 30, right: 30, bottom: 50, left: 80 };
    const width = +svg.attr("width") - margin.left - margin.right;
    const height = +svg.attr("height") - margin.top - margin.bottom;

    let g = svg.select("g.chart-group");
    if (g.empty()) {
        g = svg.append("g").attr("class", "chart-group")
            .attr("transform", `translate(${margin.left},${margin.top})`);
        g.append("g").attr("class", "x-axis").attr("transform", `translate(0,${height})`);
        g.append("g").attr("class", "y-axis");
        svg.append("text")
            .attr("class", "chart-title")
            .attr("x", width / 2 + margin.left)
            .attr("y", margin.top / 2)
            .attr("text-anchor", "middle")
            .style("font-size", "16px")
            .style("font-weight", "bold")
            .text("오늘자 설비별 사용 횟수");
    }

    const x = d3.scaleBand()
        .domain(data.map(d => d.equipment))
        .range([0, width])
        .padding(0.4);

    const currentMax = d3.max(data, d => d.count) || 0;
    const yMax = Math.max(currentMax, Y_AXIS_MIN);

    const y = d3.scaleLinear()
        .domain([0, yMax])
        .nice()
        .range([height, 0]);

	g.select(".x-axis")
		.call(d3.axisBottom(x))
		.selectAll("text")
		.attr("transform", "rotate(-30)")   // 45도 회전
		.style("text-anchor", "end")        // 오른쪽 끝 기준 정렬
		.style("font-size", "9px");        // 글자 조금 작게
    g.select(".y-axis").call(d3.axisLeft(y).ticks(5));

    const bars = g.selectAll(".bar").data(data, d => d.equipment);
    bars.join(
        enter => enter.append("rect")
            .attr("class", "bar")
            .attr("x", d => x(d.equipment))
            .attr("width", x.bandwidth())
            .attr("y", d => y(d.count))
            .attr("height", d => height - y(d.count))
            .attr("fill", "#ed7e31"),
        update => update
            .transition().duration(300)
            .attr("y", d => y(d.count))
            .attr("height", d => height - y(d.count)),
        exit => exit.remove()
    );

    const labels = g.selectAll(".label").data(data, d => d.equipment);
    labels.join(
        enter => enter.append("text")
            .attr("class", "label")
            .attr("x", d => x(d.equipment) + x.bandwidth() / 2)
            .attr("y", d => y(d.count) - 5)
            .attr("text-anchor", "middle")
            .style("font-size", "12px")
            .text(d => d.count + "회"),
        update => update
            .transition().duration(300)
            .attr("y", d => y(d.count) - 5)
            .text(d => d.count + "회"),
        exit => exit.remove()
    );
}