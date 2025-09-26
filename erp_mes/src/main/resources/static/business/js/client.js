document.addEventListener("DOMContentLoaded", () => {
	const grid = new tui.Grid({
		el: document.getElementById('clientGrid'),
		scrollX: false,
		scrollY: true,
		bodyHeight: 400,
		rowHeight: 'auto',
		minBodyHeight: 200,
		emptyMessage: '조회결과가 없습니다.',
		columns: [
			{ header: '거래처 번호', name: 'clientId', align: 'center' },
			{ header: '거래처명', name: 'clientName', align: 'center' },
			{ header: '거래처 유형', name: 'clientType', align: 'center' },
			{ header: '사업자 번호', name: 'businessNumber', align: 'center' },
			{ header: '대표자명', name: 'ceoName', align: 'center' },
			{ header: '주소', name: 'clientAddress', align: 'left' },
			{ header: '전화번호', name: 'clientPhone', align: 'center' },
			{ header: '거래 여부', name: 'clientStatus', align: 'center' }
		],
		data: []
	});


	// 공용 모달 객체
	const clientAddModal = new bootstrap.Modal(document.getElementById('clientAddModal'));
	const form = document.getElementById("clientAddForm");
	const modalTitle = document.getElementById("clientAddModalLabel");
	const submitBtn = form.querySelector("button[type='submit']");

	let isEditMode = false; // 등록/수정 모드 구분
	let allClient = [];
	
	const addBtn = document.getElementById("addBtn");
	if (!isAUTLevel) {
		if (addBtn) addBtn.style.display = "none";
	}

	// 페이지 처음 로딩 시 전체 목록 불러오기
	function loadClients(){
		fetch("/business/api/clients")
			.then(response => response.json())
			.then(data => {
				allClient = data;
//				grid.resetData(allClient);
				filterClient();
			})
			.catch(error => console.error("데이터 불러오는 과정에서 오류남:", error));
	};

	// 검색 버튼 클릭 시 실행
	function filterClient() {
		console.log("ALL CLIENT DATA EXAMPLE:", allClient[0]); 

		const type = document.getElementById("cliType").value;
		const status = document.getElementById("cliStatus").value;
		const keyword = document.getElementById("cliSearch").value.trim();

		let filteredData = allClient;

		// 거래처유형 필터
		if (type !== "ALL") {
			filteredData = filteredData.filter(client => client.clientTypeCode === type);
		}

		// 거래여부 필터
		if (status !== "ALL") {
			filteredData = filteredData.filter(client => client.clientStatusCode === status);
		}

		// 거래처명 필터
		if (keyword) {
			filteredData = filteredData.filter(client =>
				(client.clientName && client.clientName.includes(keyword))
			);
		}

		grid.resetData(filteredData);
	}

	// 검색 이벤트 바인딩
	document.getElementById("searchBtn").addEventListener("click", filterClient);

	// 엔터키 검색
	document.getElementById("cliSearch").addEventListener("keydown", function(e) {
		if (e.key === "Enter") {
			filterClient();
		}
	});

	loadClients();

	// 등록 버튼 이벤트
	if(addBtn){
		addBtn.addEventListener("click", () => {
			isEditMode = false;
			modalTitle.textContent = "거래처 등록";
			submitBtn.textContent = "등록";
			form.reset();
			document.getElementById("clientId").value = ""; // hidden 초기화
			clientAddModal.show();
		});
	}

	// 주소 찾기 버튼 이벤트 (카카오 주소 API 연동)
	document.getElementById("searchAddress").addEventListener("click", () => {
		new daum.Postcode({
			oncomplete: function(data) {
				// 팝업에서 검색 결과를 받아 주소 필드에 적용
				document.getElementById('clientAddress').value = data.address;
			}
		}).open();
	});

	if(isAUTLevel) {
		grid.on("dblclick", (ev) => {
			const rowData = grid.getRow(ev.rowKey);
			if (!rowData) return;

			isEditMode = true;
			modalTitle.textContent = "거래처 수정";
			submitBtn.textContent = "수정";

			// 데이터 세팅
			document.getElementById("clientId").value = rowData.clientId;
			document.getElementById("clientName").value = rowData.clientName;
			document.getElementById("ceoName").value = rowData.ceoName;
			document.getElementById("businessNumber").value = rowData.businessNumber;
			document.getElementById("clientPhone").value = rowData.clientPhone;
			document.getElementById("clientAddress").value = rowData.clientAddress;
			document.getElementById("clientType").value = rowData.clientTypeCode;
			document.getElementById("clientStatus").value = rowData.clientStatusCode;

			clientAddModal.show();
		});
	}

	// 모달 폼 제출 이벤트 (등록, 수정 같이 사용)
	form.addEventListener("submit", async (event) => {
		event.preventDefault();

		// 필수 입력 필드 확인
		const requiredFields = [
			{ id: "clientName", name: "거래처명" },
			{ id: "ceoName", name: "대표자명" },
			{ id: "businessNumber", name: "사업자 등록번호" },
			{ id: "clientType", name: "거래처 유형" },
			{ id: "clientStatus", name: "거래 여부" },
			{ id: "clientPhone", name: "거래처 연락처" },
			{ id: "clientAddress", name: "주소" }
		];

		for (const field of requiredFields) {
			const value = document.getElementById(field.id).value.trim();
			if (!value) {
				alert(`${field.name}을(를) 입력해주세요.`);
				document.getElementById(field.id).focus();
				return; // 함수 실행 중단
			}
		}

		const csrfToken = document.querySelector('meta[name="_csrf"]').content;
		const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

		const formData = {
			clientId: document.getElementById("clientId").value,
			clientName: document.getElementById("clientName").value,
			ceoName: document.getElementById("ceoName").value,
			businessNumber: document.getElementById("businessNumber").value,
			clientType: document.getElementById("clientType").value,
			clientStatus: document.getElementById("clientStatus").value,
			clientPhone: document.getElementById("clientPhone").value,
			clientAddress: document.getElementById("clientAddress").value,
		};

		try {
			const url = isEditMode
				? `/business/api/clients/update/${formData.clientId}`
				: "/business/api/clients/submit";

			const method = isEditMode ? "PUT" : "POST";

			const response = await fetch(url, {
				method: method,
				headers: {
					"Content-Type": "application/json",
					[csrfHeader]: csrfToken
				},
				body: JSON.stringify(formData),
			});

			if (response.ok) {
				alert(isEditMode ? '거래처가 수정되었습니다.' : '거래처 등록이 성공적으로 제출되었습니다.');
				clientAddModal.hide();
				loadClients();
			} else {
				const errorText = await response.text();
				alert((isEditMode ? '거래처 수정 실패: ' : '거래처 등록 실패: ') + errorText);
			}
		} catch (error) {
			console.error('API 호출중 오류:', error);
			alert('처리 중 오류가 발생했습니다.');
		}
	});


});
