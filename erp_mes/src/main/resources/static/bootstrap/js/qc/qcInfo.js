document.addEventListener('DOMContentLoaded', function() {
    // CSRF 토큰
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    // HTML에 선언된 pageData 객체에서 데이터를 가져옵니다.
    const { processes, materials, inspectionFMs, units } = pageData;

    // HTML 엘리먼트 참조
    const targetTypeRadios = document.querySelectorAll('input[name="targetType"]');
    const materialDropdownContainer = document.getElementById('materialDropdownContainer');
    const processDropdownContainer = document.getElementById('processDropdownContainer');
    const targetCodeMaterial = document.getElementById('targetCodeMaterial');
    const targetCodeProcess = document.getElementById('targetCodeProcess');
    const standardModal = document.getElementById('standardModal');
    const standardModalLabel = document.getElementById('standardModalLabel');
    const inspectionModal = document.getElementById('inspectionModal');
    const inspectionModalLabel = document.getElementById('inspectionModalLabel');

    // 전역 변수로 각 테이블의 현재 모드와 선택된 항목 ID를 관리
    let currentItemMode = 'register'; // 오른쪽 테이블 모드
    let selectedItemId = null; // 오른쪽 테이블 선택된 ID
    let currentFmMode = 'register'; // 왼쪽 테이블 모드
    let selectedFmId = null; // 왼쪽 테이블 선택된 ID
    
    // 왼쪽 테이블 (dataTable1) 검색 기능
    const searchInput1 = document.querySelector('#dataTable1').closest('.card-body').querySelector('input[type="text"]');
    const searchBtn1 = document.querySelector('#dataTable1').closest('.card-body').querySelector('#searchBtn1');
    const tableBody1 = document.querySelector('#dataTable1 tbody');
    const rows1 = tableBody1.querySelectorAll('tr');

    const filterTable1 = () => {
        const searchText = searchInput1.value.toLowerCase();
        rows1.forEach(row => {
            const rowData = Array.from(row.cells).map(cell => cell.textContent.toLowerCase()).join(' ');
            row.style.display = rowData.includes(searchText) ? '' : 'none';
        });
    };
    searchBtn1.addEventListener('click', filterTable1);
    searchInput1.addEventListener('keyup', (event) => {
        if (event.key === 'Enter') { filterTable1(); }
    });

    // 오른쪽 테이블 (dataTable2) 검색 기능
    const searchInput2 = document.querySelector('#dataTable2').closest('.card-body').querySelector('input[type="text"]');
    const searchBtn2 = document.querySelector('#dataTable2').closest('.card-body').querySelector('#searchBtn2');
    const tableBody2 = document.querySelector('#dataTable2 tbody');
    const rows2 = tableBody2.querySelectorAll('tr');

    const filterTable2 = () => {
        const searchText = searchInput2.value.toLowerCase();
        rows2.forEach(row => {
            const rowData = Array.from(row.cells).map(cell => cell.textContent.toLowerCase()).join(' ');
            row.style.display = rowData.includes(searchText) ? '' : 'none';
        });
    };
    searchBtn2.addEventListener('click', filterTable2);
    searchInput2.addEventListener('keyup', (event) => {
        if (event.key === 'Enter') { filterTable2(); }
    });
    
    // ------------------- 왼쪽 테이블 로직 -------------------

    // 왼쪽 테이블 행 클릭 시 선택 상태 토글 및 ID 저장
    document.getElementById('dataTable1').addEventListener('click', function(event) {
        const row = event.target.closest('tr');
        if (row?.parentNode.tagName === 'TBODY') {
            document.querySelectorAll('#dataTable1 tbody tr.selected').forEach(r => r.classList.remove('selected'));
            row.classList.add('selected');
            selectedFmId = row.dataset.id;
        }
    });

    // '등록' 버튼 클릭 이벤트 (왼쪽 테이블)
    document.querySelector('[data-target="#inspectionModal"]').addEventListener('click', function() {
        currentFmMode = 'register';
        selectedFmId = null;
    });

    // '수정' 버튼 클릭 이벤트 (왼쪽 테이블)
    document.getElementById('editFmBtn').addEventListener('click', function() {
        const selectedRow = document.querySelector('#dataTable1 tbody tr.selected');
        if (!selectedRow) {
            alert('수정할 항목을 선택해주세요.');
            return;
        }
        
        currentFmMode = 'update';
        selectedFmId = selectedRow.dataset.id;
        $('#inspectionModal').modal('show');
    });

    // '삭제' 버튼 클릭 이벤트 (왼쪽 테이블)
    document.getElementById('deleteFmBtn').addEventListener('click', function() {
        const selectedRows = document.querySelectorAll('#dataTable1 tbody tr.selected');
        if (selectedRows.length === 0) { alert('삭제할 항목을 선택해주세요.'); return; }
        if (!confirm('선택된 항목을 정말 삭제하시겠습니까?')) { return; }
        const idsToDelete = Array.from(selectedRows).map(row => row.dataset.id);
        fetch('/quality/fm', {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json', [header]: token },
            body: JSON.stringify(idsToDelete)
        })
        .then(response => response.json())
        .then(data => {
            alert(data.message);
            if (data.success) { window.location.reload(); }
        })
        .catch(error => { console.error('Error:', error); alert('삭제 실패: 서버 연결 또는 응답 오류'); });
    });

    // 왼쪽 모달이 열릴 때 이벤트
	$('#inspectionModal').on('show.bs.modal', function() {
	    document.getElementById('record-form').reset();
	    if (currentFmMode === 'update') {
	        inspectionModalLabel.textContent = '검사 유형별 기준 수정';
	        const selectedRow = document.querySelector('#dataTable1 tbody tr.selected');
	        if (selectedRow) {
	            // data-type-id 속성에서 ID값을 가져와서 사용
	            const inspectionTypeId = selectedRow.dataset.typeId;
	            const itemName = selectedRow.cells[2].textContent;
	            const methodName = selectedRow.cells[3].textContent;

	            // 폼 필드에 값 채우기
	            document.getElementById('inspectionTypeId').value = inspectionTypeId;
	            document.getElementById('itemName_record').value = itemName;
	            document.getElementById('methodName').value = methodName;
	        }
	    } else {
	        inspectionModalLabel.textContent = '검사 유형별 기준 등록';
	    }
	});
    
    // 왼쪽 모달 ('검사 유형별 기준 등록')의 '저장' 버튼 클릭 이벤트
    document.getElementById('saveRecordBtn').addEventListener('click', function() {
        const formData = {
            inspectionFMId: currentFmMode === 'update' ? selectedFmId : null,
            inspectionType: document.getElementById('inspectionTypeId').value,
            itemName: document.getElementById('itemName_record').value,
            methodName: document.getElementById('methodName').value
        };

        const url = '/quality/fm';
        const method = currentFmMode === 'update' ? 'PUT' : 'POST';

        fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json', [header]: token },
            body: JSON.stringify(formData)
        })
        .then(response => response.json())
        .then(data => {
            alert(data.message);
            if (data.success) { window.location.reload(); }
        })
        .catch(error => { console.error('Error:', error); alert('저장 실패: 서버 연결 또는 응답 오류'); });
    });

    // 모달이 닫힐 때 폼 초기화 (왼쪽)
    $('#inspectionModal').on('hidden.bs.modal', function() {
        document.getElementById('record-form').reset();
        document.querySelectorAll('#dataTable1 tbody tr.selected').forEach(r => r.classList.remove('selected'));
        selectedFmId = null;
        currentFmMode = 'register';
    });

    // ------------------- 오른쪽 테이블 로직 -------------------

    // 오른쪽 테이블 행 클릭 시 선택 상태 토글 및 ID 저장
    document.getElementById('dataTable2').addEventListener('click', function(event) {
        const row = event.target.closest('tr');
        if (row?.parentNode.tagName === 'TBODY') {
            document.querySelectorAll('#dataTable2 tbody tr.selected').forEach(r => r.classList.remove('selected'));
            row.classList.add('selected');
            selectedItemId = row.dataset.id;
        }
    });
    
    // '등록' 버튼 클릭 이벤트 (오른쪽 테이블)
    document.querySelector('[data-target="#standardModal"]').addEventListener('click', function() {
        currentItemMode = 'register';
        selectedItemId = null;
        standardModalLabel.textContent = '검사 항목 등록 및 공차 설정';
        document.getElementById('item-form').reset();
        materialDropdownContainer.style.display = 'block';
        processDropdownContainer.style.display = 'none';
        document.getElementById('targetTypeMaterial').checked = true;
    });
    
    // '수정' 버튼 클릭 이벤트 (오른쪽 테이블)
    document.getElementById('editItemBtn').addEventListener('click', function() {
        const selectedRow = document.querySelector('#dataTable2 tbody tr.selected');
        if (!selectedRow) {
            alert('수정할 항목을 선택해주세요.');
            return;
        }
    
        currentItemMode = 'update';
        selectedItemId = selectedRow.dataset.id;
        standardModalLabel.textContent = '검사 항목 수정 및 공차 설정';
        $('#standardModal').modal('show');
    });

    // '삭제' 버튼 클릭 이벤트 (오른쪽 테이블)
    document.getElementById('deleteItemBtn').addEventListener('click', function() {
        const selectedRows = document.querySelectorAll('#dataTable2 tbody tr.selected');
        if (selectedRows.length === 0) { alert('삭제할 항목을 선택해주세요.'); return; }
        if (!confirm('선택된 항목을 정말 삭제하시겠습니까?')) { return; }
        const idsToDelete = Array.from(selectedRows).map(row => row.dataset.id);
        fetch('/quality/item', {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json', [header]: token },
            body: JSON.stringify(idsToDelete)
        })
        .then(response => response.json())
        .then(data => {
            alert(data.message);
            if (data.success) { window.location.reload(); }
        })
        .catch(error => { console.error('Error:', error); alert('삭제 실패: 서버 연결 또는 응답 오류'); });
    });

    // 검사 대상 라디오 버튼 변경 이벤트 리스너
    targetTypeRadios.forEach(radio => {
        radio.addEventListener('change', (event) => {
            const selectedValue = event.target.value;
            materialDropdownContainer.style.display = 'none';
            processDropdownContainer.style.display = 'none';
            
            if (selectedValue === 'material') {
                materialDropdownContainer.style.display = 'block';
            } else if (selectedValue === 'process') {
                processDropdownContainer.style.display = 'block';
            }
        });
    });

    // 오른쪽 모달이 열릴 때 이벤트
    $('#standardModal').on('show.bs.modal', function() {
        const inspectionFMDropdown = document.getElementById('inspectionFMId');
        const unitDropdown = document.getElementById('unit');

        // 초기화
        inspectionFMDropdown.innerHTML = '<option value="">선택</option>';
        unitDropdown.innerHTML = '<option value="">선택</option>';
        document.getElementById('item-form').reset();
        
        // 공정 드롭다운 채우기
        const targetCodeProcess = document.getElementById('targetCodeProcess');
        targetCodeProcess.innerHTML = '<option value="">선택</option>';
        if (processes && processes.length > 0) {
            processes.forEach(proc => {
                const option = document.createElement('option');
                option.value = proc.proId;
                option.textContent = proc.proNm;
                targetCodeProcess.appendChild(option);
            });
        }
    
        // 자재 드롭다운 채우기
        const targetCodeMaterial = document.getElementById('targetCodeMaterial');
        targetCodeMaterial.innerHTML = '<option value="">선택</option>';
        if (materials && materials.length > 0) {
            materials.forEach(mat => {
                const option = document.createElement('option');
                option.value = mat.materialId;
                option.textContent = mat.materialName;
                targetCodeMaterial.appendChild(option);
            });
        }
        
        // 검사 유형 드롭다운 채우기
		const uniqueFMs = new Set();
		if (inspectionFMs && inspectionFMs.length > 0) {
		    inspectionFMs.forEach(fm => {
		        const key = fm.inspectionFMId;
		        if (!uniqueFMs.has(key)) {
		            const option = document.createElement('option');
		            option.value = fm.inspectionFMId;
		            // inspectionTypeName (이름)과 itemName을 조합하여 표시
		            option.textContent = `${fm.inspectionTypeName} - ${fm.itemName}`;
		            inspectionFMDropdown.appendChild(option);
		            uniqueFMs.add(key);
		        }
		    });
		}

        // 단위 드롭다운 채우기
        if (units && units.length > 0) {
            units.forEach(unit => {
                const option = document.createElement('option');
                option.value = unit.comDtNm;
                option.textContent = unit.comDtNm;
                unitDropdown.appendChild(option);
            });
        }

        // 수정 모드일 경우 데이터 채우기
        if (currentItemMode === 'update') {
            const selectedRow = document.querySelector('#dataTable2 tbody tr.selected');
            if (selectedRow) {
                const cells = selectedRow.querySelectorAll('td');
                const materialOrProName = cells[1].textContent;
                const toleranceValue = cells[4].textContent;
                const unit = cells[5].textContent;

                document.getElementById('itemId_hidden').value = selectedItemId;
                document.getElementById('inspectionFMId').value = cells[0].textContent;
                document.getElementById('toleranceValue').value = toleranceValue;
                document.getElementById('unit').value = unit;

                const materialItem = materials.find(m => m.materialName === materialOrProName);
                const proItem = processes.find(p => p.proNm === materialOrProName);

                if(materialItem) {
                    document.getElementById('targetTypeMaterial').checked = true;
                    materialDropdownContainer.style.display = 'block';
                    processDropdownContainer.style.display = 'none';
                    document.getElementById('targetCodeMaterial').value = materialItem.materialId;
                } else if (proItem) {
                    document.getElementById('targetTypeProcess').checked = true;
                    materialDropdownContainer.style.display = 'none';
                    processDropdownContainer.style.display = 'block';
                    document.getElementById('targetCodeProcess').value = proItem.proId;
                }
            }
        }
    });

    // 모달이 닫힐 때 폼 초기화 (오른쪽)
    $('#standardModal').on('hidden.bs.modal', function() {
        document.getElementById('item-form').reset();
        document.querySelectorAll('#dataTable2 tbody tr.selected').forEach(r => r.classList.remove('selected'));
        selectedItemId = null;
        currentItemMode = 'register';
    });

    // 오른쪽 모달 ('검사 항목 등록 및 공차 설정')의 '저장' 버튼 클릭 이벤트
    document.getElementById('saveItemBtn').addEventListener('click', function() {
        const targetType = document.querySelector('input[name="targetType"]:checked').value;
        let targetId = null;

        if (targetType === 'material') {
            targetId = document.getElementById('targetCodeMaterial').value;
        } else if (targetType === 'process') {
            targetId = document.getElementById('targetCodeProcess').value;
        }
        if (targetId === "") {
            targetId = null;
        }

        const formData = {
            itemId: currentItemMode === 'update' ? selectedItemId : null,
            materialId: targetType === 'material' ? targetId : null,
            proId: targetType === 'process' ? targetId : null,
            inspectionFMId: document.getElementById('inspectionFMId').value,
            toleranceValue: document.getElementById('toleranceValue').value,
            unit: document.getElementById('unit').value
        };
        

        const url = '/quality/item';
        const method = currentItemMode === 'update' ? 'PUT' : 'POST';

        fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json', [header]: token },
            body: JSON.stringify(formData)
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(error => { throw new Error(error.message || '서버 응답 오류'); });
            }
            return response.json();
        })
        .then(data => {
            alert(data.message);
            if (data.success) { window.location.reload(); }
        })
        .catch(error => { console.error('Error:', error); alert(`저장 실패: ${error.message}`); });
    });
});