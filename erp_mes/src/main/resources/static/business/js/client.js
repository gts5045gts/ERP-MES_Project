document.addEventListener("DOMContentLoaded", () => {
	const grid = new tui.Grid({
		el: document.getElementById('clientGrid'),
		scrollX: false,
		scrollY: true,
		bodyHeight: 350,
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

	// 페이지 처음 로딩 시 전체 목록 불러오기
	fetch("/business/api/clients")
		.then(response => response.json())
		.then(data => {
			grid.resetData(data);
		})
		.catch(error => console.error("데이터 불러오는 과정에서 오류남:", error));

	// 검색 버튼 이벤트
	document.getElementById("searchBtn").addEventListener("click", () => {
		const clientName = document.getElementById("cliSearch").value;
		const clientType = document.getElementById("cliStatus").value;

		fetch(`/business/api/clients/search?clientName=${encodeURIComponent(clientName)}&clientType=${clientType}`)
			.then(response => response.json())
			.then(data => {
				grid.resetData(data);
			})
			.catch(error => console.error("데이터 불러오기 오류:", error));
	});

	// 등록 버튼 이벤트: 모달창 띄우기
	const addBtn = document.getElementById("addBtn");
	const clientAddModal = new bootstrap.Modal(document.getElementById('clientAddModal'));
	addBtn.addEventListener("click", () => {
		clientAddModal.show();
	});

	// 주소 찾기 버튼 이벤트 (카카오 주소 API 연동)
	document.getElementById("searchAddress").addEventListener("click", () => {
		new daum.Postcode({
			oncomplete: function(data) {
				// 팝업에서 검색 결과를 받아 주소 필드에 적용
				document.getElementById('clientAddress').value = data.address;
			}
		}).open();
	});

	// 모달 폼 제출 이벤트 (등록 버튼)
	document.getElementById("clientAddForm").addEventListener("submit", async (event) => {
		event.preventDefault();

		const csrfToken = document.querySelector('meta[name="_csrf"]').content;
		const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

		const formData = {
			clientName: document.getElementById("clientName").value,
			ceoName: document.getElementById("ceoName").value,
			businessNumber: document.getElementById("businessNumber").value,
			clientType: document.getElementById("clientType").value,
			clientStatus: document.getElementById("clientStatus").value,
			clientPhone: document.getElementById("clientPhone").value,
			clientAddress: document.getElementById("clientAddress").value,
		};

		try {
			const response = await fetch("/business/api/clients/submit", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
					[csrfHeader]: csrfToken
				},
				body: JSON.stringify(formData),
			});

			if (response.ok) {
				alert('거래처 등록이 성공적으로 제출되었습니다.');
				location.reload();
			} else {
				const errorText = await response.text(); // await 추가
				alert('거래처 등록 실패: ' + errorText);
			}

		} catch (error) {
			console.error('API 호출중 오류:', error);
			alert('거래처 등록중 오류가 발생했습니다.');
		}
	});
});
