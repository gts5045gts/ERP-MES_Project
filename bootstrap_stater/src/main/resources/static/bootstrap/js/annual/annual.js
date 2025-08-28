// 그리드
let grid;

document.addEventListener('DOMContentLoaded', function() {
	let currentPage = 0;
   	let totalPages = 1; // 초기값
	
    const columns = [
        { header: '사원번호', name: 'empId', sortable: true},
        { header: '이름', name: 'empName', sortable: true},
        { header: '부서', name: 'depName', filter: 'select' },
        { header: '직급', name: 'empPos', filter: 'select' },
        { header: '입사일', name: 'joinDate', sortable: true},
        { header: '총 휴가일수', name: 'annTotal', sortable: true},
        { header: '사용 휴가일수', name: 'annUse', sortable: true},
        { header: '잔여 휴가일수', name: 'annRemain', sortable: true},
        { header: '휴가 소멸일', name: 'annExpire', sortable: true }
    ];

	grid = new tui.Grid({
        el: document.getElementById('grid'),
        data: [],
        columns: columns,
        bodyHeight: 400,
        rowHeaders: ['rowNum'],
        scrollX: false,
        emptyMessage: '조회결과가 없습니다.'
    });

   

    function loadPage(page) {
        fetch(`/attendance/annListAll/2025?page=${page}&size=20`)
            .then(res => res.json())
            .then(res => {
                totalPages = res.totalPages;
                currentPage = res.page;

                if (page === 0) {
                    grid.resetData(res.data);
                } else {
                    grid.appendRows(res.data);
                }
            });
    }

    // 초기 데이터 로딩
    loadPage(0);

    // 무한스크롤 이벤트
	grid.on('scrollEnd', () => {
		if (currentPage + 1 < totalPages) {
		    loadPage(currentPage + 1);
		}
	});
});

//연차 검색창
$('#AnnSearch').on('keyup', function() {
	const keyword = $(this).val().trim();
	$.ajax({
		url: '/attendance/annSearch',
		method: 'GET',
		data: { keyword: keyword},
		success: function(data) {
			if (data.length === 0) {
				grid.resetData([]); // 검색 결과 없음
			} else {
                grid.resetData(data); // Grid API로 데이터 업데이트
            }
		},
	});
});

// 차트 
const chartEl = document.getElementById('chart-area');

fetch(`/attendance/annualList/chart`)
	.then(res => res.json())
	.then(data => {
		const annTotal = Number(data.annTotal) || 0;
		const annUse = Number(data.annUse) || 0;
		const annPercent = annTotal === 0 ? 0 : Math.round((annUse / annTotal) * 100);
		
		const chartData = {
			series: [
				{ 
					name: '사용률', 
					data: [{value: annPercent}] 
				}
			]
		};

		const options = {
			chart: {
				width: 270,
				height: 300
			},
			gaugeScale: { min: 0, max: 100 },
			exportMenu: {
				visible: false
			},
			series: {
				solid: true,
				dataLabels: { 
					visible: true, 
					offsetY: -27, formatter: (value) => `사용률 ${value.value}%` },
			},
			theme: {
				circularAxis: {
					lineWidth: 0,
					strokeStyle: 'rgba(0, 0, 0, 0)',
					tick: {
						lineWidth: 0,
						strokeStyle: 'rgba(0, 0, 0, 0)',
					},
					label: {
						color: 'rgba(0, 0, 0, 0)',
					},
				},
				series: {
					dataLabels: {
						fontSize: 20,
						fontFamily: 'Impact',
						fontWeight: 500,
						color: '#00a9ff',
						textBubble: {
							visible: false,
						},
					},
				},
			},
		};

		toastui.Chart.gaugeChart({ el: chartEl, data: chartData, options });
	});

// 오늘의 연차사원
document.addEventListener('DOMContentLoaded', function() {
	const table = document.getElementById('todayAnnTable');

	fetch('/attendance/todayAnn')
		.then(response => response.json())
		.then(data => {
			if (data.length === 0) {
				table.innerHTML = '<tr><td colspan="5" class="text-center">오늘 연차 사원이 없습니다.</td></tr>';
				return;
			}

			let html = `<thead>
                            <tr>
                                <th>사원ID</th>
                                <th>이름</th>
                                <th>부서</th>
                                <th>직급</th>
                                <th>휴가종류</th>
                            </tr>
                        </thead><tbody>`;

			data.forEach(emp => {
				html += `<tr>
                            <td>${emp.empId}</td>
                            <td>${emp.empName}</td>
                            <td>${emp.depName}</td>
                            <td>${emp.empPos}</td>
                            <td>${emp.annType}</td>
                         </tr>`;
			});

			html += '</tbody>';
			table.innerHTML = html;
		})
		.catch(err => console.error('오늘 연차 조회 실패', err));
});