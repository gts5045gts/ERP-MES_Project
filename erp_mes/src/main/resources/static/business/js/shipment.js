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

	const addBtn = document.getElementById("addBtn");
	if (!isBUSTeam && !isAUTLevel) {
		if (addBtn) addBtn.style.display = "none";
	}

	// 선택한 품목 정보를 저장할 배열
	let selectedProducts = [];

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
				{ header: '출하번호', name: 'shipmentId', align: 'center', sortable: true },
				{ header: '수주번호', name: 'orderId', align: 'center' },
				{ header: '거래처명', name: 'clientName', align: 'center' },
				{ header: '등록자 사원번호', name: 'empId', align: 'center' },
				{ header: '등록자', name: 'empName', align: 'center' },
				{
					header: '출하일', name: 'shipmentDate', align: 'center',
					sortable: true,
					formatter: function(value) {
						if (value.value) {
							return value.value.split('T')[0];
						}
						return value.value;
					}
				},
				{
					header: '납기일', name: 'deliveryDate', align: 'center',
					sortable: true,
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
							case 'READY':
								color = 'blue';
								statusText = '출하대기';
								break;
							case 'DELAY':
								color = 'green';
								statusText = '날짜지연';
								break;
							case 'PARTIAL':
								color = 'red';
								statusText = '부분출하';
								break;
							case 'COMPLETION':
								color = 'black';
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
				{ header: '수주수량', name: 'orderQty', align: 'center', sortable: true },
				{ header: '현재 출하수량', name: 'shipmentQty', align: 'center', sortable: true },
				{
					header: '진행상태', name: 'shipmentDetailStatus', align: 'center',
					formatter: function(value) {
						let color = '';
						let statusText = '';
						switch (value.value) {
							case 'READY':
								color = 'blue';
								statusText = '출하대기';
								break;
							case 'DELAY':
								color = 'green';
								statusText = '날짜지연';
								break;
							case 'PARTIAL':
								color = 'red';
								statusText = '부분출하';
								break;
							case 'COMPLETION':
								color = 'black';
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

			// 그 외 클릭: 상세 로드 및 수정 버튼 표시
			loadShipmentDetails(rowData.shipmentId);
		});
	};

	// 페이지 초기화 함수 호출
	initializePage();

	// 서버에서 목록/데이터 로드하는 함수들
	//--------------------------------------------------------

	let allShipment = [];

	function loadShipments() {
		fetch("/business/api/shipment")
			.then(response => response.json())
			.then(data => {
				allShipment = data;
				shipmentGrid.resetData(allShipment);
			})
			.catch(error => console.error("출하 목록 불러오기 오류:", error));
	}

	// 검색 버튼 클릭 시 실행
	function filterShipment() {
		const status = document.getElementById("shipmentStatus").value;
		const keyword = document.getElementById("combinedSearch").value.trim();
		const startDate = document.getElementById("inputDateSearch").value;
		const endDate = document.getElementById("inputDateEndSearch").value;

		let filteredData = allShipment;

		// 진행상태 필터
		if (status !== "ALL") {
			filteredData = filteredData.filter(order => order.shipmentStatus === status);
		}

		// 거래처명/발주번호 필터
		if (keyword) {
			filteredData = filteredData.filter(order =>
				(order.clientName && order.clientName.includes(keyword)) ||
				(order.shipmentId && order.shipmentId.includes(keyword))
			);
		}

		// 입고요청일 필터
		if (startDate || endDate) {
			filteredData = filteredData.filter(order => {
				// Grid 데이터의 필드명 'inputDate' 사용
				const deliveryDate = order.deliveryDate;
				if (!deliveryDate)
					return false;

				// 날짜 데이터가 유효한지 확인하고 범위 필터링
				if (startDate && endDate) {
					return deliveryDate >= startDate && deliveryDate <= endDate;
				} else if (startDate) {
					return deliveryDate >= startDate;
				} else if (endDate) {
					return deliveryDate <= endDate;
				}
				return false; // 날짜 데이터가 없으면 필터링
			});
		}

		shipmentGrid.resetData(filteredData);
	}

	// 검색 이벤트 바인딩
	document.getElementById("searchBtn").addEventListener("click", filterShipment);

	// 엔터키 검색
	document.getElementById("combinedSearch").addEventListener("keydown", function(e) {
		if (e.key === "Enter") {
			filterShipment();
		}
	});

	// 출하 상세 목록을 불러오는 함수
	function loadShipmentDetails(shipmentId) {
		fetch(`/business/api/shipment/${shipmentId}/details`)
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

	// 출하 모달창에서 수주 목록 불러오기
	function loadOrderListForModal() {
		fetch("/business/api/shipment/orders")
			.then(response => response.json())
			.then(data => {
				orderListGrid.resetData(data);
			})
			.catch(error => console.error("수주 목록 불러오기 오류:", error));
	}

	// 모달창에서 수주 상세 목록 불러오기
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
					{ header: '거래처', name: 'clientName', align: 'center', minwidth: 100 },
					{
						header: '수주일', name: 'orderDate', align: 'center', width: 100,
						formatter: function(value) {
							if (value.value) {
								return value.value.split('T')[0]; // T 문자를 기준으로 날짜만 추출
							}
							return value.value;
						}
					},
					{ header: '납품요청일', name: 'deliveryDate', align: 'center', width: 100 },
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
				const row = orderListGrid.getRow(ev.rowKey);
				const isChecked = row && row._attributes.checked;
				if (isChecked) orderListGrid.uncheck(ev.rowKey);
				else orderListGrid.check(ev.rowKey);
				updateSelectedItems(); // 체크 시마다 상세 갱신
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
					{ header: '수주번호', name: 'orderId', align: 'center', width: 140 },
					{ header: '품목명', name: 'productName', align: 'center', width: 90 },
					{ header: '재고량', name: 'stockQty', align: 'center', width: 75 },
					{ header: '수주수량', name: 'orderQty', align: 'center', width: 75 },
					{
						header: '필요출하수량', name: 'shipmentQty', align: 'center', minwidth: 80,
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
		let allDetails = [];

		// 기존 체크박스 상태 저장
		const prevCheckedMap = {};
		if (orderDetailGrid) {
			orderDetailGrid.getData().forEach((row, idx) => {
				prevCheckedMap[`${row.orderId}_${row.productName}`] = true;
			});
		}

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
					remainingQty: item.remainingQty, // 출하등록시 수주수량 > 출하수량일 경우 출하실패하도록 하기위한 필드
					// 여기서 서버의 orderQty 값을 shipmentQty에 할당
					shipmentQty: item.remainingQty
				}));

				allDetails = allDetails.concat(gridData);

			} catch (error) {
				console.error("수주 상세 목록 불러오기 오류:", error);
			}
		}

		// 병합된 모든 상세 목록 데이터로 그리드를 업데이트
		orderDetailGrid.resetData(allDetails);

		// 수주가 체크되면 해당 상세 목록 전체를 자동으로 체크
		if (orderDetailGrid.getData().length > 0) {
			orderDetailGrid.checkAll();
		}

		// 체크박스 상태 복원
		orderDetailGrid.getData().forEach((row, idx) => {
			if (prevCheckedMap[`${row.orderId}_${row.id}`]) {
				orderDetailGrid.check(idx);
			}
		});
	};

	// 폼 제출 이벤트 (출하 등록 및 수정)
	form.addEventListener("submit", async (event) => {
		event.preventDefault();

		// 상세 목록 그리드에서 체크된 행만 가져옴
		const checkedDetails = orderDetailGrid.getCheckedRows();
		const allDetails = orderDetailGrid.getData();

		// 출하 수량 유효성 검사
		for (const item of checkedDetails) {
			const shipmentQty = parseInt(item.shipmentQty);
			const orderQty = parseInt(item.orderQty);
			const remainingQty = parseInt(item.remainingQty);

			// 'orderQty'와 'remainingQty'를 비교하여 '부분출하' 상태를 판단
			const isPartialShipment = (orderQty > item.remainingQty) && (item.remainingQty > 0);

			if (isPartialShipment) {
				if (isNaN(shipmentQty) || shipmentQty <= 0) {
					alert(`부분출하 품목인 "${item.productName}"은(는) 출하 수량을 1개 이상 입력해야 합니다.`);
					return; // 유효성 검사 실패 시 함수 실행 중단
				}
			}

			if (shipmentQty > remainingQty) {
				alert(`"${item.productName}" 품목의 출하 수량(${shipmentQty})이 잔여 수량(${remainingQty})보다 많습니다.`);
				return;
			}
		}

		if (checkedDetails.length !== allDetails.length) {
			alert("해당 수주의 모든 상세 품목을 선택해야 출하 등록이 가능합니다.");
			return;
		}
		//		if (checkedDetails.length === 0) {
		//			alert("하나 이상의 품목을 선택하고 출하 수량을 입력해주세요.");
		//			return;
		//		}

		// 수주 건별로 데이터를 그룹화
		const shipmentsByOrderId = {};
		checkedDetails.forEach(item => {
			const orderId = item.orderId;
			// 해당 수주 건의 정보를 가져옴
			const orderData = orderListGrid.getData().find(row => row.orderId === orderId);

			if (!orderData) {
				console.error("수주 정보를 찾을 수 없습니다:", orderId);
				return;
			}

			if (!shipmentsByOrderId[orderId]) {
				shipmentsByOrderId[orderId] = {
					orderId: orderData.orderId,
					clientId: orderData.clientId,
					clientName: orderData.clientName,
					deliveryDate: orderData.deliveryDate,
					items: []
				};
			}

			// 출하 수량 유효성 검사 및 데이터 추가
			const shipmentQty = parseInt(item.shipmentQty);
			if (isNaN(shipmentQty) || shipmentQty < 0) {
				alert("출하 수량을 올바르게 입력해주세요.");
				return;
			}

			const orderQty = parseInt(item.orderQty);
			if (isNaN(orderQty)) {
				console.error("수주 수량이 누락되었습니다:", item.productName);
				alert("수주 수량이 누락되었습니다: 품목 " + item.productName);
				return;
			}

			shipmentsByOrderId[orderId].items.push({
				orderId: item.orderId,
				productId: item.productId,
				productName: item.productName,
				shipmentQty: shipmentQty,
				orderQty: orderQty,
			});
		});

		// 모든 수주 건에 대한 출하 등록을 순차적으로 요청
		const shipmentPayloads = Object.values(shipmentsByOrderId);

		const csrfToken = document.querySelector('meta[name="_csrf"]').content;
		const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

		try {
			let allSuccess = true;
			let lastShipmentId = null;

			for (const payload of shipmentPayloads) {
				console.log("전송될 페이로드:", payload);

				const res = await fetch("/business/api/shipment/submit", {
					method: "POST",
					headers: {
						"Content-Type": "application/json",
						[csrfHeader]: csrfToken
					},
					body: JSON.stringify(payload)
				});

				const responseText = await res.text();

				if (!res.ok) {
					allSuccess = false;
					//					const txt = await res.text();
					console.error("서버 응답 에러:", res.status, responseText);
					alert("출하 등록 실패: " + responseText);
					break; // 하나라도 실패하면 중단
				} else {
					try {
						const jsonResponse = JSON.parse(responseText);
						lastShipmentId = jsonResponse.shipmentId; // 응답에서 출하 번호를 받아서 저장
					} catch (e) {
						console.error("JSON 파싱 오류: 서버 응답이 유효한 JSON이 아닙니다.", e, "응답 텍스트:", responseText);
						allSuccess = false;
						alert("출하 등록은 성공했지만, 응답 처리 중 오류가 발생했습니다.");
						break;
					}
				}
			}

			if (allSuccess) {
				alert("출하 등록 완료!");
				shipmentAddModal.hide();
				loadShipments();
				if (lastShipmentId) {
					loadShipmentDetails(lastShipmentId);
				}

			}

		} catch (err) {
			console.error(err);
			alert("서버 통신 오류");
		}
	});

});