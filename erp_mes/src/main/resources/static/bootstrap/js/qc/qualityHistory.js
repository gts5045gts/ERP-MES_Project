document.addEventListener('DOMContentLoaded', function() {
	// CSRF 토큰
	const token = document.querySelector('meta[name="_csrf"]').content;
	const header = document.querySelector('meta[name="_csrf_header"]').content;

	// --- 상단 그리드 (품질검사 이력) ---
	let allHistoryData = [];
	const historyGrid = new tui.Grid({
		el: document.getElementById('historyGrid'),
		columns: [
			{ header: '검사ID', name: 'inspectionId' },
			{ header: '검사유형', name: 'inspectionTypeName' },
			{ header: '자재명/제품명', name: 'displayTargetName' },
			{ header: '공정명', name: 'displayProcessName' },
			{ header: '검사일자', name: 'inspectionDate' },
			{ header: '검사자', name: 'empName' },
			{ header: '로트번호', name: 'lotId' },
			{ header: '검사결과', name: 'result' },
			{ header: '비고', name: 'remarks' }
		]
	});

	async function loadHistoryData() {
		try {
			const response = await fetch('/quality/api/inspection-results');
			if (!response.ok) { throw new Error('데이터 로드 실패'); }
			allHistoryData = await response.json();
			historyGrid.resetData(allHistoryData);
		} catch (error) {
			console.error('Fetch error:', error);
		}
	}

	function filterHistoryData() {
		const filterType = document.getElementById('historyFilterType').value;
		const filterProduct = document.getElementById('historyFilterProduct').value.toLowerCase();
		const filterLotId = document.getElementById('historyFilterLotId').value.toLowerCase();
		let filteredData = allHistoryData.filter(item => {
			const typeMatch = (filterType === 'ALL' || item.inspectionType === filterType);
			const productMatch = item.productName && item.productName.toLowerCase().includes(filterProduct);
			const lotIdMatch = item.lotId && item.lotId.toLowerCase().includes(filterLotId);
			return typeMatch && productMatch && lotIdMatch;
		});
		historyGrid.resetData(filteredData);
	}
	document.getElementById('historySearchBtn').addEventListener('click', filterHistoryData);

	// --- 검사 대기 목록 라디오 버튼 로직 ---
	const radioButtons = document.querySelectorAll('input[name="inspectionTypeRadio"]');
	const gridContainers = document.querySelectorAll('.inspection-grid-container');

	radioButtons.forEach(radio => {
		radio.addEventListener('change', function() {
			const selectedValue = this.value;

			// 모든 그리드 컨테이너 숨기기
			gridContainers.forEach(container => {
				container.style.display = 'none';
			});

			// 선택된 값에 해당하는 그리드 컨테이너만 표시
			const selectedContainer = document.getElementById(`${selectedValue}GridContainer`);
			if (selectedContainer) {
				selectedContainer.style.display = 'block';

				// Tui-Grid가 컨테이너의 크기를 다시 계산하도록 요청
				if (selectedValue === 'incoming') {
					incomingGrid.refreshLayout();
				} else if (selectedValue === 'process') {
					processGrid.refreshLayout();
				} else if (selectedValue === 'packaging') {
					packagingGrid.refreshLayout();
				}
			}
		});
	});

	// --- 하단 그리드 (검사 대기 목록) ---
	let incomingGrid, processGrid, packagingGrid;
	let selectedTargetData = null;

	async function loadTargetData() {
		try {
			const incomingResponse = await fetch('/quality/api/incoming-targets');
			const processResponse = await fetch('/quality/api/process-targets');
			const packagingResponse = await fetch('/quality/api/packaging-targets');

			const incomingData = await incomingResponse.json();
			const processData = await processResponse.json();
			const packagingData = await packagingResponse.json();

			// Grid 인스턴스가 없을 때만 생성합니다.
			if (!incomingGrid) {
				incomingGrid = new tui.Grid({
					el: document.getElementById('incomingGrid'),
					data: incomingData,
					columns: [
						{ header: 'ID', name: 'targetId' },
						{ header: '자재명', name: 'targetName' },
						{ header: '로트번호', name: 'lotId' },
						{ header: '수량', name: 'quantity' },
						{ header: '검사유형', name: 'inspectionTypeName' },
						{ header: '출처', name: 'targetSource', hidden: true }
					]
				});
				// 이벤트 리스너도 여기서 한 번만 추가합니다.
				addGridClickListener(incomingGrid);
			} else {
				// 인스턴스가 이미 있으면 데이터를 덮어씁니다.
				incomingGrid.resetData(incomingData);
			}

			if (!processGrid) {
				processGrid = new tui.Grid({
					el: document.getElementById('processGrid'),
					data: processData,
					columns: [
						{ header: 'ID', name: 'targetId' },
						{ header: '제품명', name: 'targetName' },
						{ header: '공정명', name: 'processName' },
						{ header: '설비명', name: 'equipName' },
						{ header: '로트번호', name: 'lotId' },
						{ header: '수량', name: 'quantity' },
						{ header: '검사유형', name: 'inspectionTypeName' },
						{ header: '출처', name: 'targetSource', hidden: true }
					]
				});
				addGridClickListener(processGrid);
			} else {
				processGrid.resetData(processData);
			}

			if (!packagingGrid) {
				packagingGrid = new tui.Grid({
					el: document.getElementById('packagingGrid'),
					data: packagingData,
					columns: [
						{ header: 'ID', name: 'targetId' },
						{ header: '제품명', name: 'targetName' },
						{ header: '로트번호', name: 'lotId' },
						{ header: '수량', name: 'quantity' },
						{ header: '검사유형', name: 'inspectionTypeName' },
						{ header: '출처', name: 'targetSource', hidden: true }
					]
				});
				addGridClickListener(packagingGrid);
			} else {
				packagingGrid.resetData(packagingData);
			}
		} catch (error) {
			console.error('Fetch error:', error);
		}
	}

	function addGridClickListener(grid) {
		grid.on('click', async (ev) => {
			if (typeof ev.rowKey !== 'undefined') {
				selectedTargetData = grid.getRow(ev.rowKey);

				document.getElementById('modalProductName').value = selectedTargetData.targetName;
				document.getElementById('modalLotId').value = selectedTargetData.lotId;
				document.getElementById('modalInspectionType').value = selectedTargetData.inspectionTypeName;

				const criteriaFieldsContainer = document.getElementById('criteriaFields');
				criteriaFieldsContainer.innerHTML = '';

				if (selectedTargetData.targetSource === 'Incoming') {
					const fieldDiv = document.createElement('div');
					fieldDiv.className = 'form-group';
					fieldDiv.innerHTML = `
	                    <label>총 입고 예정 수량: ${selectedTargetData.quantity}</label><br>
	                    <label>합격 수량</label>
	                    <input type="number" id="acceptedCount" class="form-control" placeholder="합격 수량 입력" required>
	                    <label>불량 수량</label>
	                    <input type="number" id="defectiveCount" class="form-control" placeholder="불량 수량 입력" required>
	                    <div id="countWarning" class="text-danger mt-2" style="display:none;">입력된 수량의 합이 총 입고 예정 수량과 일치하지 않습니다.</div>
	                `;
					criteriaFieldsContainer.appendChild(fieldDiv);

					// 불량 수량 입력 필드에 이벤트 리스너 추가
					document.getElementById('defectiveCount').addEventListener('input', async (e) => {
						const defectiveCount = parseInt(e.target.value) || 0;
						const defectFields = document.getElementById('defectFields');

						if (defectiveCount > 0 && !defectFields) {
							// 불량 유형과 사유를 모두 가져오는 API 호출
							const defectCodeResponse = await fetch('/quality/api/defect-codes');
							const defectCodes = await defectCodeResponse.json();

							const defectDiv = document.createElement('div');
							defectDiv.id = 'defectFields';
							defectDiv.innerHTML = `
	                            <hr>
	                            <h6>불량 정보</h6>
	                            <div class="form-group">
	                                <label for="defectType">불량 사유</label>
	                                <select id="defectType" class="form-control">
	                                    <option value="">선택</option>
	                                    ${defectCodes.map(code => `<option value="${code.comDtId}">${code.comDtNm}</option>`).join('')}
	                                </select>
	                            </div>
	                        `;
							criteriaFieldsContainer.appendChild(defectDiv);
						} else if (defectiveCount === 0 && defectFields) {
							criteriaFieldsContainer.removeChild(defectFields);
						}
					});
				} else if (selectedTargetData.targetSource === 'WorkOrder') {
					// 공정 검사 & 포장 검사 (치수 및 육안 검사)
					const processId = selectedTargetData.processId;

					if (processId) { 
						let criteriaResponse;
						try {
							criteriaResponse = await fetch(`/quality/api/inspection-item/process/${processId}`);
							if (!criteriaResponse.ok) {
								throw new Error('검사 기준 로드 실패');
							}
							const criteriaList = await criteriaResponse.json();

							if (criteriaList && criteriaList.length > 0) {
								criteriaList.forEach(criteria => {
									const fieldDiv = document.createElement('div');
									fieldDiv.className = 'form-group';
									fieldDiv.innerHTML = `
					                                    <label>${criteria.itemName} (${criteria.methodName})</label>
					                                    <input type="number" step="0.01" class="form-control measurement-input" 
					                                        data-item-id="${criteria.itemId}" data-tolerance="${criteria.toleranceValue}" data-standard="${criteria.standardValue}" data-unit="${criteria.unit}" 
					                                        placeholder="실측값" required>
					                                    <small class="form-text text-muted">기준값: ${criteria.standardValue} ${criteria.unit}, 허용 공차: ${criteria.toleranceValue} ${criteria.unit}</small>
					                                    <input type="text" class="form-control result-input" readonly placeholder="결과">
					                                `;
									criteriaFieldsContainer.appendChild(fieldDiv);
								});
								document.querySelectorAll('.measurement-input').forEach(input => {
									input.addEventListener('input', updateResult);
								});
							} else {
								criteriaFieldsContainer.innerHTML = `<p class="text-danger">해당 검사 기준이 없습니다.</p>`;
							}
						} catch (error) {
							console.error('Fetch error:', error);
							criteriaFieldsContainer.innerHTML = `<p class="text-danger">검사 기준을 불러오는 중 오류가 발생했습니다.</p>`;
						}
					} else {
						criteriaFieldsContainer.innerHTML = `<p class="text-danger">유효한 공정 ID가 없습니다.</p>`;
					}
				}

				$('#inspectionModal').modal('show');
			}
		});
	}

	function updateResult(event) {
		const input = event.target;
		const measurement = parseFloat(input.value);
		const tolerance = parseFloat(input.dataset.tolerance);
		const standard = parseFloat(input.dataset.standard); // 기준값
		const resultInput = input.closest('.form-group').querySelector('.result-input');

		if (isNaN(measurement)) {
			resultInput.value = '';
			resultInput.style.color = 'black';
			return;
		}

		// 기준값과 허용 공차를 이용한 치수 검사
		// 최소 허용값 = 기준값 - 허용 공차
		// 최대 허용값 = 기준값 + 허용 공차
		const min = standard - tolerance;
		const max = standard + tolerance;

		// 허용 공차가 0인 경우 (육안 검사 등)
		if (tolerance === 0) {
			if (measurement === standard) {
				resultInput.value = '합격';
				resultInput.style.color = 'blue';
			} else {
				resultInput.value = '불합격';
				resultInput.style.color = 'red';
			}
		} else {
			if (measurement >= min && measurement <= max) {
				resultInput.value = '합격';
				resultInput.style.color = 'blue';
			} else {
				resultInput.value = '불합격';
				resultInput.style.color = 'red';
			}
		}
	}

	document.getElementById('registerBtn').addEventListener('click', async () => {
		let registrationData = {};
		let apiUrl = '';

		if (selectedTargetData.targetSource === 'Incoming') {
			// 수입 검사 (in_count 로직)
			const expectedCount = selectedTargetData.quantity;
			const acceptedCount = parseInt(document.getElementById('acceptedCount').value) || 0;
			const defectiveCount = parseInt(document.getElementById('defectiveCount').value) || 0;
			const totalActualCount = acceptedCount + defectiveCount;

			if (totalActualCount !== expectedCount) {
				document.getElementById('countWarning').style.display = 'block';
				return;
			} else {
				document.getElementById('countWarning').style.display = 'none';
			}

			registrationData = {
				targetId: selectedTargetData.targetId,
				acceptedCount: acceptedCount, // 합격 수량
				defectiveCount: defectiveCount, // 불량 수량
				lotId: selectedTargetData.lotId || '',
				inspectionType: selectedTargetData.inspectionType, // 검사 유형
				remarks: document.getElementById('modalRemarks').value,
				materialId: selectedTargetData.materialId
			};

			// 불량 수량이 있을 경우, 선택된 불량 유형과 비고를 데이터에 추가
			if (defectiveCount > 0) {
				const defectType = document.getElementById('defectType').value;
				registrationData.defectType = defectType;
			}
			apiUrl = '/quality/api/verify-incoming-count';

		} else if (selectedTargetData.targetSource === 'WorkOrder') {
			// 공정 검사 (기존 로직)
			const measurementInputs = document.querySelectorAll('.measurement-input');
			const results = [];
			let allValid = true;

			measurementInputs.forEach(input => {
				const measurement = parseFloat(input.value);
				const resultInput = input.closest('.form-group').querySelector('.result-input');

				if (isNaN(measurement)) {
					allValid = false;
					return;
				}

				const resultValue = (resultInput.value === '합격') ? 'Y' : 'N';

				results.push({
					itemId: input.dataset.itemId,
					measurement: measurement,
					result: resultValue
				});
			});

			if (!allValid) {
				alert('모든 실측값을 올바르게 입력해주세요.');
				return;
			}

			registrationData = {
				targetSource: selectedTargetData.targetSource,
				targetId: selectedTargetData.targetId,
				lotId: selectedTargetData.lotId || '',
				inspectionType: selectedTargetData.inspectionType,
				remarks: document.getElementById('modalRemarks').value,

				productId: selectedTargetData.productId,
				processId: selectedTargetData.processId,

				inspectionResults: results
			};
			apiUrl = '/quality/api/register-inspection-result';

		} else {
			alert('유효하지 않은 검사 유형입니다.');
			return;
		}

		// 공통된 API 호출 로직
		try {
			const response = await fetch(apiUrl, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
					[header]: token
				},
				body: JSON.stringify(registrationData)
			});
			const result = await response.json();
			if (result.success) {
				alert(result.message);
				$('#inspectionModal').modal('hide');
				loadHistoryData();
				loadTargetData();
			} else {
				alert('등록 실패: ' + result.message);
			}
		} catch (error) {
			console.error('Registration failed:', error);
			alert('등록 중 오류 발생');
		}
	});

	loadHistoryData();
	loadTargetData();
});