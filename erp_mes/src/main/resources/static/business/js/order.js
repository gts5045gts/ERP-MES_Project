document.addEventListener("DOMContentLoaded", () => {
	// TUI Grid 인스턴스
	const orderGrid = new tui.Grid({
		el: document.getElementById('orderGrid'),
		scrollX: false,
		scrollY: true,
		bodyHeight: 350,
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
			{ header: '수주상태', name: 'orderStatus', align: 'center' }
		],
		data: []
	});

	// 품목 리스트를 위한 별도의 Grid
	const productListGrid = new tui.Grid({
		el: document.getElementById('productListGrid'),
		scrollX: false,
		scrollY: true,
		rowHeaders: ['checkbox'], // 체크박스 열 추가
		bodyHeight: 290,
		columns: [
			{ header: '품목 ID', name: 'productId', align: 'center' },
			{ header: '품목명', name: 'productName', align: 'center' },
			{ header: '단위', name: 'unit', align: 'center' },
			{ header: '단가', name: 'price', align: 'right' }
		],
		data: []
	});

	const orderAddModal = new bootstrap.Modal(document.getElementById('orderAddModal'));
	const form = document.getElementById("orderAddForm");

	// 새로운 DOM 요소 변수
	const selectedItemsContainer = document.getElementById('selectedItemsContainer');
	const totalPriceElement = document.getElementById('totalPrice');
	const emptyMessage = document.getElementById('emptyMessage');

	// 선택한 품목 정보를 저장할 배열
	let selectedProducts = [];

	// 페이지 로딩 시 전체 수주 목록 불러오기
	const loadOrders = () => {
		fetch("/business/api/orders")
			.then(response => response.json())
			.then(data => {
				orderGrid.resetData(data);
			})
			.catch(error => console.error("수주 목록 불러오기 오류:", error));
	};
	loadOrders();

	// 등록 버튼 클릭 시 모달창 열기
	document.getElementById("addBtn").addEventListener("click", () => {
		// 모달창 열기 전 데이터 로드
		loadClientsForModal();
		loadProductsForModal();
		// 기타 필드 초기화
		orderAddModal.show();
	});

	// 수주 등록 모달창 - 거래처 리스트 불러오기 (매출사만)
	const loadClientsForModal = () => {
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
	};

	// 수주 등록 모달창 - 품목 리스트 불러오기
	const loadProductsForModal = () => {
		fetch("/business/api/products")
			.then(response => response.json())
			.then(data => {
				productListGrid.resetData(data);
			})
			.catch(error => console.error("품목 목록 불러오기 오류:", error));
	};

	// 품목 리스트 그리드에서 체크박스 클릭 시 발생하는 이벤트
	productListGrid.on('checkAll', () => updateSelectedItems());
	productListGrid.on('uncheckAll', () => updateSelectedItems());
	productListGrid.on('check', () => updateSelectedItems());
	productListGrid.on('uncheck', () => updateSelectedItems());

	// 선택된 품목 목록을 업데이트하는 함수
	const updateSelectedItems = () => {
		selectedProducts = productListGrid.getCheckedRows();
		renderSelectedItems();
	};

	// 선택된 품목을 DOM에 렌더링하는 함수
	const renderSelectedItems = () => {
		selectedItemsContainer.innerHTML = ''; // 기존 목록 초기화
		let total = 0;

		if (selectedProducts.length === 0) {
			selectedItemsContainer.appendChild(emptyMessage);
		} else {
			selectedProducts.forEach((item, index) => {
				const itemDiv = document.createElement('div');
				itemDiv.classList.add('d-flex', 'align-items-center', 'mb-2');
				itemDiv.dataset.rowKey = item.rowKey;

				const itemHtml = `
		                <div class="d-flex align-items-center w-100">
		                    <span class="me-2">${item.productName}</span>
		                    <input type="number" class="form-control form-control-sm me-2" style="width: 60px;" value="1" min="1" data-product-price="${item.price}" data-unit="${item.unit}" onchange="updateItemPrice(this, '${item.rowKey}')">
		                    <span class="me-2">${item.unit}</span>
		                    <span class="me-2">${item.price.toLocaleString()} 원</span>
		                    <span class="item-total-price me-auto text-end">₩${(item.price * 1).toLocaleString()}</span>
		                    <button type="button" class="btn-close ms-2" aria-label="Close" onclick="removeItem('${item.rowKey}')"></button>
		                </div>
		            `;
				itemDiv.innerHTML = itemHtml;
				selectedItemsContainer.appendChild(itemDiv);
				total += item.price;
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
	window.removeItem = (rowKey) => {
		productListGrid.uncheck(rowKey);
		updateSelectedItems();
	};

	// 품목 검색 버튼 이벤트
	document.getElementById("searchProductBtn").addEventListener("click", () => {
		const keyword = document.getElementById("productSearch").value;
		fetch(`/business/api/products/search?keyword=${encodeURIComponent(keyword)}`)
			.then(response => response.json())
			.then(data => {
				productListGrid.resetData(data);
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