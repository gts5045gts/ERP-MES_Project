document.addEventListener("DOMContentLoaded", () => {
	// TUI Grid 인스턴스 변수로 선언
	let orderGrid;
	let orderDetailGrid;
	let productListGrid;

	const orderAddModalElement = document.getElementById('orderAddModal');
	const orderAddModal = new bootstrap.Modal(orderAddModalElement);
	const form = document.getElementById("orderAddForm");

	// 새로운 DOM 요소 변수
	const selectedItemsContainer = document.getElementById('selectedItemsContainer');
	const totalPriceElement = document.getElementById('totalPrice');
	const emptyMessage = document.getElementById('emptyMessage');

	// 선택한 품목 정보를 저장할 배열
	let selectedProducts = [];

	// TUI Grid 인스턴스들을 초기화하고 데이터를 불러오는 함수
	const initializePage = () => {
		// 수주 목록 그리드 초기화
		orderGrid = new tui.Grid({
			el: document.getElementById('orderGrid'),
			scrollX: false,
			scrollY: true,
			bodyHeight: 220,
			rowHeight: 'auto',
			minBodyHeight: 200,
			emptyMessage: '조회결과가 없습니다.',
			columns: [
				{ header: '수주 번호', name: 'orderId', align: 'center' },
				{ header: '거래처 번호', name: 'clientId', align: 'center' },
				{ header: '거래처명', name: 'clientName', align: 'center' },
				{ header: '사원 번호', name: 'empId', align: 'center' },
				{ header: '사원명', name: 'empName', align: 'center' },
				{ header: '수주일', name: 'orderDate', align: 'center' },
				{ header: '납기예정일', name: 'deliveryDate', align: 'center' },
				{ header: '수주수량', name: 'orderQty', align: 'center' },
				{ header: '수주금액', name: 'orderPrice', align: 'center' },
				{
					header: '수주상태',
					name: 'orderStatus',
					align: 'center',
					formatter: function(value) {
						let color = '';
						switch (value.value) {
							case 'RECEIVED': // 등록
								color = 'blue';
								break;
							case 'CANCELED': // 취소
								color = 'red';
								break;
							case 'PREPARING': // 납품 대기
								color = 'green';
								break;
							default: // 납품 완료 (DELIVERED)는 원래 색상
								color = 'black';
								break;
						}
						return `<span style="color: ${color}; font-weight: bold;">${value.value}</span>`;
					}
				}
			],
			data: []
		});

		// 수주 상세 목록을 위한 그리드 인스턴스
		orderDetailGrid = new tui.Grid({
			el: document.getElementById('orderDetailGrid'),
			scrollX: false,
			scrollY: true,
			bodyHeight: 220,
			minBodyHeight: 200,
			emptyMessage: '수주 목록의 행을 클릭하여 상세 정보를 확인하세요.',
			columns: [
				{ header: '수주 ID', name: 'orderDetailId', align: 'center' },
				{ header: '품목번호', name: 'productId', align: 'center' },
				{ header: '품목명', name: 'productName', align: 'left' },
				{ header: '단가', name: 'price', align: 'right' },
				{ header: '단위', name: 'unit', align: 'right' },
				{ header: '수량', name: 'quantity', align: 'center' },
				{ header: '총금액', name: 'totalPrice', align: 'right' }
			],
			data: []
		});

		// 페이지 로드 시 전체 수주 목록 불러오기
		loadOrders();

		// 수주 목록 그리드 행 클릭 이벤트 리스너
		orderGrid.on('click', (ev) => {
			const rowData = orderGrid.getRow(ev.rowKey);
			if (rowData) {
				const orderId = rowData.orderId;
				loadOrderDetails(orderId);
			}
		});
	};

	// 페이지 초기화 함수 호출
	initializePage();

	// 등록 버튼 클릭 시 모달창 열기
	document.getElementById("addBtn").addEventListener("click", () => {
		loadClientsForModal();
		orderAddModal.show();
	});

	// 모달이 완전히 표시된 후에 품목 리스트 그리드 초기화 및 데이터 로드
	orderAddModalElement.addEventListener('shown.bs.modal', () => {
		// productListGrid가 아직 생성되지 않았다면 초기화
		if (!productListGrid) {
			productListGrid = new tui.Grid({
				el: document.getElementById('productListGrid'),
				scrollX: false,
				scrollY: true,
				rowHeaders: ['checkbox'], // 체크박스 열 추가
				bodyHeight: 250,
				columns: [
					{ header: '품목번호', name: 'productId', align: 'center', width: 100 },
					{ header: '품목명', name: 'productName', align: 'left', minwidth: 170 },
					{ header: '단위', name: 'unit', align: 'center', width: 70 },
					{ header: '단가', name: 'price', align: 'center', minwidth: 90 }
				],
				columnOptions: {
					resizable: true // 컬럼 너비를 사용자가 조절할 수 있게 합니다.
				},
				data: []
			});

			// TUI Grid의 resize 메서드를 호출하여 크기를 강제로 재조정
			productListGrid.refreshLayout();

			// 이벤트 리스너를 한 번만 등록
			productListGrid.on('checkAll', () => updateSelectedItems());
			productListGrid.on('uncheckAll', () => updateSelectedItems());
			productListGrid.on('check', () => updateSelectedItems());
			productListGrid.on('uncheck', () => updateSelectedItems());

			// 행 클릭 이벤트 → 체크박스 토글
			productListGrid.on('click', (ev) => {
				if (ev.rowKey != null) {
					const checkedRowKeys = productListGrid.getCheckedRowKeys();
					if (checkedRowKeys.includes(ev.rowKey)) {
						productListGrid.uncheck(ev.rowKey);
					} else {
						productListGrid.check(ev.rowKey);
					}
					updateSelectedItems();
				}
			});
		}

		loadProductsForModal();
	});

	// 모달이 완전히 닫힌 후 그리드 파괴
	orderAddModalElement.addEventListener('hidden.bs.modal', () => {
		if (productListGrid) {
			productListGrid.destroy();
			productListGrid = null;
			selectedProducts = []; // 선택된 품목 배열 초기화
			renderSelectedItems(); // 화면 초기화
		}
	});

	// 페이지 로딩 시 전체 수주 목록 불러오기
	function loadOrders() {
		fetch("/business/api/orders")
			.then(response => response.json())
			.then(data => {
				orderGrid.resetData(data);
			})
			.catch(error => console.error("수주 목록 불러오기 오류:", error));
	}



	// 수주 상세 목록을 불러오는 함수
	function loadOrderDetails(orderId) {
		fetch(`/business/api/orders/${orderId}/details`)
			.then(response => {
				if (!response.ok) {
					throw new Error('네트워크 응답이 올바르지 않습니다.');
				}
				return response.json();
			})
			.then(data => {
				orderDetailGrid.resetData(data);
			})
			.catch(error => console.error("수주 상세 목록 불러오기 오류:", error));
	}

	// 수주 등록 모달창 - 거래처 리스트 불러오기 (매출사만)
	function loadClientsForModal() {
		fetch("/business/api/clients/order-type")
			.then(response => response.json())
			.then(data => {
				const selectElement = document.getElementById("clientId");
				selectElement.innerHTML = '<option value="">선택</option>';
				data.forEach(client => {
					const option = document.createElement("option");
					option.value = client.clientId;
					option.textContent = client.clientName;
					selectElement.appendChild(option);
				});
			})
			.catch(error => console.error("거래처 목록 불러오기 오류:", error));
	}

	// 수주 등록 모달창 - 품목 리스트 불러오기
	function loadProductsForModal() {
		fetch("/business/api/products")
			.then(response => response.json())
			.then(data => {
				if (productListGrid) {
					productListGrid.resetData(data);
				}
			})
			.catch(error => console.error("품목 목록 불러오기 오류:", error));
	}

	// 선택된 품목 목록을 업데이트하는 함수
	const updateSelectedItems = () => {
		selectedProducts = productListGrid.getCheckedRows();
		renderSelectedItems();
	};

	const renderSelectedItems = () => {
		selectedItemsContainer.innerHTML = '';
		let total = 0;

		if (selectedProducts.length === 0) {
			selectedItemsContainer.appendChild(emptyMessage);
		} else {
			selectedProducts.forEach((item) => {
				const price = parseInt(item.price) || 0; 

				const itemDiv = document.createElement('div');
				itemDiv.classList.add('d-flex', 'align-items-center', 'mb-2');
				itemDiv.dataset.rowKey = item.rowKey;
				itemDiv.dataset.productId = item.productId;

				const itemHtml = `
	                <div class="d-flex align-items-center w-100">
	                    <span class="me-2">${item.productName}</span>
	                    <input type="number" 
	                        class="form-control form-control-sm me-2" 
	                        style="width: 60px;" 
	                        value="1" min="1" 
	                        data-product-price="${price}" 
	                        data-unit="${item.unit}" 
	                        onchange="updateItemPrice(this, '${item.rowKey}')">
	                    <span class="me-2">×</span> 
	                    <span class="me-2">${price.toLocaleString()} 원</span>
						<span class="me-2">=</span> 
	                    <span class="item-total-price me-auto text-end">₩${(price * 1).toLocaleString()}</span>
	                    <button type="button" class="btn-close ms-2" aria-label="Close" onclick="removeItem('${item.productId}')"></button>
	                </div>
	            `;

				itemDiv.innerHTML = itemHtml;
				selectedItemsContainer.appendChild(itemDiv);

				total += price; 
			});
		}

		totalPriceElement.textContent = `₩${total.toLocaleString()}`;
	};

	// 개별 품목의 수량을 변경했을 때 총 가격을 업데이트
	window.updateItemPrice = (inputElement, rowKey) => {
		const quantity = parseInt(inputElement.value);
		const price = parseInt(inputElement.dataset.productPrice);
		const newTotal = quantity * price;

		// 개별 품목의 총 가격 업데이트
		const itemDiv = selectedItemsContainer.querySelector(`[data-row-key='${rowKey}']`);
		itemDiv.querySelector('.item-total-price').textContent = `₩${newTotal.toLocaleString()}`;

		// 전체 총 금액 업데이트
		updateTotalPrice();
	};

	// 총 금액을 다시 계산하는 함수
	const updateTotalPrice = () => {
		let total = 0;
		const itemElements = selectedItemsContainer.querySelectorAll('.item-total-price');
		itemElements.forEach(el => {
			const priceText = el.textContent.replace('₩', '').replace(/,/g, '');
			total += parseInt(priceText);
		});
		totalPriceElement.textContent = `₩${total.toLocaleString()}`;
	};

	// 수주 품목 목록에서 항목을 제거
	window.removeItem = (productId) => {
		// TUI Grid에서 해당 체크박스 해제
		const row = productListGrid.getData().find(item => item.productId === productId);
		if (row) {
			const rowKey = productListGrid.getIndexOfRow(row);
			productListGrid.uncheck(rowKey);
		}

		// DOM에서 해당 항목 제거
		const itemDiv = selectedItemsContainer.querySelector(`[data-product-id='${productId}']`);
		if (itemDiv) {
			itemDiv.remove();
		}

		// selectedProducts 배열에서도 제거
		selectedProducts = selectedProducts.filter(item => item.productId !== productId);

		// 항목 제거 후 빈 메시지 표시 및 총 금액 업데이트
		if (selectedProducts.length === 0) {
			selectedItemsContainer.appendChild(emptyMessage);
		}
		updateTotalPrice();
	};

	// 품목 검색 버튼 이벤트
	document.getElementById("searchProductBtn").addEventListener("click", () => {
		const keyword = document.getElementById("productSearch").value;
		fetch(`/business/api/products/search?keyword=${encodeURIComponent(keyword)}`)
			.then(response => response.json())
			.then(data => {
				if (productListGrid) {
					productListGrid.resetData(data);
				}
			})
			.catch(error => console.error("품목 검색 오류:", error));
	});

	// 폼 제출 이벤트 (수주 등록)
	form.addEventListener("submit", async (event) => {
		event.preventDefault();

		// 선택된 품목 가져오기
		const orderItems = [];
		const itemElements = selectedItemsContainer.querySelectorAll('div[data-row-key]');
		itemElements.forEach(div => {
			const rowKey = div.dataset.rowKey;
			const item = productListGrid.getRow(rowKey);
			const quantity = parseInt(div.querySelector('input').value);
			orderItems.push({
				itemId: item.productId,
				itemPrice: item.price,
				itemQty: quantity
			});
		});

		if (orderItems.length === 0) {
			alert('하나 이상의 품목을 선택해주세요.');
			return;
		}

		const formData = {
			clientId: document.getElementById("clientId").value,
			deliveryDate: document.getElementById("deliveryDate").value,
			orderItems: orderItems
		};

		const csrfToken = document.querySelector('meta[name="_csrf"]').content;
		const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

		try {
			const response = await fetch("/business/api/orders/submit", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
					[csrfHeader]: csrfToken
				},
				body: JSON.stringify(formData),
			});

			if (response.ok) {
				alert('수주 등록이 완료되었습니다.');
				orderAddModal.hide();
				loadOrders(); // 목록 새로고침
			} else {
				const errorText = await response.text();
				alert('수주 등록 실패: ' + errorText);
			}
		} catch (error) {
			console.error('API 호출 중 오류:', error);
			alert('처리 중 오류가 발생했습니다.');
		}
	});
});