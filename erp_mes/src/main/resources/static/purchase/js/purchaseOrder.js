document.addEventListener("DOMContentLoaded", () => {
	// TUI Grid 인스턴스 변수
	let purchaseGrid;
	let purchaseDetailGrid;

	// 일반 발주 모달 관련 변수
	let normalMaterialListGrid;
	const normalPurchaseModalElement = document.getElementById('normalPurchaseAddModal');
	const normalPurchaseModal = new bootstrap.Modal(normalPurchaseModalElement);
	const normalForm = document.getElementById("normalPurchaseAddForm");
	const normalSelectedMaterialsContainer = normalForm.querySelector('#selectedItemsContainer');
	const normalTotalPriceElement = normalForm.querySelector('#totalPrice');
	const normalEmptyMessage = normalForm.querySelector('#emptyMessage');
	let normalSelectedMaterials = [];

	// 작업지시 발주 모달 관련 변수
	let workOrderGrid;
	const workOrderPurchaseModalElement = document.getElementById('workOrderPurchaseAddModal');
	const workOrderPurchaseModal = new bootstrap.Modal(workOrderPurchaseModalElement);
	const workOrderForm = document.getElementById("workOrderPurchaseAddForm");
	const workOrderSelectedItemsContainer = workOrderForm.querySelector('#workOrderSelectedItemsContainer');
	const workOrderTotalPriceElement = workOrderForm.querySelector('#workOrderTotalPrice');
	const workOrderEmptyMessage = workOrderForm.querySelector('#workOrderEmptyMessage');
	let workOrderSelectedMaterials = [];

	// 편집 모드 관련
	let isEditMode = false;
	let editPurchaseId = null;
	let editMaterials = [];

	const today = new Date();
	const todayString = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;

	const workOrderAddBtn = document.getElementById("WorkOrderAddBtn");
	const normalAddBtn = document.getElementById("NormalAddBtn");
	let editBtn = document.getElementById("editBtn");
	if (!editBtn) {
		editBtn = document.createElement("button");
		editBtn.id = "editBtn";
		editBtn.type = "button";
		editBtn.className = "btn btn-secondary ms-2";
		editBtn.textContent = "수정";
		editBtn.style.display = "none";
		if (normalAddBtn && normalAddBtn.parentNode) {
			normalAddBtn.parentNode.insertBefore(editBtn, normalAddBtn.nextSibling);
		} else {
			document.body.appendChild(editBtn);
		}
	}

	editBtn.addEventListener('click', () => {
		const focused = purchaseGrid.getFocusedCell();
		if (!focused) {
			alert("수정할 행을 선택해주세요.");
			return;
		}
		const rowData = purchaseGrid.getRow(focused.rowKey);
		openEditModal(rowData.purchaseId, rowData);
	});

	const initializePage = () => {
		purchaseGrid = new tui.Grid({
			el: document.getElementById('purchaseGrid'),
			scrollX: false,
			scrollY: true,
			bodyHeight: 200,
			minBodyHeight: 200,
			emptyMessage: '조회결과가 없습니다.',
			columns: [
				{ header: '발주번호', name: 'purchaseId', align: 'center' },
				{ header: '거래처명', name: 'clientName', align: 'center' },
				{ header: '등록자 사원번호', name: 'empId', align: 'center' },
				{ header: '등록자', name: 'empName', align: 'center' },
				{
					header: '발주일', name: 'purchaseDate', align: 'center',
					formatter: (value) => value.value ? value.value.split('T')[0] : ''
				},
				{
					header: '입고요청일', name: 'inputDate', align: 'center',
					formatter: (value) => value.value ? value.value.split('T')[0] : ''
				},
				{ header: '발주수량', name: 'totalPurchaseQty', align: 'center' },
				{
					header: '발주금액', name: 'totalPurchasePrice', align: 'center',
					formatter: (value) => value.value ? value.value.toLocaleString() : ''
				},
				{
					header: '발주상태', name: 'purchaseStatus', align: 'center',
					formatter: (value) => {
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
					formatter: (value) => value.value ? value.value.toLocaleString() : ''
				},
				{
					header: '총금액', name: 'totalPrice', align: 'center',
					formatter: (value) => value.value ? value.value.toLocaleString() : ''
				},
				{
					header: '발주상태', name: 'purchaseDetailStatus', align: 'center',
					formatter: (value) => {
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

		loadPurchaseOrders();

		purchaseGrid.on('click', async (ev) => {
			const rowData = purchaseGrid.getRow(ev.rowKey);
			if (!rowData) {
				editBtn.style.display = "none";
				editBtn.removeAttribute('data-purchaseId');
				return;
			}
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
							headers: { "Content-Type": "application/json", [csrfHeader]: csrfToken }
						});
						if (!res.ok) throw new Error(await res.text());
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

	initializePage();

	//--- 서버 통신 함수 ---
	function loadPurchaseOrders() {
		fetch("/purchase/api/purchase").then(response => response.json()).then(data => {
			purchaseGrid.resetData(data);
		}).catch(error => console.error("발주 목록 불러오기 오류:", error));
	}

	function loadPurchaseDetails(purchaseId) {
		fetch(`/purchase/api/purchase/${purchaseId}/details`).then(response => {
			if (!response.ok) throw new Error('네트워크 응답이 올바르지 않습니다.');
			return response.json();
		}).then(data => {
			purchaseDetailGrid.resetData(data);
		}).catch(error => console.error("발주 상세 목록 불러오기 오류:", error));
	}

	function loadClientsForModal(selectId) {
		return fetch("/business/api/clients")
			.then(response => response.json())
			.then(data => {
				const selectElement = document.getElementById(selectId);
				if (!selectElement) {
					console.error(`Error: Element with ID '${selectId}' not found.`);
					return;
				}
				selectElement.innerHTML = '<option value="">선택</option>';
				const filteredClients = data.filter(client =>
					client.clientType === '매입사' && client.clientStatus === '거래중'
				);
				filteredClients.forEach(client => {
					const option = document.createElement("option");
					option.value = client.clientId;
					option.textContent = client.clientName;
					selectElement.appendChild(option);
				});
			})
			.catch(error => console.error("거래처 목록 불러오기 오류:", error));
	}

	function loadNormalMaterials() {
		return new Promise((resolve, reject) => {
			fetch("/purchase/api/materials").then(response => response.json()).then(data => {
				if (normalMaterialListGrid) {
					normalMaterialListGrid.resetData(data);
					resolve(data);
				} else {
					reject(new Error("normalMaterialListGrid가 초기화되지 않았습니다."));
				}
			}).catch(error => {
				console.error("자재 목록 불러오기 오류:", error);
				reject(error);
			});
		});
	}

	function loadWorkOrders() {
		return fetch("/purchase/api/work-orders/shortage").then(response => response.json()).then(data => {
			const groupedData = data.reduce((acc, item) => {
				if (!acc[item.workOrderId]) {
					acc[item.workOrderId] = {
						workOrderId: item.workOrderId,
						materials: []
					};
				}
				acc[item.workOrderId].materials.push(item);
				return acc;
			}, {});
			const gridData = Object.values(groupedData).map(workOrder => {
				const totalQty = workOrder.materials.reduce((sum, mat) => sum + mat.requireQty, 0);
				return { workOrderId: workOrder.workOrderId, materialCount: workOrder.materials.length, totalQty: totalQty };
			});
			workOrderGrid.resetData(gridData);
		}).catch(error => console.error("작업지시 목록 불러오기 오류:", error));
	}

	//--- 일반 발주 모달 로직 ---
	if (normalAddBtn) {
		normalAddBtn.addEventListener("click", async () => {
			isEditMode = false;
			editPurchaseId = null;
			editMaterials = [];
			normalSelectedMaterials = [];
			renderNormalMaterials();
			normalForm.reset();
			normalPurchaseModal.show();
		});
	}

	normalPurchaseModalElement.addEventListener('shown.bs.modal', async () => {
		document.getElementById('NormalPurchaseModalTitle').textContent = '일반 발주';
		document.getElementById('purchaseSubmitBtn').textContent = '등록';

		const clientIdSelect = normalForm.querySelector("#clientId");
		await loadClientsForModal("clientId");
		clientIdSelect.disabled = false;

		const inputDate = normalForm.querySelector("#inputDate");
		inputDate.min = todayString;

		if (!normalMaterialListGrid) {
			normalMaterialListGrid = new tui.Grid({
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
						formatter: (value) => value.value ? value.value.toLocaleString() : ''
					}
				],
				columnOptions: { resizable: true }
			});

			normalMaterialListGrid.on('checkAll', () => updateNormalSelectedMaterials());
			normalMaterialListGrid.on('uncheckAll', () => updateNormalSelectedMaterials());
			normalMaterialListGrid.on('check', () => updateNormalSelectedMaterials());
			normalMaterialListGrid.on('uncheck', () => updateNormalSelectedMaterials());

			normalMaterialListGrid.on('click', (ev) => {
				if (ev.rowKey != null && ev.columnName !== '_disabled') {
					const checkedRowKeys = normalMaterialListGrid.getCheckedRowKeys();
					if (checkedRowKeys.includes(ev.rowKey)) {
						normalMaterialListGrid.uncheck(ev.rowKey);
					} else {
						normalMaterialListGrid.check(ev.rowKey);
					}
					updateNormalSelectedMaterials();
				}
			});
		}
		await loadNormalMaterials();
	});

	normalPurchaseModalElement.addEventListener('hidden.bs.modal', () => {
		if (normalMaterialListGrid) {
			normalMaterialListGrid.destroy();
			normalMaterialListGrid = null;
		}
		normalSelectedMaterials = [];
		renderNormalMaterials();
	});

	normalForm.addEventListener("submit", async (event) => {
		event.preventDefault();
		const clientId = normalForm.querySelector("#clientId").value;
		const clientName = normalForm.querySelector("#clientId").options[normalForm.querySelector("#clientId").selectedIndex].text;
		const inputDate = normalForm.querySelector("#inputDate").value;

		if (!clientId) {
			alert("거래처를 선택해주세요.");
			return;
		}
		if (!inputDate) {
			alert("입고예정일을 선택해주세요.");
			return;
		}
		const materials = normalSelectedMaterials.map(material => ({
			materialId: material.materialId,
			materialName: material.materialName,
			unit: material.unit,
			purchaseQty: parseInt(material.qty) || 0,
			purchasePrice: parseInt(material.price) || 0,
			totalPrice: (parseInt(material.qty) || 0) * (parseInt(material.price) || 0),
		}));
		if (materials.length === 0) {
			alert("하나 이상의 자재를 선택해주세요.");
			return;
		}

		const totalPurchaseQty = materials.reduce((sum, m) => sum + m.purchaseQty, 0);
		const totalPurchasePrice = materials.reduce((sum, m) => sum + m.totalPrice, 0);
		const payload = {
			clientId, clientName, inputDate, totalPurchaseQty, totalPurchasePrice, materials
		};
		const csrfToken = document.querySelector('meta[name="_csrf"]').content;
		const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

		try {
			const res = await fetch("/purchase/api/purchase/submit", {
				method: "POST",
				headers: { "Content-Type": "application/json", [csrfHeader]: csrfToken },
				body: JSON.stringify(payload)
			});
			if (res.ok) {
				const j = await res.json();
				alert("발주 등록 완료: " + j.purchaseId);
				normalPurchaseModal.hide();
				loadPurchaseOrders();
			} else {
				const txt = await res.text();
				console.error("서버 응답 에러:", res.status, txt);
				alert("등록 실패: " + txt);
			}
		} catch (err) {
			console.error(err);
			alert("서버 통신 오류");
		}
	});

	const updateNormalSelectedMaterials = () => {
		if (!normalMaterialListGrid) return;
		const checkedRows = normalMaterialListGrid.getCheckedRows();
		normalSelectedMaterials = checkedRows.map(r => {
			const existing = normalSelectedMaterials.find(sp => sp.materialId === r.materialId);
			return {
				...r,
				qty: existing ? existing.qty : 1
			};
		});
		renderNormalMaterials();
	};

	const renderNormalMaterials = () => {
		normalSelectedMaterialsContainer.innerHTML = '';
		let total = 0;
		if (normalSelectedMaterials.length === 0) {
			normalSelectedMaterialsContainer.appendChild(normalEmptyMessage);
		} else {
			if (normalEmptyMessage && normalEmptyMessage.parentNode) {
				normalEmptyMessage.parentNode.removeChild(normalEmptyMessage);
			}
			normalSelectedMaterials.forEach((material) => {
				const price = parseInt(material.price) || 0;
				const initialQty = material.qty ? material.qty : 1;
				const materialDiv = document.createElement('div');
				materialDiv.classList.add('d-flex', 'align-items-center', 'mb-2');
				materialDiv.dataset.materialId = material.materialId;
				materialDiv.innerHTML = `
					<div class="d-flex align-items-center w-100">
						<span class="me-2">${material.materialName}</span>
						<input type="number" class="form-control form-control-sm me-2 material-qty" style="width: 60px;" value="${initialQty}" min="1" data-material-price="${price}" data-unit="${material.unit}" data-material-id="${material.materialId}">
						<span class="me-2">×</span>
						<span class="me-2">${price.toLocaleString()} 원</span>
						<span class="me-2">=</span>
						<span class="material-total-price me-auto text-end">₩${(price * initialQty).toLocaleString()}</span>
						<button type="button" class="btn-close ms-2 remove-material-btn" aria-label="Close"></button>
					</div>
				`;
				normalSelectedMaterialsContainer.appendChild(materialDiv);
				materialDiv.querySelector('.material-qty').addEventListener('change', (event) => {
					const qty = parseInt(event.target.value) || 0;
					const updatedMaterial = normalSelectedMaterials.find(m => m.materialId === material.materialId);
					if (updatedMaterial) updatedMaterial.qty = qty;
					updateNormalTotalPrice();
					const el = materialDiv.querySelector('.material-total-price');
					if (el) el.textContent = `₩${(price * qty).toLocaleString()}`;
				});
				materialDiv.querySelector('.remove-material-btn').addEventListener('click', () => {
					removeNormalMaterial(material.materialId);
				});
				total += price * initialQty;
			});
		}
		normalTotalPriceElement.textContent = `₩${total.toLocaleString()}`;
	};

	const updateNormalTotalPrice = () => {
		let total = normalSelectedMaterials.reduce((sum, material) => sum + (material.qty || 0) * (material.price || 0), 0);
		normalTotalPriceElement.textContent = `₩${total.toLocaleString()}`;
	};

	const removeNormalMaterial = (materialId) => {
		if (!normalMaterialListGrid) return;
		const rowKey = normalMaterialListGrid.getData().findIndex(r => r.materialId === materialId);
		if (rowKey !== -1) {
			normalMaterialListGrid.uncheck(rowKey, false);
		}
		normalSelectedMaterials = normalSelectedMaterials.filter(material => material.materialId !== materialId);
		renderNormalMaterials();
	};

	//--- 작업지시 발주 모달 로직 ---
	if (workOrderAddBtn) {
		workOrderAddBtn.addEventListener("click", async () => {
			isEditMode = false;
			editPurchaseId = null;
			workOrderSelectedMaterials = [];
			renderWorkOrderMaterials();
			workOrderForm.reset();
			workOrderPurchaseModal.show();
		});
	}

	workOrderPurchaseModalElement.addEventListener('shown.bs.modal', async () => {
		const clientIdSelect = workOrderForm.querySelector("#workOrderClientId");
		await loadClientsForModal("workOrderClientId");
		clientIdSelect.disabled = false;

		const inputDate = workOrderForm.querySelector("#workOrderInputDate");
		inputDate.min = todayString;

		if (!workOrderGrid) {
			workOrderGrid = new tui.Grid({
				el: document.getElementById('workOrderListGrid'),
				scrollX: false,
				scrollY: true,
				rowHeaders: ['checkbox'],
				bodyHeight: 280,
				columns: [
					{ header: '작업지시 ID', name: 'workOrderId', align: 'center' },
					{ header: '자재 종류', name: 'materialCount', align: 'center' },
					{ header: '총 필요 수량', name: 'totalQty', align: 'center' },
				],
				columnOptions: { resizable: true }
			});
			
			workOrderGrid.on('click', (ev) => {
				if (ev.rowKey != null) {
					const row = workOrderGrid.getRow(ev.rowKey);
					console.log(row)
					updateWorkOrderSelectedItems(row.workOrderId);
				}
			});
		}
		await loadWorkOrders();
	});

	workOrderPurchaseModalElement.addEventListener('hidden.bs.modal', () => {
		if (workOrderGrid) {
			workOrderGrid.destroy();
			workOrderGrid = null;
		}
		workOrderSelectedMaterials = [];
		renderWorkOrderMaterials();
	});

	const updateWorkOrderSelectedItems = async (workOrderId) => {
		try {
			const response = await fetch(`/purchase/api/work-orders/${workOrderId}/details`);
			if (!response.ok) throw new Error('작업지시 상세 정보 불러오기 실패');
			const data = await response.json();
			workOrderSelectedMaterials = data.map(item => ({
				materialId: item.materialId,
				materialName: item.materialName,
				price: item.price,
				qty: item.requireQty
			}));
			renderWorkOrderMaterials();
		} catch (error) {
			console.error("작업지시 상세 목록 불러오기 오류:", error);
			alert("작업지시 상세 목록을 불러오는 중 오류가 발생했습니다.");
		}
	};

	const renderWorkOrderMaterials = () => {
		workOrderSelectedItemsContainer.innerHTML = '';
		let total = 0;
		if (workOrderSelectedMaterials.length === 0) {
			workOrderSelectedItemsContainer.appendChild(workOrderEmptyMessage);
		} else {
			if (workOrderEmptyMessage && workOrderEmptyMessage.parentNode) {
				workOrderEmptyMessage.parentNode.removeChild(workOrderEmptyMessage);
			}
			workOrderSelectedMaterials.forEach((material) => {
				const price = parseInt(material.price) || 0;
				const initialQty = material.qty ? material.qty : 1;
				const materialDiv = document.createElement('div');
				materialDiv.classList.add('d-flex', 'align-items-center', 'mb-2');
				materialDiv.dataset.materialId = material.materialId;
				materialDiv.innerHTML = `
					<div class="d-flex align-items-center w-100">
						<span class="me-2">${material.materialName}: </span>
						<span class="me-2 fw-bold">${initialQty}개</span>
						<span class="me-2">×</span>
						<span class="me-2">${price.toLocaleString()}원</span>
						<span class="me-2">=</span>
						<span class="material-total-price me-auto text-end">₩${(price * initialQty).toLocaleString()}</span>
					</div>
				`;
				workOrderSelectedItemsContainer.appendChild(materialDiv);
				total += price * initialQty;
			});
		}
		workOrderTotalPriceElement.textContent = `₩${total.toLocaleString()}`;
	};

	workOrderForm.addEventListener("submit", async (event) => {
		event.preventDefault();
		const workOrderId = workOrderGrid.getCheckedRowKeys()[0];
		if (!workOrderId) {
			alert("작업지시를 선택해주세요.");
			return;
		}
		const clientId = workOrderForm.querySelector("#workOrderClientId").value;
		const clientName = workOrderForm.querySelector("#workOrderClientId").options[workOrderForm.querySelector("#workOrderClientId").selectedIndex].text;
		const inputDate = workOrderForm.querySelector("#workOrderInputDate").value;

		if (!clientId) {
			alert("거래처를 선택해주세요.");
			return;
		}
		if (!inputDate) {
			alert("입고예정일을 선택해주세요.");
			return;
		}

		const materials = workOrderSelectedMaterials.map(material => ({
			materialId: material.materialId,
			materialName: material.materialName,
			purchaseQty: material.qty,
			purchasePrice: material.price,
			totalPrice: material.qty * material.price,
		}));
		if (materials.length === 0) {
			alert("하나 이상의 자재를 선택해주세요.");
			return;
		}

		const totalPurchaseQty = materials.reduce((sum, m) => sum + m.purchaseQty, 0);
		const totalPurchasePrice = materials.reduce((sum, m) => sum + m.totalPrice, 0);
		const payload = {
			clientId, clientName, inputDate, totalPurchaseQty, totalPurchasePrice, materials, workOrderId
		};
		const csrfToken = document.querySelector('meta[name="_csrf"]').content;
		const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

		try {
			const res = await fetch("/purchase/api/purchase/submit", {
				method: "POST",
				headers: { "Content-Type": "application/json", [csrfHeader]: csrfToken },
				body: JSON.stringify(payload)
			});
			if (res.ok) {
				const j = await res.json();
				alert("작업지시 발주 등록 완료: " + j.purchaseId);
				workOrderPurchaseModal.hide();
				loadPurchaseOrders();
			} else {
				const txt = await res.text();
				console.error("서버 응답 에러:", res.status, txt);
				alert("등록 실패: " + txt);
			}
		} catch (err) {
			console.error(err);
			alert("서버 통신 오류");
		}
	});

	async function openEditModal(purchaseId, rowData) {
		try {
			const res = await fetch(`/purchase/api/purchase/${purchaseId}`);
			if (!res.ok) throw new Error("발주 정보를 불러오지 못했습니다.");
			const purchase = await res.json();

			isEditMode = true;
			editPurchaseId = purchaseId;
			editMaterials = purchase.materials || [];

			document.getElementById('NormalPurchaseModalTitle').textContent = '발주 수정';
			document.getElementById('purchaseSubmitBtn').textContent = '수정';

			normalPurchaseModal.show();
		} catch (err) {
			console.error("편집 모달 오픈 실패:", err);
			alert("편집 모달을 열 수 없습니다: " + err.message);
		}
	}

	normalPurchaseModalElement.addEventListener('shown.bs.modal', async () => {
		if (isEditMode) {
			await loadClientsForModal("clientId");
			document.getElementById("clientId").value = editMaterials[0].clientId || '';
			document.getElementById("clientId").disabled = true;

			document.getElementById("inputDate").value = editMaterials[0].inputDate ? editMaterials[0].inputDate.split("T")[0] : "";

			normalSelectedMaterials = editMaterials.map(m => ({
				materialId: m.materialId,
				materialName: m.materialName,
				unit: m.unit,
				price: m.purchasePrice,
				qty: m.purchaseQty
			}));
			renderNormalMaterials();
		} else {
			await loadClientsForModal("clientId");
			document.getElementById("clientId").disabled = false;
			document.getElementById("inputDate").value = '';
		}

		const inputDate = normalForm.querySelector("#inputDate");
		inputDate.min = todayString;

		if (!normalMaterialListGrid) {
			normalMaterialListGrid = new tui.Grid({
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
						formatter: (value) => value.value ? value.value.toLocaleString() : ''
					}
				],
				columnOptions: { resizable: true }
			});

			normalMaterialListGrid.on('checkAll', () => updateNormalSelectedMaterials());
			normalMaterialListGrid.on('uncheckAll', () => updateNormalSelectedMaterials());
			normalMaterialListGrid.on('check', () => updateNormalSelectedMaterials());
			normalMaterialListGrid.on('uncheck', () => updateNormalSelectedMaterials());

			normalMaterialListGrid.on('click', (ev) => {
				if (ev.rowKey != null && ev.columnName !== '_disabled') {
					const checkedRowKeys = normalMaterialListGrid.getCheckedRowKeys();
					if (checkedRowKeys.includes(ev.rowKey)) {
						normalMaterialListGrid.uncheck(ev.rowKey);
					} else {
						normalMaterialListGrid.check(ev.rowKey);
					}
					updateNormalSelectedMaterials();
				}
			});
		}
		await loadNormalMaterials();

		if (isEditMode) {
			const gridData = normalMaterialListGrid.getData();
			const checkedRowKeys = normalSelectedMaterials.map(m => gridData.findIndex(r => r.materialId === m.materialId)).filter(index => index !== -1);
			normalMaterialListGrid.uncheckAll();
			checkedRowKeys.forEach(key => normalMaterialListGrid.check(key, false));
		}
	});
});