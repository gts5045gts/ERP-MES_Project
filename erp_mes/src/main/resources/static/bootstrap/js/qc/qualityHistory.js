document.addEventListener('DOMContentLoaded', function() {
	// CSRF 토큰
	const token = document.querySelector('meta[name="_csrf"]').content;
	const header = document.querySelector('meta[name="_csrf_header"]').content;

	// --- 상단 그리드 (품질검사 이력) ---
	const historyGrid = new tui.Grid({
		el: document.getElementById('historyGrid'),
		// 서버에서 데이터를 가져오도록 API 설정
		data: {
			api: {
				readData: { url: '/quality/api/history-list', method: 'GET' }
			},
			contentType: 'application/json'
		},
		columns: [
			{ header: '검사ID', name: 'inspectionId' },
			{ header: '검사유형', name: 'inspectionTypeName' },
			{ header: '자재명/제품명', name: 'displayTargetName' },
			{ header: '검사일자', name: 'inspectionDate' },
			{ header: '검사자', name: 'empName' },
			{ header: '로트번호', name: 'lotId' },
			{ header: '검사결과', name: 'result' },
			{ header: '비고', name: 'remarks' }
		],
		rowHeaders: ['rowNum'],
		bodyHeight: 'auto' // 무한 스크롤 활성화
	});
    
	function showInspectionDetailModal(detailData) {
	    if (!detailData) {
	        alert('상세 정보가 유효하지 않습니다.');
	        return;
	    }

		const isIncoming = detailData.inspectionType === 'QC001';

		// HTML 모달 요소에 데이터 매핑
		document.getElementById('detailInspectionId').value = detailData.inspectionId || '';
		document.getElementById('detailTargetName').value = detailData.targetName || '';
	    
	    // 1. 데이터 설정 (값 자체는 DTO에 맞게 설정)
	    document.getElementById('detailTargetId').value = detailData.targetId || 'N/A'; // 작업지시 ID / 자재 코드
	    document.getElementById('detailPlanId').value = detailData.planId || 'N/A'; // 생산 계획 ID

		document.getElementById('detailInspectionType').value = detailData.inspectionTypeName || detailData.inspectionType || '';
		document.getElementById('detailLotId').value = detailData.lotId || '';
		// 불량 사유
		const defectReasonElement = document.getElementById('detailDefectReason');
		defectReasonElement.value = detailData.defectReason || '(불량 사유 미기재 또는 해당 없음)';
		// 비고
		document.getElementById('detailRemarks').value = detailData.remarks || '(비고 미기재)';
	    // 이 필드들은 HTML에서 해당 ID를 가진 input의 부모 (예: .form-group)로 접근해야 합니다.
	    const targetIdContainer = document.getElementById('detailTargetId').closest('.form-group');
	    const planIdContainer = document.getElementById('detailPlanId').closest('.form-group');
	    
	    if (targetIdContainer) {
	        // 수입 검사(QC001)일 경우 숨김 처리
	        targetIdContainer.style.display = isIncoming ? 'none' : 'block';
	    }
	    if (planIdContainer) {
	        // 수입 검사(QC001)일 경우 숨김 처리
	        planIdContainer.style.display = isIncoming ? 'none' : 'block';
	    }
		// 모달 표시
		$('#detailModal').modal('show');  
	}

	// 기존 loadHistoryData 함수를 제거하고, 필터링 로직만 수정
	function filterHistoryData() {
		const filterType = document.getElementById('historyFilterType').value;
		const filterProduct = document.getElementById('historyFilterProduct').value;
		const filterLotId = document.getElementById('historyFilterLotId').value;

		// readData 메서드로 필터링 파라미터 전달
		historyGrid.readData(1, {
			filterType: filterType,
			filterProduct: filterProduct,
			filterLotId: filterLotId
		});
	}
	document.getElementById('historySearchBtn').addEventListener('click', filterHistoryData);

	// --- 검사 대기 목록 라디오 버튼 로직 ---
	const radioButtons = document.querySelectorAll('input[name="inspectionTypeRadio"]');
	const gridContainers = document.querySelectorAll('.inspection-grid-container');

	radioButtons.forEach(radio => {
		radio.addEventListener('change', function() {
			const selectedValue = this.value;
			gridContainers.forEach(container => {
				container.style.display = 'none';
			});
			const selectedContainer = document.getElementById(`${selectedValue}GridContainer`);
			if (selectedContainer) {
				selectedContainer.style.display = 'block';
				// 그리드 레이아웃 재계산
				if (selectedValue === 'incoming') {
					incomingGrid.refreshLayout();
				} else if (selectedValue === 'process') {
					processGrid.refreshLayout();
				}
			}
		});
	});

	// --- 하단 그리드 (검사 대기 목록) ---
	let incomingGrid, processGrid;
	let selectedTargetData = null;
    // checkIfLastProcess 함수는 서버에서 정의되어 있다고 가정합니다.
    // async function checkIfLastProcess(targetId, proSeq) { ... } 

	async function loadTargetData() {
		// incomingGrid 인스턴스가 없으면 생성
		if (!incomingGrid) {
			incomingGrid = new tui.Grid({
				el: document.getElementById('incomingGrid'),
				data: {
					api: {
						readData: { url: '/quality/api/incoming-targets', method: 'GET' }
					},
					contentType: 'application/json'
				},
				columns: [
					{ header: 'ID', name: 'targetId' },
					{ header: '자재명', name: 'targetName' },
					{ header: '로트번호', name: 'lotId' },
					{ header: '수량', name: 'quantity' },
					{ header: '검사유형', name: 'inspectionTypeName' },
					{ header: '출처', name: 'targetSource', hidden: true }
				],
				rowHeaders: ['rowNum'],
				bodyHeight: 'auto'
			});
			addGridClickListener(incomingGrid);
		} else {
			incomingGrid.readData(); // 데이터 새로고침
		}
		
		// processGrid 인스턴스가 없으면 생성
		if (!processGrid) {
			processGrid = new tui.Grid({
				el: document.getElementById('processGrid'),
				data: {
					api: {
						readData: { url: '/quality/api/process-targets', method: 'GET' }
					},
					contentType: 'application/json'
				},
				columns: [
					{ header: 'ID', name: 'targetId' },
					{ header: '제품명', name: 'targetName' },
					{ header: '로트번호', name: 'lotId' },
					{ header: '계획 수량', name: 'quantity' },
					{ header: '양품 수량', name: 'goodQty' },
					{ header: '불량 수량', name: 'defectQty' },
					{ header: '검사유형', name: 'inspectionTypeName' },
					{ header: '출처', name: 'targetSource', hidden: true },
					{ header: '공정 순서', name: 'proSeq', hidden: true }
				],
				rowHeaders: ['rowNum'],
				bodyHeight: 'auto'
			});
			addGridClickListener(processGrid);
		} else {
			processGrid.readData(); // 데이터 새로고침
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

				if (selectedTargetData.targetSource === 'WorkOrder') {
                    const totalGoodQuantity = selectedTargetData.goodQty || 0;

					const fieldDiv = document.createElement('div');
					fieldDiv.className = 'form-group';
					fieldDiv.innerHTML = `
						<label>총 검사 수량 (생산 실적 합계): ${totalGoodQuantity}</label><br>
						<label>합격 수량</label>
						<input type="number" id="acceptedCount" class="form-control" placeholder="합격 수량 입력" required>
						<label>불량 수량</label>
						<input type="number" id="defectiveCount" class="form-control" placeholder="불량 수량 입력" required>
						<div id="countWarning" class="text-danger mt-2" style="display:none;">입력된 수량의 합이 총 검사 수량 (${totalGoodQuantity})과 일치하지 않습니다.</div>
					`;
					criteriaFieldsContainer.appendChild(fieldDiv);

                    // 불량 수량 입력 시 불량 정보 필드 표시 로직만 유지
					document.getElementById('defectiveCount').addEventListener('input', async (e) => {
						const defectiveCount = parseInt(e.target.value) || 0;
						const defectFields = document.getElementById('defectFields');

						if (defectiveCount > 0 && !defectFields) {
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

					$('#inspectionModal').modal('show');
				}
				else if (selectedTargetData.targetSource === 'Incoming') {
					// 수입 검사 로직 (기존과 동일)
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

					document.getElementById('defectiveCount').addEventListener('input', async (e) => {
						const defectiveCount = parseInt(e.target.value) || 0;
						const defectFields = document.getElementById('defectFields');

						if (defectiveCount > 0 && !defectFields) {
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
					$('#inspectionModal').modal('show');
				}
				else {
					alert('유효하지 않은 검사 유형입니다.');
				}
			}
		});
	}

	document.getElementById('registerBtn').addEventListener('click', async () => {
		let registrationData = {};
		let apiUrl = '';

        // 합격/불량 수량 공통 변수
        const acceptedCount = parseInt(document.getElementById('acceptedCount').value) || 0;
		const defectiveCount = parseInt(document.getElementById('defectiveCount').value) || 0;
		const totalActualCount = acceptedCount + defectiveCount;
        const remarks = document.getElementById('modalRemarks').value;
        const defectType = document.getElementById('defectType') ? document.getElementById('defectType').value : null;


		if (selectedTargetData.targetSource === 'Incoming') {
			const expectedCount = selectedTargetData.quantity;

			if (totalActualCount !== expectedCount) {
				document.getElementById('countWarning').style.display = 'block';
				return;
			} else {
				document.getElementById('countWarning').style.display = 'none';
			}

			registrationData = {
				targetId: selectedTargetData.targetId,
				acceptedCount: acceptedCount,
				defectiveCount: defectiveCount,
				productId: selectedTargetData.productId,
				processId: selectedTargetData.processId,
				lotId: selectedTargetData.lotId || '',
				inspectionType: selectedTargetData.inspectionType,
				remarks: remarks,
				materialId: selectedTargetData.materialId
			};

			if (defectiveCount > 0) {
				registrationData.defectType = defectType;
			}
			apiUrl = '/quality/api/verify-incoming-count';

		} else if (selectedTargetData.targetSource === 'WorkOrder') {
			
            const totalGoodQuantity = selectedTargetData.goodQty || 0;
            const expectedCount = totalGoodQuantity; // 검증 기준은 총 생산 수량

			if (totalActualCount !== expectedCount) { // 조건이 참(true)이 되어야 오류가 발생합니다.
			    document.getElementById('countWarning').style.display = 'block'; // 오류 메시지 표시
			    return;
			}

            registrationData = {
                targetSource: selectedTargetData.targetSource,
                targetId: selectedTargetData.targetId, // WORK_ORDER_ID
                acceptedCount: acceptedCount,
                defectiveCount: defectiveCount,
                lotId: selectedTargetData.lotId || '',
                inspectionType: selectedTargetData.inspectionType,
                remarks: remarks,
                productId: selectedTargetData.productId,
                processId: selectedTargetData.processId,
                proSeq: selectedTargetData.proSeq
            };

            if (defectiveCount > 0) {
                registrationData.defectType = defectType;
            }
            
            // 이 API는 중간 공정이든 최종 공정이든 WORK_ORDER의 검사 결과를 처리합니다.
            // 서버에서 isLastProcess를 체크하여 상태 업데이트를 분기합니다.
            apiUrl = '/quality/api/register-process-inspection-result'; 

		} else {
			alert('유효하지 않은 검사 유형입니다.');
			return;
		}
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
				historyGrid.readData(); // historyGrid 새로고침
				incomingGrid.readData(); // incomingGrid 새로고침
				processGrid.readData(); // processGrid 새로고침
			} else {
				alert('등록 실패: ' + result.message);
			}
		} catch (error) {
			console.error('Registration failed:', error);
			alert('등록 중 오류 발생');
		}
	});
	
    // DOMContentLoaded 내부에서 호출되도록 재배치합니다.
    function setupHistoryGridClickListener() {
        historyGrid.on('click', async (ev) => {
            if (typeof ev.rowKey !== 'undefined') {
                const rowData = historyGrid.getRow(ev.rowKey);
                const inspectionId = rowData.inspectionId; // 검사 ID
                
                try {
                    const response = await fetch(`/quality/api/inspection-detail/${inspectionId}`);
                    
                    // HTTP 상태 코드 확인 (500 에러 등을 걸러냅니다)
                    if (!response.ok) {
                        throw new Error('서버 응답 오류: ' + response.status);
                    }
                    
                    const detailData = await response.json();
                    
                    showInspectionDetailModal(detailData); 
                    
                } catch (error) {
                    console.error('Failed to fetch inspection detail:', error);
                    alert('검사 상세 정보를 불러오는 데 실패했습니다.');
                }
            }
        });
    }


	// 페이지 로드 시 초기 데이터 로드
	historyGrid.readData();
	loadTargetData();
    setupHistoryGridClickListener(); 
});