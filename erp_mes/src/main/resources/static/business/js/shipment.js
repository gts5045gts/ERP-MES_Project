document.addEventListener("DOMContentLoaded", () => {
	// TUI Grid 인스턴스 변수로 선언
	let shipmentGrid;
	let shipmentDetailGrid;
	let orderListGrid;
	let orderDetailGrid; // 상세 목록 그리드 변수 추가

	const shipmentAddModalElement = document.getElementById('shipmentAddModal');
	const shipmentAddModal = new bootstrap.Modal(shipmentAddModalElement);
	const form = document.getElementById("shipmentAddForm");

	// 새로운 DOM 요소 변수
	const selectedItemsContainer = document.getElementById('selectedItemsContainer');
	const totalPriceElement = document.getElementById('totalPrice');
	const emptyMessage = document.getElementById('emptyMessage');

	// 오늘 날짜를 'YYYY-MM-DD' 형식으로 가져오는 코드
	const today = new Date();
	const year = today.getFullYear();
	const month = String(today.getMonth() + 1).padStart(2, '0');
	const day = String(today.getDate()).padStart(2, '0');
	const todayString = `${year}-${month}-${day}`;

	// 선택한 품목 정보를 저장할 배열
	let selectedProducts = [];

	// 편집 모드 관련
	let isEditMode = false;
	let editShipmentId = null;
	let editItems = []; // 서버에서 불러온 편집 대상 품목들

	// 동적으로 수정 버튼 생성 (등록 버튼 옆에)
	const addBtn = document.getElementById("addBtn");
	let editBtn = document.getElementById("editBtn");
	if (!editBtn) {
		editBtn = document.createElement("button");
		editBtn.id = "editBtn";
		editBtn.type = "button";
		editBtn.className = "btn btn-secondary ms-2";
		editBtn.textContent = "수정";
		editBtn.style.display = "none"; // 기본 숨김
		// addBtn이 있는 곳의 부모에 추가
		if (addBtn && addBtn.parentNode) {
			addBtn.parentNode.insertBefore(editBtn, addBtn.nextSibling);
		} else {
			document.body.appendChild(editBtn);
		}
	}

	// 수정 버튼 클릭 이벤트
	editBtn.addEventListener('click', () => {
		const focused = shipmentGrid.getFocusedCell();
		if (!focused) {
			alert("수정할 행을 선택해주세요.");
			return;
		}

		const rowData = shipmentGrid.getRow(focused.rowKey);
		openEditModal(rowData.shipmentId, rowData);
	});

	// TUI Grid 인스턴스들을 초기화하고 데이터를 불러오는 함수
	const initializePage = () => {
		// 출하 목록 그리드 초기화
		shipmentGrid = new tui.Grid({
			el: document.getElementById('shipmentGrid'),
			scrollX: false,
			scrollY: true,
			bodyHeight: 200,
			rowHeight: 'auto',
			minBodyHeight: 200,
			emptyMessage: '조회결과가 없습니다.',
			columns: [
				{ header: '출하번호', name: 'shipmentId', align: 'center' },
				{ header: '수주번호', name: 'orderId', align: 'center' },
				{ header: '거래처 번호', name: 'clientId', align: 'center' },
				{ header: '거래처명', name: 'clientName', align: 'center' },
				{ header: '등록자 사원번호', name: 'empId', align: 'center' },
				{ header: '등록자', name: 'empName', align: 'center' },
				{
					header: '출하일', name: 'shipmentDate', align: 'center',
					formatter: function(value) {
						if (value.value) {
							return value.value.split('T')[0];
						}
						return value.value;
					}
				},
				{
					header: '납기일', name: 'deliveryDate', align: 'center',
					editor: {
						type: 'datePicker',
						options: {
							format: 'yyyy-MM-dd',
							minDate: new Date()
						}
					},
					formatter: function(value) {
						if (value.value) {
							return value.value.split('T')[0];
						}
						return '';
					}
				},
				{
					header: '진행상태', name: 'shipmentStatus', align: 'center',
					formatter: function(value) {
						let color = '';
						let statusText = '';
						switch (value.value) {
							case 'PARTIAL':
								color = 'blue';
								statusText = '부분출하';
								break;
							case 'DELAY':
								color = 'red';
								statusText = '날짜지연';
								break;
							case 'COMPLETION':
								color = 'green';
								statusText = '출하완료';
								break;
						}
						return `<span style="color: ${color}; font-weight: bold;">${statusText}</span>`;
					}
				}
			],
			data: []
		});

		// 출하 상세 목록을 위한 그리드 인스턴스
		shipmentDetailGrid = new tui.Grid({
			el: document.getElementById('shipmentDetailGrid'),
			scrollX: false,
			scrollY: true,
			bodyHeight: 200,
			minBodyHeight: 200,
			emptyMessage: '출하 목록의 행을 클릭하여 상세 정보를 확인하세요.',
			columns: [
				{ header: 'No.', name: 'id', align: 'center', width: 70 },
				{ header: '출하번호', name: 'shipmentId', align: 'center' },
				{ header: '품목번호', name: 'productId', align: 'center' },
				{ header: '품목명', name: 'productName', align: 'center' },
				{ header: '수주 수량', name: 'orderQty', align: 'center' },
				{ header: '출하 수량', name: 'shipmentQty', align: 'center' },
				{
					header: '진행상태', name: 'shipmentDetailStatus', align: 'center',
					formatter: function(value) {
						let color = '';
						let statusText = '';
						switch (value.value) {
							case 'NOTSHIPPED':
								color = 'blue';
								statusText = '미출하';
								break;
							case 'DELAY':
								color = 'red';
								statusText = '날짜지연';
								break;
							case 'COMPLETION':
								color = 'green';
								statusText = '출하완료';
								break;
						}
						return `<span style="color: ${color}; font-weight: bold;">${statusText}</span>`;
					}
				}
			],
			data: []
		});

		// 페이지 로드 시 전체 출하 목록 불러오기
		loadShipments();

		shipmentGrid.on('click', async (ev) => {
			const rowData = shipmentGrid.getRow(ev.rowKey);
			if (!rowData) {
				editBtn.style.display = "none";
				editBtn.removeAttribute('data-shipment-id');
				return;
			}

			// 출하상태 컬럼 클릭 시 취소 로직
			if (ev.columnName === 'shipmentStatus') {
				if (rowData.shipmentStatus === 'CANCELED') {
					alert("이미 취소된 출하입니다.");
					return;
				}

				editBtn.style.display = "none";

				if (confirm("출하를 취소하시겠습니까?")) {
					const shipmentId = rowData.shipmentId;
					try {
						const csrfToken = document.querySelector('meta[name="_csrf"]').content;
						const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

						const res = await fetch(`/shipment/api/shipments/${shipmentId}/cancel`, {
							method: "PUT",
							headers: {
								"Content-Type": "application/json",
								[csrfHeader]: csrfToken
							}
						});

						if (!res.ok) {
							throw new Error(await res.text());
						}

						shipmentGrid.setValue(ev.rowKey, 'shipmentStatus', 'CANCELED');
						alert("출하가 취소되었습니다.");
						
						// 상세 목록도 업데이트
						loadShipmentDetails(shipmentId);
					} catch (err) {
						console.error("출하 취소 실패:", err);
						alert("출하 취소 실패: " + err.message);
					}
				}
				return;
			}

			// 그 외 클릭: 상세 로드 및 수정 버튼 표시
			loadShipmentDetails(rowData.shipmentId);

			if (rowData.shipmentStatus === 'REGISTERED') {
				editBtn.style.display = "inline-block";
				editBtn.dataset.shipmentId = rowData.shipmentId;
			} else {
				editBtn.style.display = "none";
				editBtn.removeAttribute('data-shipment-id');
			}
		});
	};

	// 페이지 초기화 함수 호출
	initializePage();

	// 서버에서 목록/데이터 로드하는 함수들
	//--------------------------------------------------------

	// 페이지 로딩 시 전체 출하 목록 불러오기
	function loadShipments() {
		fetch("/business/api/shipments")
			.then(response => response.json())
			.then(data => {
				shipmentGrid.resetData(data);
			})
			.catch(error => console.error("출하 목록 불러오기 오류:", error));
	}

	// 출하 상세 목록을 불러오는 함수
	function loadShipmentDetails(shipmentId) {
		fetch(`/shipment/api/shipments/${shipmentId}/details`)
			.then(response => {
				if (!response.ok) {
					throw new Error('네트워크 응답이 올바르지 않습니다.');
				}
				return response.json();
			})
			.then(data => {
				shipmentDetailGrid.resetData(data);
			})
			.catch(error => console.error("출하 상세 목록 불러오기 오류:", error));
	}
	
	// 모달창에서 수주 목록 불러오기 (API 호출로 변경)
	function loadOrderListForModal() {
		fetch("/business/api/shipment/orders")
			.then(response => response.json())
			.then(data => {
				orderListGrid.resetData(data);
			})
			.catch(error => console.error("수주 목록 불러오기 오류:", error));
	}
	
	// 모달창에서 수주 상세 목록 불러오기 (임시 데이터 사용)
	function loadOrderDetailGrid(orderId) {
		fetch(`/business/api/shipment/ordersDetail?orderId=${encodeURIComponent(orderId)}`)
			.then(response => {
		    	if (!response.ok) {
		    		throw new Error('네트워크 응답이 올바르지 않습니다.');
		    	}
		    	return response.json();
		    })
		    .then(data => {
				// 서버에서 받은 데이터를 기반으로 그리드에 표시할 데이터 배열을 만듦
				const gridData = data.map(item => ({
					...item,
				    // 여기서 서버의 orderQty 값을 shipmentQty에 할당
				    shipmentQty: item.orderQty 
				}));
				
				if (orderDetailGrid) {
					// 수정된 데이터 배열로 그리드를 업데이트
				    orderDetailGrid.resetData(gridData);
				}
		    })
		    .catch(error => console.error("수주 상세 목록 불러오기 오류:", error));
	}


	//--------------------------------------------------------------------------------------

	// 모달/품목 선택 UI 관련
	// ----------------------------------------------------------------------------------
	if (addBtn) {
		addBtn.addEventListener("click", async () => {
			isEditMode = false;
			editShipmentId = null;
			editItems = [];

			document.getElementById('shipmentModalTitle').textContent = '출하 등록';
			document.getElementById('shipmentSubmitBtn').textContent = '등록';

			const clientSelect = document.getElementById("clientId");
			if (clientSelect) clientSelect.disabled = false;

			shipmentAddModal.show();
		});
	}

	// 모달이 완전히 표시된 후에 품목 리스트 그리드 초기화 및 데이터 로드
	shipmentAddModalElement.addEventListener('shown.bs.modal', async () => {
		
		if (!orderListGrid) {
			orderListGrid = new tui.Grid({
				el: document.getElementById('orderListGrid'),
				scrollX: false,
				scrollY: true,
				rowHeaders: ['checkbox'],
				bodyHeight: 280,
				columns: [
					{ header: '수주번호', name: 'orderId', align: 'center', width: 140 },
					{ header: '거래처', name: 'clientName', align: 'center', width: 140 },
					{
						header: '수주일', name: 'orderDate', align: 'center',
						formatter: function(value) {
							if (value.value) {
								return value.value.split('T')[0]; // T 문자를 기준으로 날짜만 추출
							}
							return value.value;
						}
					},
					{ header: '납품요청일', name: 'deliveryDate', align: 'center', width: 118 },
				],
				columnOptions: {
					resizable: true
				},
				data: []
			});
			orderListGrid.refreshLayout();

			// 수주 리스트 그리드 클릭 이벤트
			orderListGrid.on('click', (ev) => {
				if (!ev || ev.rowKey == null) return;

				const rowKey = ev.rowKey;

				if (ev.columnName === '_checked') {
				    // 체크박스 클릭 시 → 그리드 기본 동작(check/uncheck)에 맡기고 상세 갱신만 실행
				    setTimeout(() => updateSelectedItems(), 0);
				} else {
				    // 셀 클릭 시 → 체크박스 토글 + 상세 갱신
					const row = orderListGrid.getRow(rowKey);
					const isChecked = row && row._attributes.checked;
				    if (isChecked) {
				        orderListGrid.uncheck(rowKey);
				    } else {
				    	orderListGrid.check(rowKey);
				    }
				        updateSelectedItems();
				    }
			});

			orderListGrid.on('checkAll', () => updateSelectedItems());
			orderListGrid.on('uncheckAll', () => updateSelectedItems());
			orderListGrid.on('check', () => updateSelectedItems());
			orderListGrid.on('uncheck', () => updateSelectedItems());
		}

		// 상세 목록 그리드 초기화
		if (!orderDetailGrid) {
			orderDetailGrid = new tui.Grid({
				el: document.getElementById('orderDetailGrid'),
				scrollX: false,
				scrollY: true,
				bodyHeight: 280,
				rowHeaders: ['checkbox'],
				columns: [
					{ header: '수주번호', name: 'orderId', align: 'center', width: 140},
					{ header: '품목명', name: 'productName', align: 'center' },
					{ header: '재고량', name: 'stockQty', align: 'center' },
					{ 
						header: '출하수량', name: 'shipmentQty', align: 'center',
						editor: 'text' 
					},
				],
				columnOptions: {
					resizable: true
				},
				data: []
			});
			
			orderDetailGrid.refreshLayout();
		}

		await loadOrderListForModal();
		orderDetailGrid.resetData([]);
	});


	// 모달이 완전히 닫힌 후 그리드 파괴 및 상태 리셋
	shipmentAddModalElement.addEventListener('hidden.bs.modal', () => {
		if (orderListGrid) {
			orderListGrid.destroy();
			orderListGrid = null;
			orderDetailGrid.destroy();
			orderDetailGrid = null;
		}
		selectedProducts = [];
		isEditMode = false;
		editShipmentId = null;
		editItems = [];
		const titleEl = document.getElementById('shipmentModalTitle');
		if (titleEl) titleEl.textContent = '출하 등록';
		const submitBtn = document.getElementById('shipmentSubmitBtn');
		if (submitBtn) submitBtn.textContent = '등록';
		form.reset();
	});

	// 선택된 품목 목록을 업데이트하는 함수 (체크박스 기준)
	const updateSelectedItems = async () => {
		if (!orderListGrid) return;

		const checkedRows = orderListGrid.getCheckedRows();
//		const checkedOrderIds = checkedRows.map(row => row.orderId);
		let allDetails = [];
		
		for (const row of checkedRows) {
			try {
		    	const response = await fetch(`/business/api/shipment/ordersDetail?orderId=${encodeURIComponent(row.orderId)}`);
		        if (!response.ok) {
		        	throw new Error('네트워크 응답이 올바르지 않습니다.');
		        }
		        const data = await response.json();
		                
		        // 서버에서 받은 데이터를 기반으로 그리드에 표시할 데이터 배열을 만듦
		        const gridData = data.map(item => ({
		        	...item,
		            // 여기서 서버의 orderQty 값을 shipmentQty에 할당
		            shipmentQty: item.orderQty 
		        }));
		                
		       allDetails = allDetails.concat(gridData);

		     } catch (error) {
		           console.error("수주 상세 목록 불러오기 오류:", error);
		       }
		 }
		        
		 // 병합된 모든 상세 목록 데이터로 그리드를 업데이트
		 orderDetailGrid.resetData(allDetails);
		
//		// 실제 상세 목록 데이터 가져오기 (예시)
		const allRowKeys = orderDetailGrid.getRowCount(); // 행의 개수
    	for (let i = 0; i < allRowKeys; i++) {
        	orderDetailGrid.check(i);
    	}
	};
	
	// 폼 제출 이벤트 (출하 등록 및 수정)
	form.addEventListener("submit", async (event) => {
		event.preventDefault();

		const items = orderDetailGrid.getData().map(item => {
			const shipmentQty = parseInt(item.shipmentQty) || 0;
			return {
				orderId: item.orderId,
				productName: item.productName,
				shipmentQty: shipmentQty,
			};
		});

		if (items.length === 0) {
			alert("하나 이상의 품목을 선택하고 출하 수량을 입력해주세요.");
			return;
		}
		
		// 거래처 정보 가져오기 (가상)
		const clientData = orderListGrid.getCheckedRows()[0];
		if (!clientData) {
			alert("수주를 선택해주세요.");
			return;
		}
		
		const clientId = clientData.clientId;
		const clientName = clientData.clientName;
		const deliveryDate = clientData.deliveryDate;

		const payload = {
			clientId: clientId,
			clientName: clientName,
			deliveryDate: deliveryDate,
			items: items
		};

		console.log("전송될 페이로드:", payload);

		const csrfToken = document.querySelector('meta[name="_csrf"]').content;
		const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

		try {
			let res;
			if (isEditMode && editShipmentId) {
				res = await fetch(`/shipment/api/shipments/${editShipmentId}`, {
					method: "PUT",
					headers: {
						"Content-Type": "application/json",
						[csrfHeader]: csrfToken
					},
					body: JSON.stringify(payload)
				});
			} else {
				res = await fetch("/shipment/api/shipments/submit", {
					method: "POST",
					headers: {
						"Content-Type": "application/json",
						[csrfHeader]: csrfToken
					},
					body: JSON.stringify(payload)
				});
			}

			if (res.ok) {
				const j = await res.json();
				if (isEditMode) {
					alert("출하 수정 완료: " + (j.shipmentId || editShipmentId));
				} else {
					alert("출하 등록 완료: " + j.shipmentId);
				}
				shipmentAddModal.hide();
				loadShipments();

				const updatedShipmentId = isEditMode ? editShipmentId : j.shipmentId;
				loadShipmentDetails(updatedShipmentId);

			} else {
				const txt = await res.text();
				console.error("서버 응답 에러:", res.status, txt);
				alert((isEditMode ? "수정 실패: " : "등록 실패: ") + txt);
			}
		} catch (err) {
			console.error(err);
			alert("서버 통신 오류");
		}
	});

	// 편집 모달 열기 (기존 출하 불러와서 채우기)
	async function openEditModal(shipmentId) {
		try {
			const res = await fetch(`/shipment/api/shipments/${shipmentId}`);
			if (!res.ok) {
				throw new Error("출하 정보를 불러오지 못했습니다.");
			}
			const shipment = await res.json();

			isEditMode = true;
			editShipmentId = shipmentId;

			document.getElementById('shipmentModalTitle').textContent = '출하 수정';
			document.getElementById('shipmentSubmitBtn').textContent = '수정';

			editItems = shipment.items || [];

			shipmentAddModal.show();

		} catch (err) {
			console.error("편집 모달 오픈 실패:", err);
			alert("편집 모달을 열 수 없습니다: " + err.message);
		}
	}
	
	// 검색 버튼 클릭 이벤트
	document.getElementById("searchBtn").addEventListener("click", () => {
		const status = document.getElementById("shipmentStatus").value;
		const keyword = document.getElementById("cliSearch").value;
		
		let url = `/shipment/api/shipments/search?shipmentStatus=${status}&clientName=${encodeURIComponent(keyword)}`;
		
		fetch(url)
			.then(response => response.json())
			.then(data => {
				shipmentGrid.resetData(data);
				shipmentDetailGrid.resetData([]); // 검색 시 상세 그리드 초기화
			})
			.catch(error => console.error("출하 목록 검색 오류:", error));
	});

});