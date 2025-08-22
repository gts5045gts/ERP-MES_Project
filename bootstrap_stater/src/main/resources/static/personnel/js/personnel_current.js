// personnel_current.js
document.addEventListener('DOMContentLoaded', function() {
	const personnelTableBody = document.getElementById('personnelTableBody');
	const searchInput = document.getElementById('search-input');
	const entriesPerPageSelect = document.getElementById('entries-per-page');
	const paginationDiv = document.getElementById('pagination');
	const sortableHeaders = document.querySelectorAll('#personnel-table th.sortable');


	let allPersonnelData = []; // 모든 직원 데이터를 저장할 배열
	let currentPage = 1;
	let entriesPerPage = parseInt(entriesPerPageSelect.value);

	let currentSort = {
		key: 'joinDate', // 기본 정렬 키
		direction: 'desc' // 기본 정렬 방향
	};

	// 모든 직원 데이터를 API에서 가져옴
	const fetchAllPersonnel = async () => {
		try {
			const response = await fetch('/personnel/api/personnels');
			if (!response.ok) {
				throw new Error('인사 데이터 로드 실패: ' + response.statusText);
			}
			allPersonnelData = await response.json();
			// 초기 데이터 로드 후 기본 정렬 적용
			sortData();
			renderTableAndPagination();
		} catch (error) {
			console.error('직원 데이터 로드 중 오류 발생:', error);
			personnelTableBody.innerHTML = '<tr><td colspan="8">데이터를 불러오는 데 실패했습니다.</td></tr>';
		}
	};

	// 데이터 정렬
	const sortData = () => {
		const { key, direction } = currentSort;
		allPersonnelData.sort((a, b) => {
			const valueA = a[key] || '';
			const valueB = b[key] || '';
			let compareResult = 0;

			if (typeof valueA === 'string' && typeof valueB === 'string') {
				compareResult = valueA.localeCompare(valueB);
			} else {
				if (valueA < valueB) compareResult = -1;
				else if (valueA > valueB) compareResult = 1;
			}

			return direction === 'asc' ? compareResult : -compareResult;
		});
	};

	// 테이블 헤더에 정렬 아이콘 클래스 업데이트
	const updateSortIcons = () => {
		sortableHeaders.forEach(header => {
			header.classList.remove('asc', 'desc');
			const sortKey = header.getAttribute('data-sort-key');
			if (sortKey === currentSort.key) {
				header.classList.add(currentSort.direction);
			}
		});
	};

	// 테이블 행을 렌더링하는 함수
	const renderTableRows = (dataToDisplay) => {
		personnelTableBody.innerHTML = ''; // 기존 내용 비우기
		if (dataToDisplay.length === 0) {
			personnelTableBody.innerHTML = '<tr><td colspan="8">일치하는 직원이 없습니다.</td></tr>';
			return;
		}
		dataToDisplay.forEach(personnel => {
			const row = `
                <tr>
                    <td>${personnel.joinDate || ''}</td>
                    <td>${personnel.empId || ''}</td>
                    <td>${personnel.name || ''}</td>
                    <td>${personnel.posName || ''}</td>
                    <td>${personnel.deptName || ''}</td>
                    <td>${personnel.phone || ''}</td>
                    <td>${personnel.email || ''}</td>
					<td><button class="btn btn-primary btn-sm detail-btn" data-empid="${personnel.empId}">상세조회</button></td>
					                </tr>
					            `;
			personnelTableBody.innerHTML += row;
		});

		// 상세조회 이벤트 리스너 추가
		document.querySelectorAll('.detail-btn').forEach(button => {
			button.addEventListener('click', (event) => {
				const empId = event.target.getAttribute('data-empid');
				if (empId) {
					// 상세정보 페이지로 이동하며 empId를 detailInfo URL 파라미터로 전달
					window.location.href = `/personnel/detailInfo?empId=${empId}`;
				}
			});
		});
	};

	// 페이지네이션 컨트롤을 렌더링하는 함수
	const renderPaginationControls = (filteredData) => {
		paginationDiv.innerHTML = ''; // 기존 페이지네이션 버튼 비우기
		const totalPages = Math.ceil(filteredData.length / entriesPerPage);

		if (totalPages <= 1) return; // 페이지가 하나 이하면 페이지네이션 숨김

		// 이전 페이지 버튼
		if (currentPage > 1) {
			const prevButton = document.createElement('a');
			prevButton.href = '#';
			prevButton.className = 'page-link';
			prevButton.textContent = '이전';
			prevButton.addEventListener('click', (e) => {
				e.preventDefault(); // 기본 링크 동작 방지
				currentPage--;
				renderTableAndPagination();
			});
			paginationDiv.appendChild(prevButton);
		}

		// 페이지 번호 버튼 (최대 5개 정도 표시)
		const maxPageButtons = 5;
		let startPage = Math.max(1, currentPage - Math.floor(maxPageButtons / 2));
		let endPage = Math.min(totalPages, startPage + maxPageButtons - 1);

		// 만약 끝 페이지가 충분히 크지 않으면 시작 페이지를 조정
		if (endPage - startPage + 1 < maxPageButtons) {
			startPage = Math.max(1, endPage - maxPageButtons + 1);
		}

		for (let i = startPage; i <= endPage; i++) {
			const pageButton = document.createElement('a');
			pageButton.href = '#';
			pageButton.className = `page-link ${i === currentPage ? 'active' : ''}`;
			pageButton.textContent = i;
			pageButton.addEventListener('click', (e) => {
				e.preventDefault(); // 기본 링크 동작 방지
				currentPage = i;
				renderTableAndPagination();
			});
			paginationDiv.appendChild(pageButton);
		}

		// 다음 페이지 버튼
		if (currentPage < totalPages) {
			const nextButton = document.createElement('a');
			nextButton.href = '#';
			nextButton.className = 'page-link';
			nextButton.textContent = '다음';
			nextButton.addEventListener('click', (e) => {
				e.preventDefault(); // 기본 링크 동작 방지
				currentPage++;
				renderTableAndPagination();
			});
			paginationDiv.appendChild(nextButton);
		}
	};

	// 테이블 데이터 필터링 및 페이지네이션 적용 후 렌더링
	const renderTableAndPagination = () => {
		const searchTerm = searchInput.value.toLowerCase();
		let filteredData = allPersonnelData;

		// 검색어 필터링
		if (searchTerm) {
			filteredData = allPersonnelData.filter(p =>
				(p.joinDate && String(p.joinDate).toLowerCase().includes(searchTerm)) ||
				(p.empId && String(p.empId).toLowerCase().includes(searchTerm)) ||
				(p.name && String(p.name).toLowerCase().includes(searchTerm)) ||
				(p.posName && String(p.posName).toLowerCase().includes(searchTerm)) ||
				(p.deptName && String(p.deptName).toLowerCase().includes(searchTerm)) ||
				(p.phone && String(p.phone).toLowerCase().includes(searchTerm)) ||
				(p.email && String(p.email).toLowerCase().includes(searchTerm))
			);
		}

		// 현재 페이지에 맞는 데이터 슬라이싱
		const startIndex = (currentPage - 1) * entriesPerPage;
		const endIndex = startIndex + entriesPerPage;
		const dataToDisplay = filteredData.slice(startIndex, endIndex);

		renderTableRows(dataToDisplay); // 테이블 행 렌더링
		renderPaginationControls(filteredData); // 페이지네이션 컨트롤 렌더링
		updateSortIcons();
	};

	sortableHeaders.forEach(header => {
		header.addEventListener('click', () => {
			const sortKey = header.getAttribute('data-sort-key');

			// 현재 정렬 키와 동일하면 방향을 토글, 아니면 기본 정렬 방향으로 설정
			if (currentSort.key === sortKey) {
				currentSort.direction = currentSort.direction === 'asc' ? 'desc' : 'asc';
			} else {
				currentSort.key = sortKey;
				currentSort.direction = 'asc'; // 새 열 클릭 시 기본은 오름차순
			}

			sortData(); // 데이터 정렬
			currentPage = 1; // 정렬 후 첫 페이지로 이동
			renderTableAndPagination(); // 테이블 및 페이지네이션 다시 렌더링
		});
	});

	searchInput.addEventListener('input', () => {
		currentPage = 1;
		renderTableAndPagination();
	});

	entriesPerPageSelect.addEventListener('change', (event) => {
		entriesPerPage = parseInt(event.target.value);
		currentPage = 1;
		renderTableAndPagination();
	});


	// 이벤트 리스너 설정
	searchInput.addEventListener('input', () => {
		currentPage = 1; // 검색 시 첫 페이지로 이동
		renderTableAndPagination();
	});

	entriesPerPageSelect.addEventListener('change', (event) => {
		entriesPerPage = parseInt(event.target.value);
		currentPage = 1; // 엔트리 수 변경 시 첫 페이지로 이동
		renderTableAndPagination();
	});

	// 페이지 로드 시 모든 데이터 가져오고 테이블 렌더링
	fetchAllPersonnel();
});
