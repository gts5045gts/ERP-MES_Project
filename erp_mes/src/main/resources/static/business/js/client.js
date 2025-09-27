//document.addEventListener("DOMContentLoaded", () => {
//	const grid = new tui.Grid({
//		el: document.getElementById('clientGrid'),
//		scrollX: false,
//		scrollY: true,
//		bodyHeight: 400,
//		rowHeight: 'auto',
//		minBodyHeight: 200,
//		emptyMessage: '조회결과가 없습니다.',
//		columns: [
//			{ header: '거래처 번호', name: 'clientId', align: 'center' },
//			{ header: '거래처명', name: 'clientName', align: 'center' },
//			{ header: '거래처 유형', name: 'clientType', align: 'center' },
//			{ header: '사업자 번호', name: 'businessNumber', align: 'center' },
//			{ header: '대표자명', name: 'ceoName', align: 'center' },
//			{ header: '주소', name: 'clientAddress', align: 'left' },
//			{ header: '전화번호', name: 'clientPhone', align: 'center' },
//			{ header: '거래 여부', name: 'clientStatus', align: 'center' }
//		],
//		data: []
//	});
//
//
//	// 공용 모달 객체
//	const clientAddModal = new bootstrap.Modal(document.getElementById('clientAddModal'));
//	const form = document.getElementById("clientAddForm");
//	const modalTitle = document.getElementById("clientAddModalLabel");
//	const submitBtn = form.querySelector("button[type='submit']");
//
//	let isEditMode = false; // 등록/수정 모드 구분
//	let allClient = [];
//
//	const addBtn = document.getElementById("addBtn");
//	// isAUTLevel은 상위 스코프에 정의되어 있어야 합니다. (현재 코드에는 정의되지 않았으므로 그대로 유지)
//	// if (!isAUTLevel) {
//	// 	if (addBtn) addBtn.style.display = "none";
//	// }
//
//    // =========================================================================
//    // 💡 1. 사업자번호 유효성 검증 함수 정의 (폼 제출 시 재사용)
//    // =========================================================================
//    /**
//     * 사업자등록번호 유효성 검증 API 호출 함수
//     * @param {string} businessNumber - 검증할 사업자등록번호
//     * @returns {Promise<boolean>} 유효하면 true, 아니면 false
//     */
//    async function validateBusinessNumber(businessNumber) {
//        // 필수 값 검사 (API 호출 전)
//        if (!businessNumber || businessNumber.length !== 10) {
//            alert("사업자등록번호 10자리를 입력해주세요.");
//            return false;
//        }
//
//        // CSRF 토큰 가져오기 (메타 태그가 문서에 있다고 가정)
//        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
//        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
//
//        try {
//            const validateResponse = await fetch("/business/api/validateBizNo", {
//                method: "POST",
//                headers: {
//                    "Content-Type": "application/json",
//                    [csrfHeader]: csrfToken
//                },
//                body: JSON.stringify({ businessNumber })
//            });
//
//            if (!validateResponse.ok) {
//                // 405 Method Not Allowed 등의 에러가 발생하면 이 블록으로 옴
//                alert("사업자등록번호 검증 실패: 서버 응답 오류가 발생했습니다.");
//                return false;
//            }
//
//            const validateResult = await validateResponse.json();
//            console.log("검증 결과:", validateResult);
//            
//            // 공공데이터 응답 형식에 맞게 데이터 접근 (validateResult.data가 배열이라고 가정)
//            const dataArray = validateResult.data; 
//
//            if (!dataArray || dataArray.length === 0 || !dataArray[0].valid) {
//                alert("사업자등록번호 검증 응답 형식이 올바르지 않거나 비어있습니다.");
//                return false;
//            }
//
//            const statusCode  = dataArray[0].valid; // 01: 유효
//
//            if (statusCode  !== "01") {
//                alert("유효하지 않은 사업자등록번호입니다. (폐업, 휴업 또는 미등록 상태)");
//                return false;
//            }
//            
//            // 검증 통과
//            return true;
//
//        } catch (error) {
//            console.error("검증 API 호출 에러:", error);
//            alert("사업자등록번호 검증 중 통신 오류가 발생했습니다. (서버 측 RestTemplate 호출 확인 필요)");
//            return false;
//        }
//    }
//    // =========================================================================
//
//
//	// 페이지 처음 로딩 시 전체 목록 불러오기
//	function loadClients() {
//		fetch("/business/api/clients")
//			.then(response => response.json())
//			.then(data => {
//				allClient = data;
//				//				grid.resetData(allClient);
//				filterClient();
//			})
//			.catch(error => console.error("데이터 불러오는 과정에서 오류남:", error));
//	};
//
//	// 검색 버튼 클릭 시 실행
//	function filterClient() {
//		console.log("ALL CLIENT DATA EXAMPLE:", allClient[0]);
//
//		const type = document.getElementById("cliType").value;
//		const status = document.getElementById("cliStatus").value;
//		const keyword = document.getElementById("cliSearch").value.trim();
//
//		let filteredData = allClient;
//
//		// 거래처유형 필터
//		if (type !== "ALL") {
//			filteredData = filteredData.filter(client => client.clientTypeCode === type);
//		}
//
//		// 거래여부 필터
//		if (status !== "ALL") {
//			filteredData = filteredData.filter(client => client.clientStatusCode === status);
//		}
//
//		// 거래처명 필터
//		if (keyword) {
//			filteredData = filteredData.filter(client =>
//				(client.clientName && client.clientName.includes(keyword))
//			);
//		}
//
//		grid.resetData(filteredData);
//	}
//
//	// 검색 이벤트 바인딩
//	document.getElementById("searchBtn").addEventListener("click", filterClient);
//
//	// 엔터키 검색
//	document.getElementById("cliSearch").addEventListener("keydown", function(e) {
//		if (e.key === "Enter") {
//			filterClient();
//		}
//	});
//	
//	
//
//	loadClients();
//
//	// 등록 버튼 이벤트
//	if (addBtn) {
//		addBtn.addEventListener("click", () => {
//			isEditMode = false;
//			modalTitle.textContent = "거래처 등록";
//			submitBtn.textContent = "등록";
//			form.reset();
//			document.getElementById("clientId").value = ""; // hidden 초기화
//			clientAddModal.show();
//		});
//	}
//
//	// 주소 찾기 버튼 이벤트 (카카오 주소 API 연동)
//	document.getElementById("searchAddress").addEventListener("click", () => {
//		new daum.Postcode({
//			oncomplete: function(data) {
//				// 팝업에서 검색 결과를 받아 주소 필드에 적용
//				document.getElementById('clientAddress').value = data.address;
//			}
//		}).open();
//	});
//	
//	document.getElementById("businessNumber").addEventListener("input", function (e) {
//	    // 숫자만 입력되게 필터링
//	    this.value = this.value.replace(/[^0-9]/g, "");
//	    // 최대 10자리 제한
//	    if (this.value.length > 10) {
//	        this.value = this.value.slice(0, 10);
//	    }
//        
//        // 💡 input 이벤트에서 실시간 검증은 submit에서만 검증하는 것이 안정적이므로 제거함
//	});
//
//	// isAUTLevel은 상위 스코프에 정의되어 있어야 합니다. (현재 코드에는 정의되지 않았으므로 그대로 유지)
//	// if (isAUTLevel) {
//		grid.on("dblclick", (ev) => {
//			const rowData = grid.getRow(ev.rowKey);
//			if (!rowData) return;
//
//			isEditMode = true;
//			modalTitle.textContent = "거래처 수정";
//			submitBtn.textContent = "수정";
//
//			// 데이터 세팅
//			document.getElementById("clientId").value = rowData.clientId;
//			document.getElementById("clientName").value = rowData.clientName;
//			document.getElementById("ceoName").value = rowData.ceoName;
//			document.getElementById("businessNumber").value = rowData.businessNumber;
//			document.getElementById("clientPhone").value = rowData.clientPhone;
//			document.getElementById("clientAddress").value = rowData.clientAddress;
//			document.getElementById("clientType").value = rowData.clientTypeCode;
//			document.getElementById("clientStatus").value = rowData.clientStatusCode;
//
//			clientAddModal.show();
//		});
//	// }
//
//	// 모달 폼 제출 이벤트 (등록, 수정 같이 사용)
//	form.addEventListener("submit", async (event) => {
//		event.preventDefault();
//		
//		const csrfToken = document.querySelector('meta[name="_csrf"]').content;
//		const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
//
//		const businessNumber = document.getElementById("businessNumber").value.trim();
//
//		// =========================================================================
//        // 💡 2. 폼 제출 시, 필수 필드 확인보다 앞서 사업자번호 유효성 검사 필수 수행
//        // =========================================================================
//        const isBizNoValid = await validateBusinessNumber(businessNumber);
//        if (!isBizNoValid) {
//            document.getElementById("businessNumber").focus();
//            return; // 검증 실패 시 폼 제출 중단
//        }
//        // =========================================================================
//
//
//		// 필수 입력 필드 확인
//		const requiredFields = [
//			{ id: "clientName", name: "거래처명" },
//			{ id: "ceoName", name: "대표자명" },
//			{ id: "businessNumber", name: "사업자 등록번호" },
//			{ id: "clientType", name: "거래처 유형" },
//			{ id: "clientStatus", name: "거래 여부" },
//			{ id: "clientPhone", name: "거래처 연락처" },
//			{ id: "clientAddress", name: "주소" }
//		];
//
//		for (const field of requiredFields) {
//			const value = document.getElementById(field.id).value.trim();
//			if (!value) {
//				alert(`${field.name}을(를) 입력해주세요.`);
//				document.getElementById(field.id).focus();
//				return; // 함수 실행 중단
//			}
//		}
//
//		const formData = {
//			clientId: document.getElementById("clientId").value,
//			clientName: document.getElementById("clientName").value,
//			ceoName: document.getElementById("ceoName").value,
//			businessNumber: document.getElementById("businessNumber").value,
//			clientType: document.getElementById("clientType").value,
//			clientStatus: document.getElementById("clientStatus").value,
//			clientPhone: document.getElementById("clientPhone").value,
//			clientAddress: document.getElementById("clientAddress").value,
//		};
//
//		try {
//			const url = isEditMode
//				? `/business/api/clients/update/${formData.clientId}`
//				: "/business/api/clients/submit";
//
//			const method = isEditMode ? "PUT" : "POST";
//
//			const response = await fetch(url, {
//				method: method,
//				headers: {
//					"Content-Type": "application/json",
//					[csrfHeader]: csrfToken
//				},
//				body: JSON.stringify(formData),
//			});
//
//			if (response.ok) {
//				alert(isEditMode ? '거래처가 수정되었습니다.' : '거래처 등록이 성공적으로 제출되었습니다.');
//				clientAddModal.hide();
//				loadClients();
//			} else {
//				const errorText = await response.text();
//				alert((isEditMode ? '거래처 수정 실패: ' : '거래처 등록 실패: ') + errorText);
//			}
//		} catch (error) {
//			console.error('API 호출중 오류:', error);
//			alert('처리 중 오류가 발생했습니다.');
//		}
//	});
//
//});