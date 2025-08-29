// 'DOMContentLodaded' -> HTML 구조가 완전히 로드된 후에 자바스크립트 코드가 실행
document.addEventListener('DOMContentLoaded', () => {
	// HTML 요소들을 선택
	const searchButton = document.getElementById('modalSearchEmployeeBtn');
	const deptSelect = document.getElementById('modalDeptSelect');
	const positionSelect = document.getElementById('modalPositionSelect');
	const searchInput = document.getElementById('modalEmployeeSearchInput');
	const employeeTableBody = document.getElementById('modalEmployeeTableBody');

	// 초기 사원 목록을 테이블에 표시 (페이지 로드 시)
	displayEmployees(allEmployeesData);

	// 선택된 사원 정보를 표시할 요소들
	const selectedEmployeeName = document.getElementById('selectedEmployeeName');
	const selectedEmployeeId = document.getElementById('selectedEmployeeId');
	const selectedEmployeeCurrentDept = document.getElementById('selectedEmployeeCurrentDept');
	const selectedEmployeeCurrentPosition = document.getElementById('selectedEmployeeCurrentPosition');

	// 숨겨진 입력 필드 (발령 정보 저장 시 사용)
	const hiddenSelectedEmployeeDbId = document.getElementById('hiddenSelectedEmployeeDbId');

	const saveBtn = document.getElementById('saveBtn');

	// 결재자 드롭다운
	const approverSelect = document.getElementById('approverSelect');

	// 검색 버튼 클릭 이벤트 리스너
	searchButton.addEventListener('click', () => {
		const deptId = deptSelect.value;
		const positionId = positionSelect.value;
		const searchKeyword = searchInput.value.toLowerCase(); // 대소문자 구분을 없애기 위해 소문자로 변환

		// 전체 사원 목록에서 조건에 맞는 사원만 필터링
		const filteredEmployees = allEmployeesData.filter(employee => {
			const matchesDept = (deptId === '' || employee.deptId === deptId);
			const matchesPosition = (positionId === '' || employee.posId === positionId);
			const matchesKeyword = (
				searchKeyword === '' ||
				employee.name.toLowerCase().includes(searchKeyword) ||
				employee.empId.toLowerCase().includes(searchKeyword)
			);
			return matchesDept && matchesPosition && matchesKeyword;
		});

		// 필터링된 결과를 테이블에 표시
		displayEmployees(filteredEmployees);
	});

	// 검색 결과를 테이블에 동적으로 표시하는 함수
	function displayEmployees(employees) {
		employeeTableBody.innerHTML = ''; // 기존 내용 초기화

		if (employees.length === 0) {
			employeeTableBody.innerHTML = `<tr><td colspan="5" class="text-center">사원 검색 결과가 없습니다.</td></tr>`;
			return;
		}

		employees.forEach(employee => {
			const row = document.createElement('tr');
			row.innerHTML = `
                <td>${employee.empId}</td>
                <td>${employee.name}</td>
                <td>${employee.deptName}</td>
                <td>${employee.posName}</td>
                <td>${employee.phone}</td>
            `;
			// 리스트의 행에 더블클릭 이벤트 리스너 추가
			row.addEventListener('dblclick', () => {
				// 선택된 행의 데이터로 '선택된 사원 정보' 영역 채움
				selectedEmployeeName.textContent = employee.name;
				selectedEmployeeId.textContent = employee.empId;
				selectedEmployeeCurrentDept.textContent = employee.deptName;
				selectedEmployeeCurrentPosition.textContent = employee.posName;

				// 추후 발령 정보 저장에 사용할 사원번호를 숨겨진 필드에 저장
				hiddenSelectedEmployeeDbId.value = employee.empId;
			});
			employeeTableBody.appendChild(row);
		});
	}

	// 페이지 로드 시 결재자 드롭다운
	function populateApprovers() {
		if (approversData && approversData.length > 0) {
			approversData.forEach(employee => {
				const option = document.createElement('option');
				option.value = employee.empId;
				option.textContent = employee.name + ' (' + employee.levName + ')';
				approverSelect.appendChild(option);
			});
		}
	}

	populateApprovers();

	// 신청 버튼 클릭 이벤트 리스너
	document.getElementById('saveBtn').addEventListener('click', async () => {
	    const selectedEmpId = document.getElementById('hiddenSelectedEmployeeDbId').value;
	    const transType = document.getElementById('transTypeSelect').value;
	    const transDate = document.getElementById('transDateInput').value;
	    const transDept = document.getElementById('transDeptSelect').value;
	    const transPos = document.getElementById('transPosSelect').value;
	    const approverId = document.getElementById('approverSelect').value;

	    if (!selectedEmpId || !transType || !transDate || !transDept || !transPos || !approverId) {
	        alert('모든 발령 정보를 입력해주세요.');
	        return;
	    }

	    const payload = {
	        empId: selectedEmpId,
	        transType: transType,
	        transDate: transDate,
	        transDept: transDept,
	        transPos: transPos,
	        approverId: approverId
	    };

	    try {
	        const response = await fetch('/personnel/api/trans', {
	            method: 'POST',
	            headers: { 'Content-Type': 'application/json' },
	            body: JSON.stringify(payload)
	        });

	        if (response.ok) {
	            alert('인사 발령 신청이 성공적으로 제출되었습니다.');
	            window.close(); // 팝업 닫기
	        } else {
	            const errorMessage = await response.text();
	            alert('인사 발령 신청 실패: ' + errorMessage);
	        }
	    } catch (error) {
	        console.error('API 호출 중 오류:', error);
	        alert('인사 발령 신청 중 오류가 발생했습니다.');
	    }
	});

});