document.addEventListener("DOMContentLoaded", () => {
	// TUI Grid 인스턴스 변수로 선언
	let purchaseGrid;
	let purchaseDetailGrid;
	let materialListGrid; // 자재 리스트 그리드

	const purchaseAddModalElement = document.getElementById('purchaseAddModal');
	const purchaseAddModal = new bootstrap.Modal(purchaseAddModalElement);
	const form = document.getElementById("purchaseAddForm");

	// 새로운 DOM 요소 변수
	const selectedMaterialsContainer = document.getElementById('selectedItemsContainer');
	const totalPriceElement = document.getElementById('totalPrice');
	const emptyMessage = document.getElementById('emptyMessage');

	// 오늘 날짜를 'YYYY-MM-DD' 형식으로 가져오는 코드
	const today = new Date();
	const year = today.getFullYear();
	const month = String(today.getMonth() + 1).padStart(2, '0');
	const day = String(today.getDate()).padStart(2, '0');
	const todayString = `${year}-${month}-${day}`;

	// 선택한 품목 정보를 저장할 배열
	let selectedMaterials = [];

	// 편집 모드 관련
	let isEditMode = false;
	let editPurchaseId = null; // 
	let editMaterials = []; // 서버에서 불러온 편집 대상 품목들

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
		// addBtn이 있는 곳의 부모에 추가 (존재하지 않으면 body에 append)
		if (addBtn && addBtn.parentNode) {
			addBtn.parentNode.insertBefore(editBtn, addBtn.nextSibling);
		} else {
			document.body.appendChild(editBtn);
		}
	}

	// 수정 버튼 클릭 이벤트
	editBtn.addEventListener('click', () => {
		console.log("수정 버튼 클릭됨");

		const focused = purchaseGrid.getFocusedCell();
		if (!focused) {
			alert("수정할 행을 선택해주세요.");
			return;
		}

		const rowData = purchaseGrid.getRow(focused.rowKey);
		openEditModal(rowData.purchaseId, rowData);
	});

	// TUI Grid 인스턴스들을 초기화하고 데이터를 불러오는 함수
	const initializePage = () => {
		// 발주 목록 그리드 초기화
		purchaseGrid = new tui.Grid({
			el: document.getElementById('purchaseGrid'),
			scrollX: false,
			scrollY: true,
			bodyHeight: 200,
			rowHeight: 'auto',
			minBodyHeight: 200,
			emptyMessage: '조회결과가 없습니다.',
			columns: [
				{ header: '발주번호', name: 'purchaseId', align: 'center' },
				{ header: '거래처 번호', name: 'clientId', align: 'center' },
				{ header: '거래처명', name: 'clientName', align: 'center' },
				{ header: '등록자 사원번호', name: 'empId', align: 'center' },
				{ header: '등록자', name: 'empName', align: 'center' },
				{
					header: '발주일', name: 'purchaseDate', align: 'center',
					formatter: function(value) {
						if (value.value) {
							return value.value.split('T')[0];
						}
						return value.value;
					}
				},
				{
					header: '입고예정일', name: 'inputDate', align: 'center',
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
				{ header: '발주수량', name: 'totalPurchaseQty', align: 'center' },
				{
					header: '발주금액', name: 'totalPurchasePrice', align: 'center',
					formatter: function(value) {
						if (value.value) {
							return value.value.toLocaleString();
						}
						return value.value;
					}
				},
				{
					header: '발주상태', name: 'purchaseStatus', align: 'center',
					formatter: function(value) {
						let color = '';
						let statusText = '';
						switch (value.value) {
							case 'REQUEST':
								color = 'blue';
								statusText = '요청';
								break;
							case 'CANCELED':
								color = 'red';
								statusText = '취소';
								break;
							case 'WAITING':
								color = 'green';
								statusText = '입고 대기';
								break;
							default:
								color = 'black';
								statusText = '입고 완료';
								break;
						}
						return `<span style="color: ${color}; font-weight: bold;">${statusText}</span>`;
					}
				}
			],
			data: []
		});

		// 발주 상세 목록을 위한 그리드 인스턴스
		purchaseDetailGrid = new tui.Grid({
			el: document.getElementById('purchaseDetailGrid'),
			scrollX: false,
			scrollY: true,
			bodyHeight: 200,
			minBodyHeight: 200,
			emptyMessage: '발주 목록의 행을 클릭하여 상세 정보를 확인하세요.',
			columns: [
				{ header: 'No.', name: 'id', align: 'center', width: 70 },
				{ header: '발주번호', name: 'purchaseId', align: 'center' },
				{ header: '자재번호', name: 'materialId', align: 'center' },
				{ header: '자재명', name: 'materialName', align: 'center' },
				{ header: '수량', name: 'purchaseQty', align: 'center' },
				{ header: '단위', name: 'unit', align: 'center' },
				{
					header: '단가', name: 'purchasePrice', align: 'center',
					formatter: function(value) {
						if (value.value) {
							return value.value.toLocaleString();
						}
						return value.value;
					}
				},
				{
					header: '총금액', name: 'totalPrice', align: 'center',
					formatter: function(value) {
						if (value.value) {
							return value.value.toLocaleString();
						}
						return value.value;
					}
				},
				{
					header: '발주상태', name: 'purchaseDetailStatus', align: 'center',
					formatter: function(value) {
						let color = '';
						let statusText = '';
						switch (value.value) {
							case 'REQUEST':
								color = 'blue';
								statusText = '요청';
								break;
							case 'CANCELED':
								color = 'red';
								statusText = '취소';
								break;
							case 'WAITING':
								color = 'green';
								statusText = '입고 대기';
								break;
							default:
								color = 'black';
								statusText = '입고 완료';
								break;
						}
						return `<span style="color: ${color}; font-weight: bold;">${statusText}</span>`;
					}
				}
			],
			data: []
		});

		// 페이지 로드 시 전체 발주 목록 불러오기
		loadPurchaseOrders();

		purchaseGrid.on('click', async (ev) => {
			const rowData = purchaseGrid.getRow(ev.rowKey);
			if (!rowData) {
				editBtn.style.display = "none";
				editBtn.removeAttribute('data-purchaseId');
				return;
			}

			// 발주상태 컬럼 클릭 시 취소 로직
			if (ev.columnName === 'purchaseStatus') {
				if (rowData.purchaseStatus === 'CANCELED') {
					alert("이미 취소된 발주입니다.");
					return;
				}

				editBtn.style.display = "none";

				if (confirm("발주를 취소하시겠습니까?")) {
					const purchaseId = rowData.purchaseId;
					try {
						const csrfToken = document.querySelector('meta[name="_csrf"]').content;
						const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

						const res = await fetch(`/purchase/api/purchase/${purchaseId}/cancel`, {
							method: "PUT",
							headers: {
								"Content-Type": "application/json",
								[csrfHeader]: csrfToken
							}
						});

						if (!res.ok) {
							throw new Error(await res.text());
						}

						purchaseGrid.setValue(ev.rowKey, 'purchaseStatus', 'CANCELED');
						alert("발주가 취소되었습니다.");
						
						loadPurchaseDetails(purchaseId);
					} catch (err) {
						console.error("발주 취소 실패:", err);
						alert("발주 취소 실패: " + err.message);
					}
				}
				return;
			}

			// 그 외 클릭: 상세 로드 및 수정 버튼 표시
			loadPurchaseDetails(rowData.purchaseId);

			if (rowData.purchaseStatus === 'REQUEST') {
				editBtn.style.display = "inline-block";
				editBtn.dataset.purchaseId = rowData.purchaseId;
			} else {
				editBtn.style.display = "none";
				editBtn.removeAttribute('data-purchaseId');
			}
		});
	};

	// 페이지 초기화 함수 호출
	initializePage();

	// 서버에서 목록/데이터 로드하는 함수들
	//--------------------------------------------------------

	// 페이지 로딩 시 전체 발주 목록 불러오기
	function loadPurchaseOrders() {
		fetch("/purchase/api/purchase")
			.then(response => response.json())
			.then(data => {
				purchaseGrid.resetData(data);
			})
			.catch(error => console.error("발주 목록 불러오기 오류:", error));
	}

	// 발주 상세 목록을 불러오는 함수
	function loadPurchaseDetails(purchaseId) {
		fetch(`/purchase/api/purchase/${purchaseId}/details`)
			.then(response => {
				if (!response.ok) {
					throw new Error('네트워크 응답이 올바르지 않습니다.');
				}
				return response.json();
			})
			.then(data => {
				if (data) {
					// DTO 필드명에 맞게 purchaseDetailGrid에 데이터 로드
					purchaseDetailGrid.resetData(data);
				} else {
					purchaseDetailGrid.resetData([]);
				}
			})
			.catch(error => console.error("발주 상세 목록 불러오기 오류:", error));
	}

	// 발주 등록 모달창 - 거래처 리스트 불러오기 (매입사 && 거래중)
	function loadClientsForModal(isEditMode) {
		return fetch("/business/api/clients")
			.then(response => response.json())
			.then(data => {
				const selectElement = document.getElementById("clientId");
				selectElement.innerHTML = '<option value="">선택</option>';

				let filteredClients = data;

				if (!isEditMode) {
					filteredClients = data.filter(client =>
						client.clientType === '매입사' && client.clientStatus === '거래중'
					);
				}

				filteredClients.forEach(client => {
					const option = document.createElement("option");
					option.value = client.clientId;
					option.textContent = client.clientName;
					selectElement.appendChild(option);
				});
			})
			.catch(error => console.error("거래처 목록 불러오기 오류:", error));
	}

	// 발주 등록 모달창 - 자재 리스트 불러오기
	function loadMaterialsForModal() {
		return new Promise((resolve, reject) => {
		fetch("/purchase/api/materials")
			.then(response => response.json())
			.then(data => {
				if (materialListGrid) {
					materialListGrid.resetData(data);
					// TUI Grid의 데이터 로딩이 완료된 후 resolve
					resolve(data);
				} else {
				    reject(new Error("materialListGrid가 초기화되지 않았습니다."));
				}
			})
			.catch(error => {
				console.error("자재 목록 불러오기 오류:", error)
				reject(error)
			});
		});
	}

	//--------------------------------------------------------------------------------------

	// 모달/품목 선택 UI 관련
	// ----------------------------------------------------------------------------------
	if (addBtn) {
		addBtn.addEventListener("click", async () => {
			isEditMode = false;
			editPurchaseId = null;
			editMaterials = [];

			document.getElementById('purchaseModalTitle').textContent = '발주 등록';
			document.getElementById('purchaseSubmitBtn').textContent = '등록';

			const clientSelect = document.getElementById("clientId");
			if (clientSelect) clientSelect.disabled = false;

			await loadClientsForModal(false);
			purchaseAddModal.show();
		});
	}

	// 모달이 완전히 표시된 후에 자재 리스트 그리드 초기화 및 데이터 로드
	purchaseAddModalElement.addEventListener('shown.bs.modal', async () => {
		const inputDate = document.getElementById("inputDate");
		if (inputDate) {
			inputDate.min = todayString;
		}

		if (!materialListGrid) {
			materialListGrid = new tui.Grid({
				el: document.getElementById('materialListGrid'),
				scrollX: false,
				scrollY: true,
				rowHeaders: ['checkbox'],
				bodyHeight: 280,
				columns: [
					{ header: '자재번호', name: 'materialId', align: 'center', width: 100 },
					{ header: '자재명', name: 'materialName', align: 'left', minwidth: 170 },
					{ header: '단위', name: 'unit', align: 'center', width: 70 },
					{
						header: '단가', name: 'price', align: 'center', minwidth: 90,
						formatter: function(value) {
							if (value.value) {
								return value.value.toLocaleString();
							}
							return value.value;
						}
					}
				],
				columnOptions: {
					resizable: true
				},
				data: []
			});

			materialListGrid.refreshLayout();

			materialListGrid.on('checkAll', () => updateSelectedMaterials());
			materialListGrid.on('uncheckAll', () => updateSelectedMaterials());
			materialListGrid.on('check', () => updateSelectedMaterials());
			materialListGrid.on('uncheck', () => updateSelectedMaterials());

			materialListGrid.on('click', (ev) => {
				if (ev.rowKey != null && ev.columnName !== '_disabled') {
					const checkedRowKeys = materialListGrid.getCheckedRowKeys();
					if (checkedRowKeys.includes(ev.rowKey)) {
						materialListGrid.uncheck(ev.rowKey);
					} else {
						materialListGrid.check(ev.rowKey);
					}
					updateSelectedMaterials();
				}
			});
		}

		await loadMaterialsForModal();

		if (isEditMode && editPurchaseId) {
			try {
				const gridData = materialListGrid.getData();
				materialListGrid.uncheckAll();
						
				const initialSelectedMaterials = []; // 초기 선택 품목 배열을 별도로 만들어줌

				// editMaterials를 순회하며 그리드에서 일치하는 품목을 찾고, 체크 및 배열에 추가
				editMaterials.forEach(material => {
					const rowIndex = gridData.findIndex(r => r.materialId === material.materialId);
					if (rowIndex !== -1) {
						// TUI Grid의 이벤트 리스너를 트리거하지 않도록 `check` 함수를 바로 호출
						// `false`를 두 번째 인자로 넘겨 이벤트 발생을 막을 수 잇음
						materialListGrid.check(rowIndex, false);
								
						// 초기 선택 품목 배열에 데이터를 추가
						const gridRow = materialListGrid.getRowAt(rowIndex);
						initialSelectedMaterials.push({
							...gridRow,
							qty: material.purchaseQty, 
							price: material.purchasePrice 
						});
					}
				});
						
				// 최종적으로 selectedMaterials를 초기 선택 품목 배열로 설정
				selectedMaterials = initialSelectedMaterials;
						
				// 우측 목록 렌더링
				renderSelectedMaterials();

			} catch (err) {
				purchaseAddModal.hide();
				alert("발주 수정 정보를 불러오는 데 실패했습니다.");
			}
		} else { // 신규 등록 모드인 경우
			selectedMaterials = [];
			renderSelectedMaterials();
		}
	});

	// 모달이 완전히 닫힌 후 그리드 파괴 및 상태 리셋
	purchaseAddModalElement.addEventListener('hidden.bs.modal', () => {
		if (materialListGrid) {
			materialListGrid.destroy();
			materialListGrid = null;
			selectedMaterials = [];
			renderSelectedMaterials();
		}
		isEditMode = false;
		editPurchaseId = null;
		editMaterials = [];
		const titleEl = document.getElementById('purchaseModalTitle');
		if (titleEl) titleEl.textContent = '발주 등록';
		const submitBtn = document.getElementById('purchaseSubmitBtn');
		if (submitBtn) submitBtn.textContent = '등록';
		const clientSelect = document.getElementById("clientId");
		if (clientSelect) clientSelect.disabled = false;
		form.reset();
	});

	// 선택된 품목 목록을 업데이트하는 함수
	const updateSelectedMaterials = () => {
		if (!materialListGrid) return;

		const checkedRows = materialListGrid.getCheckedRows();

		const newSelectedMaterials = checkedRows.map(r => {
			const existingMaterial = selectedMaterials.find(sp => sp.materialId === r.materialId);
			let quantity = 1;

			if (isEditMode && editMaterials.length > 0) {
				const editMaterial = editMaterials.find(it => it.materialId === r.materialId);
				if (editMaterial) {
					quantity = editMaterial.purchaseQty;
				}
			} else if (existingMaterial) {
				quantity = existingMaterial.qty;
			}

			return {
				...r,
				qty: quantity
			};
		});

		selectedMaterials = newSelectedMaterials;
		renderSelectedMaterials();
	};

	const renderSelectedMaterials = () => {
		selectedMaterialsContainer.innerHTML = '';
		let total = 0;

		if (selectedMaterials.length === 0) {
			if (emptyMessage) {
				selectedMaterialsContainer.appendChild(emptyMessage);
			}
		} else {
			if (emptyMessage && emptyMessage.parentNode) {
				emptyMessage.parentNode.removeChild(emptyMessage);
			}

			selectedMaterials.forEach((material) => {
				const price = parseInt(material.price) || 0;
				const initialQty = material.qty ? material.qty : 1;

				const materialDiv = document.createElement('div');
				materialDiv.classList.add('d-flex', 'align-items-center', 'mb-2');
				materialDiv.dataset.materialId = material.materialId;

				const materialHtml = `
	                <div class="d-flex align-items-center w-100">
	                    <span class="me-2">${material.materialName}</span>
	                    <input type="number" 
	                        class="form-control form-control-sm me-2 material-qty" // class명 변경
	                        style="width: 60px;" 
	                        value="${initialQty}" min="1" 
	                        data-material-price="${price}" 
	                        data-unit="${material.unit}"
							data-material-id="${material.materialId}"
	                        onchange="updateMaterialPrice(this)">
	                    <span class="me-2">×</span> 
	                    <span class="me-2">${price.toLocaleString()} 원</span>
						<span class="me-2">=</span> 
	                    <span class="material-total-price me-auto text-end">₩${(price * initialQty).toLocaleString()}</span> 
	                    <button type="button" class="btn-close ms-2 remove-material-btn" aria-label="Close"></button> 
	                </div>
	            `;

				materialDiv.innerHTML = materialHtml;
				selectedMaterialsContainer.appendChild(materialDiv);

				materialDiv.querySelector('.remove-material-btn').addEventListener('click', (event) => {
				    event.preventDefault();
				    event.stopPropagation();
				    // 해당 버튼에 연결된 materialId를 찾아 removeMaterial 함수에 전달
				    removeMaterial(material.materialId); 
				});

				total += price * initialQty;
			});
		}

		totalPriceElement.textContent = `₩${total.toLocaleString()}`;
	};

	window.updateMaterialPrice = (inputElement) => {
		const quantity = parseInt(inputElement.value) || 0;
		const price = parseInt(inputElement.dataset.materialPrice) || 0;
		const newTotal = quantity * price;

		const materialId = inputElement.dataset.materialId;
		const materialDiv = selectedMaterialsContainer.querySelector(`[data-material-id='${materialId}']`);

		const materialToUpdate = selectedMaterials.find(material => material.materialId === materialId);
		if (materialToUpdate) {
			materialToUpdate.qty = quantity;
		}

		if (materialDiv) {
			const el = materialDiv.querySelector('.material-total-price');
			if (el) el.textContent = `₩${newTotal.toLocaleString()}`;
		}

		updateTotalPrice();
	};

	const updateTotalPrice = () => {
		let total = 0;
		selectedMaterials.forEach(material => {
			total += (material.qty || 0) * (material.price || 0);
		});
		totalPriceElement.textContent = `₩${total.toLocaleString()}`;
	};

	window.removeMaterial = (materialId) => {
		if (!materialListGrid) return;

    	// 1. materialListGrid에서 해당 품목의 rowKey를 찾고 uncheck
    	// `getRowAt` 대신 `getData`와 `findIndex`를 사용하여 행을 찾도록 수정
    	const gridData = materialListGrid.getData();
    	const rowKey = gridData.findIndex(r => r.materialId === materialId);

    	if (rowKey !== -1) {
       		// TUI Grid의 이벤트 리스너가 다시 호출되지 않도록 `false`를 인자로 전달
       	 	materialListGrid.uncheck(rowKey, false); 
    	}
		
		// 2. selectedMaterials 배열에서 해당 품목 제거
		selectedMaterials = selectedMaterials.filter(material => material.materialId !== materialId);
		
		// 3. 발주 자재 목록 렌더링
		renderSelectedMaterials();
	};

	// 품목 검색 버튼 이벤트
	document.getElementById("searchmaterialBtn").addEventListener("click", () => {
		const keyword = document.getElementById("materialSearch").value;
		fetch(`/purchase/api/materials/search?keyword=${encodeURIComponent(keyword)}`)
			.then(response => response.json())
			.then(data => {
				if (materialListGrid) {
					materialListGrid.resetData(data);
				}
			})
			.catch(error => console.error("자재 검색 오류:", error));
	});

	// 폼 제출 이벤트 (발주 등록 및 수정)
	form.addEventListener("submit", async (event) => {
		event.preventDefault();

		const clientSelect = document.getElementById("clientId");
		const clientId = clientSelect.value;
		const clientName = clientSelect.options[clientSelect.selectedIndex] ? clientSelect.options[clientSelect.selectedIndex].text : '';

		if (!clientId) {
			alert("거래처를 선택해주세요.");
			return;
		}

		let inputDate = document.getElementById("inputDate").value || "";
		if (inputDate.includes("T")) inputDate = inputDate.split("T")[0];

		if (!inputDate) {
			alert("입고예정일을 선택해주세요.");
			return;
		}

		const materials = selectedMaterials.map(material => {
			const purchaseQty = parseInt(material.qty) || 0;
			const purchasePrice = parseInt(material.price) || 0;
			return {
				materialId: material.materialId,
				materialName: material.materialName,
				unit: material.unit,
				purchaseQty: purchaseQty,
				purchasePrice: purchasePrice,
				totalPrice: purchaseQty * purchasePrice,
			};
		});

		if (materials.length === 0) {
			alert("하나 이상의 자재를 선택해주세요.");
			return;
		}

		const totalPurchaseQty = materials.reduce((sum, material) => sum + material.purchaseQty, 0);
		const totalPurchasePrice = materials.reduce((sum, material) => sum + material.totalPrice, 0);

		const payload = {
			clientId: clientId,
			clientName: clientName,
			inputDate: inputDate,
			totalPurchaseQty: totalPurchaseQty,
			totalPurchasePrice: totalPurchasePrice,
			materials: materials
		};

		console.log("전송될 페이로드:", payload);

		const csrfToken = document.querySelector('meta[name="_csrf"]').content;
		const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

		try {
			let res;
			if (isEditMode && editPurchaseId) {
				res = await fetch(`/purchase/api/purchase/${editPurchaseId}`, {
					method: "PUT",
					headers: {
						"Content-Type": "application/json",
						[csrfHeader]: csrfToken
					},
					body: JSON.stringify(payload)
				});
			} else {
				res = await fetch("/purchase/api/purchase/submit", {
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
					alert("발주 수정 완료: " + (j.purchaseId || editPurchaseId));
				} else {
					alert("발주 등록 완료: " + j.purchaseId);
				}
				purchaseAddModal.hide();
				loadPurchaseOrders();

				const updatedPurchaseId = isEditMode ? editPurchaseId : j.purchaseId;
				loadPurchaseDetails(updatedPurchaseId);

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

	// 편집 모달 열기 (기존 발주 불러와서 채우기)
	async function openEditModal(purchaseId, rowData) {
		try {
			const res = await fetch(`/purchase/api/purchase/${purchaseId}`);
			if (!res.ok) {
				throw new Error("발주 정보를 불러오지 못했습니다.");
			}
			const purchase = await res.json();

			isEditMode = true;
			editPurchaseId = purchaseId;

			document.getElementById('purchaseModalTitle').textContent = '발주 수정';
			document.getElementById('purchaseSubmitBtn').textContent = '수정';

			await loadClientsForModal(true);
			const clientSelect = document.getElementById("clientId");
			if (clientSelect) {
				clientSelect.value = purchase.clientId || '';
				clientSelect.disabled = true;
			}

			document.getElementById("inputDate").value = purchase.inputDate ? purchase.inputDate.split("T")[0] : "";

			editMaterials = purchase.materials || [];

			purchaseAddModal.show();

		} catch (err) {
			console.error("편집 모달 오픈 실패:", err);
			alert("편집 모달을 열 수 없습니다: " + err.message);
		}
	}

	//	// 검색 버튼 클릭 이벤트 추가
	//	document.getElementById("searchBtn").addEventListener("click", () => {
	//		const status = document.getElementById("purchaseStatus").value;
	//		const keyword = document.getElementById("cliSearch").value;
	//		
	//		let url = `/purchase/api/purchase/search?purchaseStatus=${status}&clientName=${encodeURIComponent(keyword)}`; // URL 변경
	//		
	//		fetch(url)
	//			.then(response => response.json())
	//			.then(data => {
	//				purchaseGrid.resetData(data);
	//			})
	//			.catch(error => console.error("발주 목록 검색 오류:", error));
	//	});

});