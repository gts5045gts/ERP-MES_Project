document.addEventListener('DOMContentLoaded', function() {

	// 전역 변수: TUI Grid 인스턴스
	let defectGrid;

	// TUI Grid 초기화 (함수 호출 대신 직접 실행)
	defectGrid = new tui.Grid({
		el: document.getElementById('grid3'),
		data: {
			api: {
				readData: { url: '/defect/api/defect-history', method: 'GET' }
			},
			contentType: 'application/json'
		},
		columns: [
			{ header: 'No.', name: 'defectItemId', width: 60 },
			{ header: '유형', name: 'defectTypeName', width: 100 },
			{ header: '상세 사유', name: 'defectReason', width: 150 },
			{ header: '수량', name: 'defectQty', align: 'right', width: 80 },
			{ header: '제품/자재명', name: 'productNm', width: 150 },
			{ header: '등록자', name: 'empName', width: 120 },
			{ header: '발생일자', name: 'defectDate', width: 150 },
			{ header: '위치', name: 'defectLocationName', width: 100 }
		],
		rowHeaders: ['rowNum'],
		minBodyHeight: 200
	});

	function drawMonthlyChart(monthlyTrendData) {
		const ctx = document.getElementById('monthlyDefectChart').getContext('2d');
		// 데이터 구조에 따라 필드명 변경 필요 (예: MONTH, TOTAL_QTY)
		const labels = monthlyTrendData.map(d => d.MONTH); 
		const data = monthlyTrendData.map(d => d.TOTAL_QTY); 

		new Chart(ctx, {
			type: 'line',
			data: {
				labels: labels,
				datasets: [{
					label: '월별 불량 수량',
					data: data,
					borderColor: 'rgba(78, 115, 223, 1)',
					backgroundColor: 'rgba(78, 115, 223, 0.1)',
					fill: true
				}]
			},
			options: {
				responsive: true,
				scales: {
					y: { beginAtZero: true }
				}
			}
		});
	}

	// Chart.js 사용
	function drawTypeRatioChart(typeRatioData) {
		const ctx = document.getElementById('typeRatioChart').getContext('2d');
		// 데이터 구조에 따라 필드명 변경 필요 (예: DEFECTTYPENAME, RATIO)
		const labels = typeRatioData.map(d => d.DEFECTTYPENAME); 
		const data = typeRatioData.map(d => d.TOTAL_QUANTITY); 

		// 부트스트랩 테마 색상 (예시)
		const backgroundColors = [
			'#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b'
		];

		new Chart(ctx, {
			type: 'doughnut',
			data: {
				labels: labels,
				datasets: [{
					data: data,
					backgroundColor: backgroundColors.slice(0, data.length),
					hoverBackgroundColor: backgroundColors.slice(0, data.length),
					hoverBorderColor: "rgba(234, 236, 244, 1)",
				}]
			},
			options: {
				responsive: true,
				maintainAspectRatio: false,
				cutout: '80%',
				plugins: {
					legend: {
						position: 'bottom',
					}
				}
			}
		});
	}

	async function loadDashboard() {
	    try {
	        const response = await fetch('/defect/api/defect-dashboard');

	        if (!response.ok) {
	            console.error("Dashboard API failed with status:", response.status);
	            // 오류 발생 시 카드에 'N/A' 표시
	            document.getElementById('totalDefectCount').textContent = 'N/A';
	            document.getElementById('totalDefectRate').textContent = 'N/A';
	            document.getElementById('topDefectType').textContent = 'N/A';
	            return;
	        }

	        const data = await response.json();

	        // 총 불량 건수 카드 바인딩
	        document.getElementById('totalDefectCount').textContent = 
	            data.totalDefectCount !== undefined ? data.totalDefectCount : 0;
	            
	        // 총 불량률 (%) 카드 바인딩
	        document.getElementById('totalDefectRate').textContent = 
	            (data.totalDefectRate !== undefined ? data.totalDefectRate : 0) + '%';
	            
	        // TOP 1 불량 유형 카드 바인딩
	        document.getElementById('topDefectType').textContent = 
	            data.topDefectType || '없음'; // null이나 undefined일 경우 '없음'으로 표시

	        // 2. 차트 그리기
	        if (data.monthlyTrend && data.monthlyTrend.length > 0) {
	            drawMonthlyChart(data.monthlyTrend);
	        }
	        if (data.typeRatios && data.typeRatios.length > 0) {
	            drawTypeRatioChart(data.typeRatios);
	        }

	    } catch (error) {
	        console.error('Failed to load dashboard data:', error);
	    }
	}

	function setupRadioListener() {
	    const radioButtons = document.querySelectorAll('input[name="defectLocationRadio"]');
	    radioButtons.forEach(radio => {
	        radio.addEventListener('change', function() {
	            fetchDefectHistory(); // 버튼 변경 시 즉시 조회
	        });
	    });
        
        // 초기 로드 시 필터 설정
        fetchDefectHistory(); 
	}


	// 필터 데이터 수집 및 API 호출
	function fetchDefectHistory() {
        const selectedLocation = document.querySelector('input[name="defectLocationRadio"]:checked').value;
        
		// API 호출 및 필터링 데이터 전송
		defectGrid.readData(1, {
			defectLocation: selectedLocation
		});
	}

	// document.getElementById('filterBtn').addEventListener('click', fetchDefectHistory); 

	// 초기화
    loadDashboard(); 
    setupRadioListener(); 
});