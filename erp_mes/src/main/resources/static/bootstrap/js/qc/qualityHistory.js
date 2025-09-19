document.addEventListener('DOMContentLoaded', function() {
	// --- 상단 그리드 (품질검사 이력) ---
	let allHistoryData = [];
	const historyGrid = new tui.Grid({
		el: document.getElementById('historyGrid'),
		columns: [
			{ header: '검사ID', name: 'inspectionId' },
			{ header: '검사유형', name: 'inspectionTypeName' },
			{ header: '제품명', name: 'productName' },
			{ header: '공정명', name: 'processName' },
			{ header: '검사일자', name: 'inspectionDate' },
			{ header: '검사자', name: 'empName' },
			{ header: '로트번호', name: 'lotId' },
			{ header: '검사결과', name: 'result' },
			{ header: '비고', name: 'remarks' }
		]
	});

	async function loadHistoryData() {
		try {
			const response = await fetch('/quality/api/inspection-results');
			if (!response.ok) { throw new Error('데이터 로드 실패'); }
			allHistoryData = await response.json();
			historyGrid.resetData(allHistoryData);
		} catch (error) {
			console.error('Fetch error:', error);
		}
	}

	function filterHistoryData() {
		const filterType = document.getElementById('historyFilterType').value;
		const filterProduct = document.getElementById('historyFilterProduct').value.toLowerCase();
		const filterLotId = document.getElementById('historyFilterLotId').value.toLowerCase();
		let filteredData = allHistoryData.filter(item => {
			const typeMatch = (filterType === 'ALL' || item.inspectionType === filterType);
			const productMatch = item.productName && item.productName.toLowerCase().includes(filterProduct);
			const lotIdMatch = item.lotId && item.lotId.toLowerCase().includes(filterLotId);
			return typeMatch && productMatch && lotIdMatch;
		});
		historyGrid.resetData(filteredData);
	}
	document.getElementById('historySearchBtn').addEventListener('click', filterHistoryData);
	
	// --- 하단 그리드 (검사 대기 목록) ---
	const targetGrid = new tui.Grid({
		el: document.getElementById('targetGrid'),
		columns: [
			{ header: '작업지시ID', name: 'workOrderId' },
	        { header: '제품명', name: 'productName' },
	        { header: '공정명', name: 'processName' },
	        { header: '작업자이름', name: 'empName' },
	        { header: '수량', name: 'planQuantity' },
	        { header: 'LOT번호', name: 'lotId' },
	        { header: '작업상태', name: 'workOrderStatus' },
	        { header: '검사유형', name: 'inspectionTypeName' },
			{ header: '제품ID', name: 'productId', hidden: true },
			{ header: '공정ID', name: 'processId', hidden: true },
			{ header: '작업자ID', name: 'empId', hidden: true },
			{ header: '검사유형', name: 'inspectionType', hidden: true}
		]
	});

	async function loadTargetData() {
		try {
			// 백엔드에서 검사 대기 목록을 반환하는 API가 필요합니다.
			const response = await fetch('/quality/api/inspection-targets');
			if (!response.ok) { throw new Error('검사 대상 데이터 로드 실패'); }
			const data = await response.json();
			targetGrid.resetData(data);
		} catch (error) {
			console.error('Fetch error:', error);
		}
	}
	
	// --- 모달 로직 ---
	let selectedTargetData = null;
	let inspectionCriteria = null;

	// 하단 그리드 행 클릭 이벤트
	targetGrid.on('click', async (ev) => {
		console.log('클릭 이벤트 발생!');
		console.log('rowKey:', ev.rowKey);
		if (typeof ev.rowKey !== 'undefined') {
			selectedTargetData = targetGrid.getRow(ev.rowKey);
			
			console.log('선택데이터 : ' + selectedTargetData);
			
			// 모달에 기본 정보 바인딩
			document.getElementById('modalProductName').value = selectedTargetData.productName;
			document.getElementById('modalLotId').value = selectedTargetData.lotId;
			
			// 검사 기준 (허용 공차)을 가져오는 API
			try {
			    const criteriaResponse = await fetch(`/quality/api/inspection-item/${selectedTargetData.productId}`);
			    if (!criteriaResponse.ok) { throw new Error('검사 기준 로드 실패'); }
			    const criteriaList = await criteriaResponse.json();
			    // 여러 검사 항목 중 첫 번째 항목을 사용 (상황에 맞게 로직 변경 가능)
			    if (criteriaList && criteriaList.length > 0) {
			        inspectionCriteria = criteriaList[0];
			        document.getElementById('modalInspectionType').value = inspectionCriteria.inspectionTypeName; // 검사 유형 채우기
			    }
			} catch (error) {
			    console.error('검사 기준 로드 실패:', error);
			    inspectionCriteria = null;
			}
			
			// 모달 열기
			$('#inspectionModal').modal('show');
		}
	});
	
	// 실측값 입력 시 합격/불합격 자동 판단
	document.getElementById('modalMeasurement').addEventListener('input', (event) => {
		const measurement = parseFloat(event.target.value);
		if (inspectionCriteria && !isNaN(measurement)) {
			const minTolerance = inspectionCriteria.minTolerance;
			const maxTolerance = inspectionCriteria.maxTolerance;
			const resultField = document.getElementById('modalResult');
			
			if (measurement >= minTolerance && measurement <= maxTolerance) {
				resultField.value = '합격';
				resultField.style.color = 'blue';
			} else {
				resultField.value = '불합격';
				resultField.style.color = 'red';
			}
		}
	});

	// '등록' 버튼 클릭 이벤트
	document.getElementById('registerBtn').addEventListener('click', async () => {
	    const registrationData = {
	        productId: selectedTargetData.productId, // 제품명
	        processId: selectedTargetData.processId,   // 공정ID
	        empId: selectedTargetData.empId,           // 작업자ID
	        lotId: selectedTargetData.lotId,           // 로트번호
	        
	        result: document.getElementById('modalResult').value, // '합격'/'불합격'
	        remarks: document.getElementById('modalRemarks').value, // 비고
	        
	        // 추가 정보
	        workOrderId: selectedTargetData.workOrderId
	    };

	    try {
	        const response = await fetch('/quality/api/register-inspection-result', {
	            method: 'POST',
	            headers: { 'Content-Type': 'application/json' },
	            body: JSON.stringify(registrationData)
	        });
	        const result = await response.json();
	        if (result.success) {
	            alert('검사 등록 성공!');
	            $('#inspectionModal').modal('hide');
	            loadHistoryData();
	            loadTargetData();
	        } else {
	            alert('등록 실패: ' + result.message);
	        }
	    } catch (error) {
	        console.error('Registration failed:', error);
	        alert('등록 중 오류 발생');
	    }
	});

	// 초기 데이터 로드
	loadHistoryData();
	loadTargetData();
});
