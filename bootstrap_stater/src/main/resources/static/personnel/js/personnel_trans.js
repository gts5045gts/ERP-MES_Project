// 'DOMContentLodaded' -> HTML 구조가 완전히 로드된 후에 자바스크립트 코드가 실행
document.addEventListener('DOMContentLoaded', () => {

	// 1. 주요 HTML 요소들 선택
	const issueBtn = document.getElementById('issueBtn');
	const searchBtn = document.getElementById('searchBtn');
	const orderTypeSelect = document.getElementById('orderTypeSelect');
	const orderDateStart = document.getElementById('orderDateStart');
	const orderDateEnd = document.getElementById('orderDateEnd');

	// 2. 이벤트 리스너 정의
	// '발령' 버튼 클릭 시 팝업 윈도우 열기
	if (issueBtn) {
		issueBtn.addEventListener('click', () => {
			// 팝업 윈도우 설정
			const popupUrl = '/personnel/trans/save'; // 팝업 페이지 URL
			const windowName = 'personnelTransPopup';
			const windowFeatures = 'width=950,height=920,scrollbars=yes,resizable=yes';

			window.open(popupUrl, windowName, windowFeatures);
		});
	}
	
	// '조회' 버튼 클릭 이벤트
	if (searchBtn) {
	    searchBtn.addEventListener('click', () => {
	        const startDate = orderDateStart.value;
	        const endDate = orderDateEnd.value;
	        
			let transferType = orderTypeSelect.value;
			if (transferType === "PROMOTION") transferType = "승진";
			if (transferType === "TRANSFER") transferType = "전보";
			if (transferType === "ALL") transferType = null; // 전체 조회일 때 null로 처리
			
	        // 검색 조건을 파라미터로 넘겨서 함수 호출
	        loadPersonnelTransferList(transferType, startDate, endDate);
	    });
	}

	// 3. 페이지 로드 시 초기 데이터 로딩 함수 호출
	loadPersonnelTransferList();
});

// 인사발령 목록을 테이블에 표시하는 함수
function loadPersonnelTransferList(transferType = "ALL", startDate = "", endDate = "") {
	const transferTableBody = document.getElementById('transferTableBody');
	fetch('/personnel/api/transfers')
		.then(response => {
			if (!response.ok) {
				throw new Error('인사발령 목록을 불러오는데 실패했습니다.');
			}
			return response.json();
		})
		.then(data => {
			// 조회 필터링
			let filteredData = data;

			if (transferType && transferType !== "ALL") {
				filteredData = filteredData.filter(item => item.transferType === transferType);
			}

			if (startDate) {
			    filteredData = filteredData.filter(item => item.transDate >= startDate);
			}

			if (endDate) {
			    filteredData = filteredData.filter(item => item.transDate <= endDate);
			}
			
			// 테이블 바디 비우기
			transferTableBody.innerHTML = '';

			// 데이터가 없을 경우 메시지 표시
			if (filteredData.length === 0) {
				const row = `<tr><td colspan="7" class="text-center">데이터가 없습니다.</td></tr>`;
				transferTableBody.innerHTML = row;
				return;
			}

			// 각 데이터를 테이블 행으로 변환하여 추가
			let count = 1;
			filteredData.forEach(transfer => {
				const row = `
	                    <tr>
	                        <td>${count++}</td>
	                        <td>${transfer.empId}</td>
	                        <td>${transfer.name}</td>
	                        <td>${transfer.transferType}</td>
	                        <td>${transfer.transDate}</td>
							<td>${transfer.oldDeptName} -> ${transfer.newDeptName}</td>
	                        <td>${transfer.oldPosName} -> ${transfer.newPosName}</td>
	                    </tr>
	                `;
				transferTableBody.innerHTML += row;
			});
		})
		.catch(error => {
			console.error('Error:', error);
			transferTableBody.innerHTML = `<tr><td colspan="7" class="text-center text-danger">${error.message}</td></tr>`;
		});
}